<template>
	<el-dialog width="800px" :close-on-click-modal="false" v-model="visible" :show-close="false" center>
		<div id="pdfDom">
			<img :src="qrCodeBase64" class="qrCode">
			<h2 class="title">立&nbsp;&nbsp;&nbsp;替&nbsp;&nbsp;&nbsp;金&nbsp;&nbsp;&nbsp;精&nbsp;&nbsp;&nbsp;算</h2>
			<div class="top-info-container">
				<span class="info">部門：{{ dept }}</span>
				<span class="info">申込者：{{ name }}</span>
				<span class="info">申込日付：{{ date }}</span>
			</div>
			<div class="reim-table-container">
				<table class="reim-table">
					<tr align="center">
						<th width="30%">項目</th>
						<th width="34%">備考</th>
						<th width="20%">タイプ</th>
						<th width="16%">金額</th>
					</tr>
					<tr align="center" v-for="one in content">
						<td align="left">{{one.title}}</td>
						<td align="left">{{one.desc}}</td>
						<td>{{one.type}}</td>
						<td align="left">{{one.money!=""?one.money+"円":""}}</td>
					</tr>
					<tr>
						<th align="center">合計</th>
						<td colspan="3">{{ amount }}円</td>
					</tr>
					<tr>
						<td colspan="5">
							<div class="info-container">
								<span class="info">借金金額：{{ anleihen }}円</span>
								<span class="info">精算金額：{{ money_1 }}円</span>
								<span class="info">追加金額：{{ money_2 }}円</span>
							</div>
						</td>
					</tr>
				</table>
			</div>
			<div class="bottom-info-container"></div>
			<div class="bottom-info-container">
				<span class="sig">財務部門：</span>
				<span class="sig">複眼：</span>
				<span class="sig">出纳：</span>
				<span class="sig">报销人：</span>
			</div>
		</div>
		<template #footer>
			<span class="dialog-footer">
				<el-button type="primary" @click="getPdf('#pdfDom')" size="medium">PDFダウンロード</el-button>
				<el-button size="medium" @click="cancel()">キャンセル</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script>
export default {
	data: function() {
		return {
			visible: false,
			htmlTitle: '立替金精算',
			dept: null,
			name: null,
			date: null,
			amount: null,
			balance: null,
			anleihen: null,
			money_1: 0,
			money_2: 0,
			content:[],
			qrCodeBase64:null
		};
	},
	methods: {
		init: function(id) {
			
			let _this = this;
			_this.visible = true;
			_this.dept = null;
			_this.name = null;
			_this.date = null;
			_this.amount = null;
			_this.balance = null;
			_this.anleihen = null;
			_this.money_1 = 0;
			_this.money_2 = 0;
			_this.content=[]
			_this.$http('reim/searchReimById', 'POST', { id: id }, true, function(resp) {
				_this.dept = resp.dept;
				_this.name = resp.name;
				_this.date = resp.date;
				_this.amount = resp.amount;
				_this.balance = resp.balance;
				_this.anleihen = resp.anleihen;
				if (_this.anleihen > _this.amount) {
					_this.money_1 = _this.anleihen - _this.amount;
				} else if (_this.anleihen < _this.amount) {
					_this.money_2 = _this.amount - _this.anleihen;
				}
				let content = JSON.parse(resp.content);
				let temp = 5 - content.length
				for (let i = 0; i < temp; i++) {
					content.push({title:"",desc:"",type:"",money:""})
				}
				_this.content = content
				_this.qrCodeBase64 = resp.qrCodeBase64
			});
		},
		// キャンセル
		cancel: function() {
			this.visible = false;
		},

		smalltoBIG: function(n) {
			var fraction = ['角', '分'];
			var digit = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'];
			var unit = [['元', '万', '亿'], ['', '拾', '佰', '仟']];
			var head = n < 0 ? '欠' : '';
			n = Math.abs(n);

			var s = '';

			for (var i = 0; i < fraction.length; i++) {
				s += (digit[Math.floor(n * 10 * Math.pow(10, i)) % 10] + fraction[i]).replace(/零./, '');
			}
			s = s || '整';
			n = Math.floor(n);

			for (var i = 0; i < unit[0].length && n > 0; i++) {
				var p = '';
				for (var j = 0; j < unit[1].length && n > 0; j++) {
					p = digit[n % 10] + unit[1][j] + p;
					n = Math.floor(n / 10);
				}
				s = p.replace(/(零.)*零$/, '').replace(/^$/, '零') + unit[0][i] + s;
			}
			return (
				head +
				s
					.replace(/(零.)*零元/, '元')
					.replace(/(零.)+/g, '零')
					.replace(/^整$/, '零元整')
			);
		}
	}
};
</script>

<style lang="less" scoped="scoped">
@import url('reim_pdf.less');
</style>

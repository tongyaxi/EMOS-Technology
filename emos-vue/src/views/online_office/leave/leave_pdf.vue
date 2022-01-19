<template>
	<el-dialog width="780px" :close-on-click-modal="false" v-model="visible" :show-close="false">
		<div id="pdfDom">
			<h2 class="title">休暇PDF</h2>
			<table class="leave-table">
				<tr align="center">
					<td width="14%">氏名</td>
					<td width="16%">{{ name }}</td>
					<td width="14%">性別</td>
					<td width="16%">{{ sex }}</td>
					<td width="14%">部門</td>
					<td>{{ dept }}</td>
				</tr>
				<tr>
					<td align="center">タイプ</td>
					<td colspan="5">{{ type }}</td>
				</tr>
				<tr>
					<td align="center">時間帯</td>
					<td colspan="5">{{ start }}&nbsp;&nbsp;~&nbsp;&nbsp;{{ end }}</td>
				</tr>
				<tr>
					<td align="center">理由</td>
					<td colspan="5">{{ reason }}</td>
				</tr>
				<tr>
					<td align="center">署名</td>
					<td colspan="2"></td>
					<td align="center">HR押印</td>
					<td colspan="3"></td>
				</tr>
			</table>
		</div>
		<template #footer>
			<span class="dialog-footer">
				<el-button type="primary" @click="getPdf('#pdfDom')" size="medium">ダウンロード</el-button>
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
			htmlTitle: '休暇PDF',
			name: null,
			sex: null,
			dept: null,
			reason: null,
			start: null,
			end: null,
			type: null
		};
	},
	methods: {
		
		init: function(id) {
			
			let _this = this;
			_this.visible = true;
			_this.name = null;
			_this.sex = null;
			_this.dept = null;
			_this.reason = null;
			_this.start = null;
			_this.end = null;
			_this.type = null;
			_this.$http('leave/searchLeaveById', 'POST', { id: id }, true, function(resp) {
				_this.name = resp.name;
				_this.sex = resp.sex;
				_this.dept = resp.dept;
				_this.reason = resp.reason;
				if (resp.type == 1) {
					_this.type = '病気のため';
				} else if (resp.type == 2) {
					_this.type = '私用のため';
				}

				_this.start = resp.start;
				_this.end = resp.end;
			});
		},
		cancel: function() {
			this.visible = false;
		}
	}
};
</script>

<style lang="less" scoped="scoped">
@import url('leave_pdf.less');
</style>

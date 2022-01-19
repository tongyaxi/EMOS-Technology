<template>
	<el-dialog title="罰金を支払う" :close-on-click-modal="false" v-model="visible" width="305px" center>
		<img :src="qrCode" class="qrcode" v-if="!result" />
		<div v-if="result" class="pay-success">
			<el-result icon="success" title="決済完了しました."></el-result>
		</div>
		<div class="dialog-footer-style">
			<el-button type="danger" size="medium" v-if="!result" @click="cancelHandle">キャンセル</el-button>
			<el-button type="primary" size="medium" v-if="!result" @click="successHandle">決済完了</el-button>
			<el-button type="primary" size="medium" v-if="result" @click="closeHandle">閉じる</el-button>
		</div>
	</el-dialog>
</template>

<script>
export default {
	data: function() {
		return {
			visible: false,
			dataForm: {
				id: null
			},
			result: false,
			qrCode: null
		};
	},
	methods: {
		
		init: function(id) {
			let _this = this;
			_this.visible = true;
			_this.dataForm.id = id;
			_this.result = false;
			
			// 利用Websocket接收后端推送的付款成功结果
			let token = _this.$cookies.get("token");
			_this.$socket.sendObj({opt:"pay_amect",token:token});
			_this.$options.sockets.onmessage = function(resp){
				let data = resp.data;
				if(data == "決済完了"){
					_this.result = true;
				}
			}
			
			_this.$nextTick(() => {
				_this.$http('amect/createNativeAmectPayOrder', 'POST', {amectId:id}, true, function(resp){
					_this.qrCode = resp.qrCodeBase64;
				});
			});
		},
		// キャンセル
		cancelHandle: function() {
		    this.visible = false;
		},
		// 手動で支払結果を取得する
		successHandle: function() {
		    let _this = this;
		    _this.visible = false;
		    _this.$http('amect/searchNativeAmectPayResult', 'POST', { amectId: _this.dataForm.id }, true, function(resp) {
		        _this.$emit('refreshDataList');
		    });
		},
		// 閉じる
		closeHandle: function() {
		    this.visible = false;
		    this.$emit('refreshDataList');
		}
	}
};
</script>

<style scoped>
.qrCode {
	width: 255px;
	height: 255px;
}
.pay-success {
	width: 255px;
	height: 255px;
}
.dialog-footer-style {
	padding-bottom: 30px;
	padding-top: 10px;
	text-align: center;
}
</style>

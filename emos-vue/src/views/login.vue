<template>
	<div class="page">
		<el-row type="flex" justify="center" align="middle" class="container">
			<el-col :lg="14" :xl="10" class="hidden-md-and-down">
				<el-row class="panel">
					<el-col :span="12">
						<div class="left">
							<img src="../assets/login/logo.png" class="logo" />
							<img src="../assets/login/big-1.png" class="big" />
						</div>
					</el-col>
					<el-col :span="12">
						<div class="right">
							<div class="title-container">
								<h2>オンライン勤務システム</h2>
								<span>(Ver 1.0)</span>
							</div>
							<div v-if="!qrCodeVisible">
								<div class="row">
									<el-input
										v-model="username"
										placeholder="ログインID"
										prefix-icon="el-icon-user"
										clearable
									></el-input>
								</div>
								<div class="row">
									<el-input
										type="password"
										v-model="password"
										placeholder="パスワード"
										prefix-icon="el-icon-lock"
										clearable
									></el-input>
								</div>
								<div class="row">
									<el-button type="primary" class="btn" @click="login">登録</el-button>
								</div>
								<div class="row"><a class="link" @click="changeMode">QRコードから登録</a></div>
							</div>
							<div v-if="qrCodeVisible">
								<div class="qrCode-container">
									<img :src="qrCode" height="153" class="qrCode" />
									<img src="../assets/login/phone.png" height="148" />
								</div>
								<div class="row"><a class="link" @click="changeMode">アカウント登録</a></div>
							</div>
						</div>
					</el-col>
				</el-row>
			</el-col>
		</el-row>
	</div>
</template>

<script>
import 'element-plus/lib/theme-chalk/display.css';
import { isUsername, isPassword } from '../utils/validate.js';
import router from '../router/index.js';
export default {
	data: function() {
		
		return {
			username: 'admin',
			password: '333333',
			qrCodeVisible: false,
			downloadButton: false,
			qrCode: '',
			uuid: null,
			qrCodeTimer: null,
			loginTimer: null
		};
	},

	methods: {
		login: function() {
					
			let _this = this
				
			if(!isUsername(_this.username)) {
				_this.$message({
					message: 'ユーザー名を正しく入力してください。',
					type: 'error',
					duration: 1200
				});
			}else if(!isPassword(_this.password)) {
				_this.$message({
					message: 'パスワードを正しく入力してください。',
					type: 'error',
					duration: 1200
				});
			}else {
				let data = {username: _this.username, password: _this.password}
				_this.$http('user/login', 'POST', data, true, function(response){
					if(response.result) {
						let permissions = response.permissions;
						let token = response.token;
						
						// 存储在storage中，其他页面也可以共享
						localStorage.setItem('permissions', permissions);
						localStorage.setItem('token', token);
						// 路由跳转至Home.vue的name(别名)
						router.push({name: 'Home'});
					} else {
						_this.$message({
							message: 'ログインに失敗しました。',
							type: 'error',
							duration: 1200
						});
					}
				});
			}
		},
		changeMode: function() {
			let that = this;
			that.qrCodeVisible = !that.qrCodeVisible;
			//加载二维码图片
			if (that.qrCodeVisible) {
				that.loadQRCode();
				//创建刷新二维码定时器
				that.qrCodeTimer = setInterval(function() {
					that.loadQRCode();
				}, 5 * 60 * 1000);
				that.loginTimer = setInterval(function() {
					that.$http('user/wechatLogin', 'POST', { uuid: that.uuid }, true, function(resp) {
						if (resp.result) {
							clearInterval(that.qrCodeTimer);
							clearInterval(that.loginTimer);
							let permissions = resp.permissions;
							localStorage.setItem('permissions', permissions);
							router.push({ name: 'Home' });
						}
					});
				}, 5000);
			} else {
				//销毁刷新二维码定时器
				clearInterval(that.qrCodeTimer);
				clearInterval(that.loginTimer);
			}
		},
		//加载二维码图片的封装方法
		loadQRCode: function() {
			this.$http('user/createQrCode', 'GET', null, true, resp => {
				this.qrCode = resp.pic;
				this.uuid = resp.uuid;
			});
		}
	}
};
</script>

<style lang="less" scoped="scoped">
@import url('login.less');
</style>

<template>
	<el-dialog title="提示" v-model="visible" width="25%">
		<el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="80px">
			<el-form-item label="旧パスワード" prop="password">
				<el-input type="password" v-model="dataForm.password" size="medium" clearable />
			</el-form-item>
			<el-form-item label="新パスワード" prop="newPassword">
				<el-input type="password" v-model="dataForm.newPassword" size="medium" clearable />
			</el-form-item>
			<el-form-item label="新パスワード" prop="confirmPassword">
				<el-input type="password" v-model="dataForm.confirmPassword" size="medium" clearable />
			</el-form-item>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button size="medium" @click="visible = false">キャンセル</el-button>
				<el-button type="primary" size="medium" @click="dataFormSubmit">変更</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script>
export default {
	data() {
		const validateConfirmPassword = (rule, value, callback) => {
			if (value != this.dataForm.newPassword) {
				callback(new Error('2回パスワードを一致に入力してください。'));
			} else {
				callback();
			}
		};

		return {
			visible: false,
			dataForm: {
				password: '',
				newPassword: '',
				confirmPassword: ''
			},
			dataRule: {
				password: [{ required: true, pattern: '^[a-zA-Z0-9]{6,20}$', message: 'パスワードを正しく入力してください。' }],
				newPassword: [{ required: true, pattern: '^[a-zA-Z0-9]{6,20}$', message: 'パスワードを正しく入力してください。' }],
				confirmPassword: [
					{ required: true, pattern: '^[a-zA-Z0-9]{6,20}$', message: 'パスワードを正しく入力してください。' },
					{ validator: validateConfirmPassword, trigger: 'blur' }
				]
			}
		};
	},
	methods: {
		init:function(){
			this.visible = true;
			this.$nextTick(() => {
				this.$refs['dataForm'].resetFields();
			});
		},
		dataFormSubmit:function(){
			let _this = this;
			_this.$refs['dataForm'].validate(function(valid) {
				if(valid){
					let data = {password: _this.dataForm.confirmPassword}
					_this.$http('user/updatePassword','POST', data, true, function(response){
						if(response.rows = 1) {
							_this.$message({
								message: 'パスワードの変更に成功しました。',
								type: 'success',
								duration: 1200
							})
							_this.visible = false;
						} else {
							_this.$message({
								message: 'パスワードの変更に失敗しました。',
								type:  'error',
								duration: 1200
							})
						}
					})
				}
			})
		}
	}
};
</script>

<style></style>

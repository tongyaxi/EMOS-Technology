<template>
	<el-dialog
		:title="!dataForm.id ? '新規' : '修正'"
		v-if="isAuth(['ROOT', 'USER:INSERT', 'USER:UPDATE'])"
		:close-on-click-modal="false"
		v-model="visible"
		width="450px"
	>
		<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="130px">
			<el-form-item label="ユーザー名" prop="username">
				<el-input v-model="dataForm.username" size="medium" clearable />
			</el-form-item>
			<el-form-item label="パスワード" prop="password">
				<el-input type="password" v-model="dataForm.password" size="medium" clearable />
			</el-form-item>
			<el-form-item label="氏名" prop="name">
				<el-input v-model="dataForm.name" size="medium" clearable />
			</el-form-item>
			<el-form-item label="性別" prop="sex">
				<el-select v-model="dataForm.sex" size="medium" style="width: 100%;" clearable>
					<el-option label="男" value="男"></el-option>
					<el-option label="女" value="女"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="電話番号" prop="tel">
				<el-input v-model="dataForm.tel" size="medium" clearable />
			</el-form-item>
			<el-form-item label="メールアドレス" prop="email">
				<el-input v-model="dataForm.email" size="medium" clearable />
			</el-form-item>
			<el-form-item label="入社日" prop="hiredate">
				<el-date-picker
					v-model="dataForm.hiredate"
					type="date"
					placeholder="入社日を選択してください."
					size="medium"
					:editable="false"
					format="YYYY-MM-DD"
					value-format="YYYY-MM-DD"
					style="width: 100%;"
				/>
			</el-form-item>
			<el-form-item label="役割" prop="role">
				<el-select
					v-model="dataForm.role"
					size="medium"
					placeholder="役割を選択してください."
					style="width: 100%;"
					multiple
					clearable
				>
					<el-option
						v-for="one in roleList"
						:key="one.id"
						:label="one.roleName"
						:value="one.id"
						:disabled="one.roleName == 'システム管理者'"
					></el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="部門" prop="deptId">
				<el-select
					v-model="dataForm.deptId"
					size="medium"
					placeholder="部門を選択してください."
					style="width: 100%;"
					clearable
				>
					<el-option v-for="one in deptList" :key="one.id" :label="one.deptName" :value="one.id" />
				</el-select>
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
import dayjs from 'dayjs';
export default {
	data: function() {
		return {
			visible: false,// 弹出页的显示
			dataForm: {
				id: null,
				username: null,
				password: null,
				name: null,
				sex: null,
				tel: null,
				email: null,
				hiredate: dayjs(new Date()).format("YYYY-MM-DD"),
				role: null,
				deptId: null,
				status: 1
			},
			roleList: [], // 角色下拉列表	
			deptList: [], // 部门下拉列表
			dataRule: {
				username: [{ required: true, pattern: '^[a-zA-Z0-9]{5,20}$', message: 'ユーザー名を正しく入力してください。' }],
				password: [{ required: true, pattern: '^[a-zA-Z0-9]{6,20}$', message: 'パスワードを正しく入力してください。' }],
				name: [{ required: true, pattern: '^[\u4e00-\u9fa5]{2,10}$', message: '氏名を正しく入力してください。' }],
				sex: [{ required: true, message: '性別を正しく入力してください。' }],
				tel: [{ required: true, pattern: '^1\\d{10}$', message: '電話番号を正しく入力してください。' }],
				email: [{required: true,pattern: '^([a-zA-Z]|[0-9])(\\w|\\-)+@[a-zA-Z0-9]+\\.([a-zA-Z]{2,4})$',message: '郵便番号を正しく入力してください。'}],
				hiredate: [{ required: true, trigger: 'blur', message: '入社日はNULL又は空文字列が不可' }],
				role: [{ required: true, message: '役割はNULL又は空文字列が不可' }],
				deptId: [{ required: true, message: '部門IDはNULL又は空文字列が不可' }],
				status: [{ required: true, message: 'ステータスはNULL又は空文字列が不可' }]
			}
		};
	},
	methods: {
		init: function(id) {
			let _this = this;
			_this.dataForm.id = id || 0;
			_this.visible = true;// 显示弹出框
			_this.$nextTick(() => {
				// 复位弹窗中数据
				_this.$refs['dataForm'].resetFields();
				// 加载角色列表数据
				_this.$http('role/searchAllRole', 'GET', null, true, function(resp) {
					_this.roleList = resp.allRoles;
				});
				// 加载部门列表数据
				_this.$http('dept/searchAllDept', 'GET', null, true, function(resp) {
					_this.deptList = resp.allDept;
				});
				// 変更(大于0的数被认为是true)
				if (_this.dataForm.id) {
					_this.$http('user/searchUserById', 'POST', { userId: id }, true, function(resp) {
						_this.dataForm.username = resp.username;
						_this.dataForm.password = resp.password;
						_this.dataForm.name = resp.name;
						_this.dataForm.sex = resp.sex;
						_this.dataForm.tel = resp.tel;
						_this.dataForm.email = resp.email;
						_this.dataForm.hiredate = resp.hiredate;
						_this.dataForm.role = JSON.parse(resp.role);
						_this.dataForm.deptId = resp.deptId;
						_this.dataForm.status = resp.status;
					});
				}
			});
		},
		// 更新
		dataFormSubmit: function() {
			let _this = this;
			// フォームの検証
			_this.$refs['dataForm'].validate(function(valid) {
				if(valid) {
					let data = {
						userId: _this.dataForm.id,
						username: _this.dataForm.username,
						password: _this.dataForm.password,
						name: _this.dataForm.name,
						sex: _this.dataForm.sex,
						tel: _this.dataForm.tel,
						email: _this.dataForm.email,
						hiredate: dayjs(_this.dataForm.hiredate).format('YYYY-MM-DD'),// 存储在表单中的入社日是日期对象，需要转化为字符串
						role: _this.dataForm.role,
						deptId: _this.dataForm.deptId,
						status: _this.dataForm.status
					};
					// 更新按钮提交，有可能是新规，也可能是变更，URL需要做三元运算
					_this.$http(`user/${!_this.dataForm.id ? 'insert':'update'}`, 'POST', data, true, function(resp) {
						if(resp.rows == 1) {
							_this.$message({
								message: '変更されました。',
								type: 'success',
								duration: 1200
							});
							// 关闭弹窗
							_this.visible = false;
							// 刷新页面处理
							// 触发自定义在标签<add-or-update/>上的refreshDataList事件
							// 子调父方法
							_this.$emit('refreshDataList');
						} else {
							_this.$message({
								message: '変更に失敗しました。',
								type: 'error',
								duration: 1200
							});
						}
					});
				}
			});
		}
	}
};
</script>

<style lang="less" scoped="scoped">
</style>

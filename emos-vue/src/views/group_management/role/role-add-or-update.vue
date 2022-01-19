<template>
	<el-dialog
		:title="!dataForm.id ? '新規' : '修正'"
		v-if="isAuth(['ROOT', 'ROLE:INSERT', 'ROLE:UPDATE'])"
		:close-on-click-modal="false"
		v-model="visible"
		custom-class="dialog"
		width="692px"
	>
		<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="60px">
			<el-form-item label="役割" prop="roleName">
				<el-input v-model="dataForm.roleName" size="medium" style="width:50%" :readonly="systemic" clearable />
				<span class="note">（ 役割名称は2~10の文字列が必須 ）</span>
			</el-form-item>
			<el-form-item label="備考" prop="desc">
				<el-input v-model="dataForm.desc" style="width:50%" size="medium" maxlength="20" clearable />
				<span class="note">（ 備考は20の文字列以内が必須 ）</span>
			</el-form-item>
			<el-form-item label="権限" prop="permissions">
				<el-transfer
					v-model="dataForm.permissions"
					:data="permissionList"
					size="medium"
					:titles="['権限リスト', '既存権限']"
					filterable
					filter-placeholder="権限を入力してください."
				/>
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
	data: function() {
		return {
			visible: false,
			// 标记角色是否是系统内置
			systemic: true,
			dataForm: {
				id: null,
				roleName: null,
				permissions: [],
				desc: null,
				changed: false
			},
			// 権限配列用
			permissionList: [],
			// 該当の役割に繋がる既存権限（変更の場合）
			oldPermissions: [],
			dataRule: {
				roleName: [
					{ required: true, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{2,10}$', message: '役割名称を正しく入力してください。' }
				],
				permissions: [
					{ required: true, trigger: 'blur', message: '該当の役割に繋がる権限はありません。' },
					{ required: false, trigger: 'change', message: '該当の役割に繋がる権限はありません。' }
				]
			}
			
		}
	},
	methods: {
		init: function(roleId, systemic){
			let _this = this;
			_this.visible = true;
			_this.dataForm.id = roleId || 0;
			_this.systemic = systemic;
			
			_this.$nextTick(() => {
				
				_this.$refs['dataForm'].resetFields();
				// 系统内置角色会有默认权限
				let defaultPermissions = [];
				
				if (roleId) {
					_this.$http('role/searchRoleById', 'POST', { id: roleId }, false, function(resp) {
						_this.dataForm.roleName = resp.roleName;
						_this.dataForm.desc = resp.desc;
						_this.dataForm.permissions = JSON.parse(resp.permissions);
						//保存原始权限数据
						_this.oldPermissions = JSON.parse(resp.permissions);
						defaultPermissions = resp.defaultPermissions;
					});
				}
				
				_this.$http('permission/searchAllPermission','GET',null,true,function(resp){
					let temp = [];
					for(let permission of resp.permissions){
						// 如果为系统内置角色，默认权限非活性表示
						let disabled = false;
						// 判断要修改的角色是否是系统内置角色表
						if(_this.systemic){
							disabled = defaultPermissions.includes(permission.id);
						}
						
						temp.push({key:permission.id,
								   label:`${permission.moduleName}(${permission.actionName})`,
								   disabled: disabled});
					}
					_this.permissionList = temp;
				});
			});
		},
		dataFormSubmit: function() {
			
			let _this = this;
			_this.$refs['dataForm'].validate(valid => {
				
				if(valid) {
					//对现有的权限排序
					_this.dataForm.permissions.sort(function(a, b) {
						return a - b;
					});
					//判断用户选择的权限和原来的权限是否一致
					// 后端利用这个changed来判断，与该角色相关联的用户是否要踢下线
					if (_this.dataForm.permissions.join() != _this.oldPermissions.join()) {
						_this.dataForm.changed = true;
					} else {
						_this.dataForm.changed = false;
					}
					_this.$http(`role/${!_this.dataForm.id ? 'insert' : 'update'}`, 'POST', _this.dataForm, true, function(resp) {
						if(resp.rows == 1){
							_this.$message({
								message: '変更されました。',
								type: 'success',
								duration: 1200
							});
							// ポップアップを閉じる
							_this.visible = false;
							// 親の関数を行う
							_this.$emit('refreshDataList');
						}else{
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
.note {
	margin-left: 20px;
	color: #999;
}
</style>

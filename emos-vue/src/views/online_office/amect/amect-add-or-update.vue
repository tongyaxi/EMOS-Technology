<template>
	<el-dialog
		:title="!dataForm.id ? '新規' : '変更'"
		:close-on-click-modal="false"
		v-model="visible"
		width="750px"
	>
		<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="70px">
			
			<el-form-item label="タイプ" prop="typeId">
				<el-select v-model="dataForm.typeId" placeholder="罰金タイプ" size="medium" style="width:40%">
					<el-option v-for="one in amectTypeList" :label="one.type" :value="one.id" />
				</el-select>
				<span class="desc">罰金タイプ必須</span>
			</el-form-item>
			
			<el-form-item label="金額" prop="amount">
				<el-input v-model="dataForm.amount" size="medium" style="width:40%" placeholder="罰金金額" clearable />
				<span class="desc">円</span>
			</el-form-item>
			
			<el-form-item label="理由" prop="reason">
				<el-input
					type="textarea"
					:rows="2"
					placeholder="罰金理由"
					v-model="dataForm.reason"
					size="medium"
					resize="none"
					maxlength="200"
					show-word-limit
					clearable="clearable"
				/>
			</el-form-item>
			
			<el-form-item label="社員" prop="members" v-if="dataForm.id == 0">
				<el-transfer
					v-model="dataForm.members"
					:data="users"
					:titles="['社員', '当事者']"
					filterable
					filter-placeholder="当事者を入力してください."
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
			dataForm: {
				id: null,
				typeId: null,
				amount: null,
				reason: null,
				members: []
			},
			amectTypeList: [],
			users: [],
			dataRule: {
				typeId: [{ required: true, message: '罰金タイプは必須' }],
				amount: [
					{
						required: true,
						pattern: '(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)',
						message: '罰金金額を正しく入力してください。'
					}
				],
				reason: [{ required: true, message: '理由は必須' }],
				members: [
					{ required: true, trigger: 'blur', message: '当事者は必須' },
					{ required: false, trigger: 'change', message: '当事者は必須' }
				]
			}
		};
	},
	methods: {
		init: function(id) {
			let _this = this;
			_this.visible = true;
			_this.dataForm.id = id || 0;
			
			_this.$nextTick(() => {
				_this.$refs['dataForm'].resetFields();
				// タイプを検索する
				_this.$http('amect_type/searchAllAmectType', 'GET', {}, true, function(resp){
					_this.amectTypeList = resp.list;
				});
				// 社員リストを検索する
				_this.$http('user/searchAllUser', 'GET', {}, true, function(resp){
					let temp = [];
					for(let one of resp.list){
						temp.push({key: one.id,label: one.name});
					}
					_this.users = temp;
				});
				if(id){
					_this.$http('amect/searchById', 'POST', {id:id}, true, function(resp){
						_this.dataForm.typeId = resp.typeId;
						_this.dataForm.amount = resp.amount + "";
						_this.dataForm.reason = resp.reason;
					});
				}
			});
			
		},
        dataFormSubmit:function(){
			let _this = this;
			let reqData = {
				typeId: _this.dataForm.typeId,
				amount: _this.dataForm.amount,
				reason: _this.dataForm.reason,
				userId: _this.dataForm.members
			}
			if(_this.dataForm.id){
				reqData.id = _this.dataForm.id;
			}
			this.$refs['dataForm'].validate(valid => {
				if(valid){
					_this.$http(`amect/${!_this.dataForm.id?'insert':'update'}`, 'POST', reqData, true, function(resp){
						if(resp.rows > 0){
							_this.$message({
								type: 'success',
								message: '変更に成功しました。',
								duration: 1200
							});
							_this.visible = false;
							_this.$emit('refreshDataList');
						}else{
							_this.$message({
								type: 'error',
								message: '変更に失敗しました。',
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

<style>
.desc {
	margin-left: 15px;
}
</style>

<template>
	<el-dialog
		:title="!dataForm.id ? '新規' : '修正'"
		:close-on-click-modal="false"
		v-model="visible"
		width="550px"
	>
		<el-scrollbar height="470px">
			<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="100px">
				<el-form-item label="タイプ" prop="type">
					<el-radio-group v-model="dataForm.type" size="medium">
						<el-radio-button label="一般"></el-radio-button>
						<el-radio-button label="出張"></el-radio-button>
					</el-radio-group>
				</el-form-item>
				<el-form-item label="借金金額" prop="anleihen">
					<el-input
						v-model="dataForm.anleihen"
						placeholder="借金金額"
						size="medium"
						style="width:160px;"
						clearable="clearable"
						prop="anleihen"
					/>
				</el-form-item>
				
				<div v-for="(one, $index) in dataForm.project" class="project">
					<h3>【 立替金項目 】</h3>
					<i class="el-icon-delete icon-delete" @click="deleteProjectHandle($index)"></i>
					<el-form-item
						label="タイトル"
						:prop="'project.' + $index + '.title'"
						:rules="{
							required: true,
							message: 'タイトルは必須'
						}"
					>
						<el-input
							v-model="one.title"
							size="medium"
							style="width:95%"
							maxlength="20"
							clearable
						/>
					</el-form-item>
					<el-form-item
						label="タイプ"
						:prop="'project.' + $index + '.type'"
						:rules="{
							required: true,
							message: 'タイプは必須'
						}"
					>
						<el-select v-model="one.type" class="input" size="medium" clearable>
							<el-option label="办公用品" value="办公用品" />
							<el-option label="招待费" value="招待费" />
							<el-option label="采购费" value="采购费" />
							<el-option label="劳务费" value="劳务费" />
							<el-option label="培训费" value="培训费" />
							<el-option label="维修费" value="维修费" />
							<el-option label="办公费" value="办公费" />
							<el-option label="其他" value="其他" />
						</el-select>
					</el-form-item>
					<el-form-item label="備考" prop="projectDesc">
						<el-input
							v-model="one.desc"
							type="textarea"
							size="medium"
							maxlength="50"
							style="width:95%"
							resize="none"
							show-word-limit
							clearable
						/>
					</el-form-item>
					<el-form-item
						label="立替金金額"
						:prop="'project.' + $index + '.money'"
						:rules="{
							required: true,
							pattern:
								'(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)',
							message: '立替金金額は必須'
						}"
					>
						<el-input
							v-model="one.money"
							size="medium"
							style="width:160px;"
							clearable
						/>
					</el-form-item>
				</div>
			</el-form>
		</el-scrollbar>
		<template #footer>
			<span class="dialog-footer">
				<el-button type="danger" size="medium" @click="addHandle">項目追加</el-button>
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
				type: '一般',
				anleihen: null,
				project: []
			},
			dataRule: {
				anleihen: [
					{
						required: true,
						pattern:
							'(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)',
						message: '金額を正しく入力してください。'
					}
				]
			}
		};
	},
	methods: {
		
		init: function() {
			let _this = this;
			_this.visible = true;
			_this.$nextTick(() => {
				_this.$refs['dataForm'].resetFields();
				_this.dataForm.project = [{ title: null, type: null, desc: null, money: null }];
			});
		},
		// 項目追加
		addHandle: function() {
			let _this = this;
			if(_this.dataForm.project.length==5){
				_this.$message({
					message: '当立替金には四つ以下の項目が必須。',
					type: 'warning',
					duration: 1200
				});
				return
			}
			_this.dataForm.project.push({ title: null, type: null, desc: null, money: null });
			_this.$message({
				message: '項目追加に成功しました。',
				type: 'success',
				duration: 1200
			});
		},
		// 項目削除
		deleteProjectHandle: function(index) {
			let _this = this;
			if (_this.dataForm.project.length == 1) {
				_this.$message({
					message: '少なくとも一つの項目を追加しなければなりません。',
					type: 'warning',
					duration: 1200
				});
			} else {
				_this.dataForm.project.splice(index, 1);
				_this.$message({
					message: '項目削除に成功しました。',
					type: 'success',
					duration: 1200
				});
			}
		},
		// 変更
		dataFormSubmit: function() {
			
			let _this = this;
			this.$refs['dataForm'].validate(valid => {
				if (valid) {
					let amount = 0;
					for(let one of _this.dataForm.project){
						amount+=Number(one.money)
					}
					let reqData = {
						typeId: _this.dataForm.type == "一般"?1:2,
						amount: amount,
						anleihen: _this.dataForm.anleihen,
						balance: amount-Number(_this.dataForm.anleihen),
						content: JSON.stringify(_this.dataForm.project)
					};
					_this.$http(
						`reim/${!_this.dataForm.id ? 'insert' : 'update'}`, 'POST', reqData, true, function(resp) {
							
							if (resp.rows == 1) {
								_this.$message({
									message: '変更しました。',
									type: 'success',
									duration: 1200
								});
								_this.visible = false;
								_this.$emit('refreshDataList');
							} else {
								_this.$message({
									message: '変更に失敗しました。',
									type: 'error',
									duration: 1200
								});
							}
						}
					);
				}
			});
		}
	}
};
</script>

<style lang="less" scoped="scoped">
.note {
	color: #999;
	margin-left: 15px;
}
.project {
	border-top: dashed 1px #e0e0e0;
	position: relative;
}
h3 {
	text-align: center;
	margin: 20px 0;
}
.icon-delete {
	position: absolute;
	right: 20px;
	top: 20px;
	height: 24px;
	width: 24px;
	cursor: pointer;
}
</style>

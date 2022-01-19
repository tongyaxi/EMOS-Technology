<template>
	<el-dialog title="休暇申請" :close-on-click-modal="false" v-model="visible" width="450px">
		<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="80px">
			<el-form-item label="理由" prop="reason">
				<el-input
					type="textarea"
					v-model="dataForm.reason"
					placeholder="理由を入力してください."
					:autosize="{ minRows: 4, maxRows: 6 }"
					clearable="clearable"
				/>
			</el-form-item>
			<el-form-item label="開始時間">
				<el-row type="flex" justify="space-between">
					<el-col :span="12">
						<el-form-item prop="startDate" class="form-item">
							<el-date-picker
								v-model="dataForm.startDate"
								value-format="yyyy-MM-dd"
								type="date"
								placeholder="開始時間"
								style="width: 100%;"
								:disabledDate="disabledDate"
								clearable="clearable"
							></el-date-picker>
						</el-form-item>
					</el-col>
					<el-col :span="11">
						<el-form-item prop="startTime" class="form-item">
							<el-time-select
								v-model="dataForm.startTime"
								start='08:30'
								step='00:30'
								end='20:30'
								value-format="HH:mm"
								placeholder="時間を選択してください."
								style="width: 100%;"
							></el-time-select>
						</el-form-item>
					</el-col>
				</el-row>
			</el-form-item>
			<el-form-item label="終了時間">
				<el-row type="flex" justify="space-between">
					<el-col :span="12">
						<el-form-item prop="endDate" class="form-item">
							<el-date-picker
								v-model="dataForm.endDate"
								value-format="yyyy-MM-dd"
								type="date"
								placeholder="終了時間"
								style="width: 100%;"
								:disabledDate="disabledDate"
								clearable="clearable"
							></el-date-picker>
						</el-form-item>
					</el-col>
					<el-col :span="11">
						<el-form-item prop="endTime" class="form-item">
							<el-time-select
								v-model="dataForm.endTime"
								value-format="HH:mm"
								start='08:30'
								step='00:30'
								end='20:30'
								placeholder="時間を選択してください."
								style="width: 100%;"
							></el-time-select>
						</el-form-item>
					</el-col>
				</el-row>
			</el-form-item>
			<el-form-item label="タイプ" prop="type">
				<el-radio-group v-model="dataForm.type">
					<el-radio :label="1">病気のため</el-radio>
					<el-radio :label="2">私用のため</el-radio>
				</el-radio-group>
			</el-form-item>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button size="medium" @click="visible = false">キャンセル</el-button>
				<el-button type="primary" size="medium" @click="dataFormSubmit">新規</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script>
import dayjs from 'dayjs';
export default {
	data: function() {
		return {
			visible: false,
			dataForm: {
				reason: null,
				startDate: null,
				startTime: null,
				endDate: null,
				endTime: null,
				type: null
			},
			disabledDate(date) {
				return dayjs(date.toLocaleDateString()).isBefore(new Date().toLocaleDateString());
			},
			dataRule: {
				reason: [{ required: true, message: '理由を正しく入力してください。' }],
				startDate: [{ required: true, message: '開始時間を正しく入力してください。' }],
				startTime: [{ required: true, message: '時間を正しく入力してください。' }],
				endDate: [{ required: true, message: '終了時間を正しく入力してください。' }],
				endTime: [{ required: true, message: '時間を正しく入力してください。' }],
				type: [{ required: true, message: 'タイプを正しく入力してください。' }]
			}
		};
	},
	methods: {
		init: function(id) {
			let _this = this;
			_this.visible = true;
			_this.$nextTick(() => {
				_this.$refs['dataForm'].resetFields();
			});
		},
		// 新規
		dataFormSubmit:function(){
			
			let _this = this;
			let reqData = {
				reason:_this.dataForm.reason,
				start: dayjs(_this.dataForm.startDate).format('YYYY-MM-DD') + ' ' + _this.dataForm.startTime + ':00',
				end: dayjs(_this.dataForm.endDate).format('YYYY-MM-DD') + ' ' + _this.dataForm.endTime + ':00',
				type:_this.dataForm.type,
			}
			this.$refs['dataForm'].validate(valid => {
				if (valid) {
					_this.$http("leave/insert","POST",reqData,true,function(resp){
						if(resp.rows==1){
							_this.visible=false
							_this.$message({
								message: '変更に成功しました。',
								type: 'success',
								duration: 1200,
							});
							setTimeout(function(){
								_this.$emit('refreshDataList');
							},1200)
						}else{
							_this.$message({
								message: '変更に失敗しました。',
								type: 'error',
								duration: 1200,
							});
						}
					})
				}
			})
		}
	}
};
</script>

<style lang="less" scoped="scoped">
.form-item {
	margin-bottom: 0 !important;
}
</style>

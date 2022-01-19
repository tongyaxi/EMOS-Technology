<template>
	
    <el-dialog title="オンライン会議申請" :close-on-click-modal="false" v-model="visible" width="835px">
        
		<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="90px">
            
			<el-form-item label="タイトル" prop="title">
                <el-input v-model="dataForm.title" size="medium" style="width:100%" clearable="clearable" />
            </el-form-item>
			
            <el-form-item label="内容" prop="desc">
                <el-input
                    type="textarea"
                    :rows="2"
                    placeholder="会議内容を入力してください."
                    v-model="dataForm.desc"
                    size="medium"
                    resize="none"
                    maxlength="200"
                    clearable="clearable"
                />
            </el-form-item>
			
            <el-form-item label="日付" prop="date">
                <el-date-picker
                    v-model="dataForm.date"
                    type="date"
                    placeholder="日付を選択してください."
                    style="width:34.5%"
                    size="medium"
                    :disabledDate="disabledDate"
                    clearable="clearable"
                ></el-date-picker>
                <span class="note">日付は現在の時刻または未来の時刻に指定しなければなりません。</span>
            </el-form-item>
			
            <el-form-item label="時間">
                <el-col :span="11">
                    <el-form-item prop="start" class="inner-item">
                        <el-time-select
                            placeholder="開始時間"
                            v-model="dataForm.start"
                            start="08:30"
                            step="00:10"
                            end="23:30"
                            size="medium"
                            style="width:96%"
                            clearable="clearable"
                        ></el-time-select>
                    </el-form-item>
                </el-col>
                <el-col :span="2">&nbsp;&nbsp;~&nbsp;&nbsp;</el-col>
                <el-col :span="11" prop="end">
                    <el-form-item prop="end" class="inner-item">
                        <el-time-select
                            placeholder="終了時間"
                            v-model="dataForm.end"
                            start="08:30"
                            step="00:10"
                            end="23:30"
                            size="medium"
                            style="width:96%"
                            clearable="clearable"
                            :minTime="dataForm.start"
                        ></el-time-select>
                    </el-form-item>
                </el-col>
                <span class="note">時間帯にご注意ください。</span>
            </el-form-item>
			
            <el-form-item label="参加者" prop="members">
                <el-transfer
                    v-model="dataForm.members"
                    :data="users"
                    :titles="['社員', '参加者']"
                    filterable
                    filter-placeholder="名前を入力して"
                />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button size="medium" @click="visible = false">取消</el-button>
                <el-button type="primary" size="medium" @click="dataFormSubmit">确定</el-button>
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
                title: null,
                date: null,
                start: null,
                end: null,
                desc: null,
                members: [],
                type: 1
            },
            disabledDate(date) {
                return date.getTime() < Date.now() - 24 * 60 * 60 * 1000;
            },
            users: [],
            dataRule: {
                title: [{ required: true, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{2,30}$', message: 'タイトルを正しく入力してください。' }],
                desc: [{ required: true, message: '内容を入力してください。' }],
                date: [{ required: true, message: '日付を入力してください。' }],
                start: [{ required: true, message: '開始時間を入力してください。' }],
                end: [{ required: true, message: '終了時間を入力してください。' }],
                members: [
                	{ required: true, trigger: 'blur', message: '参加者を選択してください。' },
                	{ required: false, trigger: 'change', message: '参加者を選択してください。' }
                ]
            }
        };
    },
    methods: {
        init: function(id) {
			
            let _this = this;
            _this.visible = true;
            _this.$nextTick(() => {
                _this.$refs['dataForm'].resetFields();
                _this.$http('user/searchAllUser', 'GET', null, true, function(resp) {
                    let temp = [];
                    for (let one of resp.list) {
                        temp.push({ key: one.id, label: one.name });
                    }
                    _this.users = temp;
                });
            });
        },
        dataFormSubmit: function() {
            
			let _this = this;
            let reqData = JSON.parse(JSON.stringify(_this.dataForm));

            reqData.date = dayjs(_this.dataForm.date).format('YYYY-MM-DD');

            reqData.members = JSON.stringify(_this.dataForm.members);

            this.$refs['dataForm'].validate(valid => {
                if (valid) {
                    _this.$http('meeting/insert', 'POST', reqData, true, function(resp) {
                        if (resp.rows == 1) {
                            _this.visible = false;
                            _this.$message({
                                message: '変更されました',
                                type: 'success',
                                duration: 1200
                            });
                            setTimeout(function() {
                                _this.$emit('refresh');
                            }, 1200);
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

<style lang="less" scoped="scoped"></style>

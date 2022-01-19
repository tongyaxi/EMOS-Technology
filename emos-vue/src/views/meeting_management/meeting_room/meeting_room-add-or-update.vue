<template>
    <el-dialog
        :title="!dataForm.id ? '新規' : '修正'"
        v-if="isAuth(['ROOT', 'MEETING_ROOM:INSERT', 'MEETING_ROOM:UPDATE'])"
        :close-on-click-modal="false"
        v-model="visible"
        width="430px"
    >
        <el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="100px">
            <el-form-item label="会議室名" prop="name">
                <el-input v-model="dataForm.name" size="medium" style="width:100%" clearable />
            </el-form-item>
            <el-form-item label="最大人数" prop="max">
                <el-input v-model="dataForm.max" size="medium" style="width:100%" clearable />
            </el-form-item>
            <el-form-item label="備考" prop="desc">
                <el-input v-model="dataForm.desc" style="width:100%" size="medium" maxlength="20" clearable />
            </el-form-item>
            <el-form-item v-if="dataForm.id" label="ステータス">
                <el-select v-model="dataForm.status" class="input" placeholder="ステータス" size="medium">
                    <el-option label="利用可" value="1" />
                    <el-option label="利用不可" value="0" />
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
export default {
    data: function() {
        return {
            visible: false,
            dataForm: {
                id: null,
                name: null,
                max: null,
                desc: null,
                status: 1
            },
            dataRule: {
                name: [{ required: true, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{2,20}$', message: '会議室を正しく入力してください。' }],
                max: [{ required: true, pattern: '^[1-9]\\d{0,4}$', message: '最大人数を正しく入力してください。' }]
            }
        };
    },

    methods: {
        init: function(id) {
			
            let _this = this;
            _this.dataForm.id = id || 0;
            _this.visible = true;
            _this.$nextTick(() => {
                _this.$refs['dataForm'].resetFields();
                if (id) {
                    _this.$http('meeting_room/searchById', 'POST', { id: id }, true, function(resp) {
                        _this.dataForm.name = resp.name;
                        _this.dataForm.max = resp.max + '';
                        _this.dataForm.desc = resp.desc;
                        _this.dataForm.status = resp.status + '';
                    });
                }
            });
        },
        dataFormSubmit: function() {
            let _this = this;
            this.$refs['dataForm'].validate(valid => {
                if (valid) {
                    _this.$http(
                        `meeting_room/${!_this.dataForm.id ? 'insert' : 'update'}`,
                        'POST',
                        _this.dataForm,
                        true,
                        function(resp) {
                            if (resp.rows == 1) {
                                _this.$message({
                                    message: '変更されました。',
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

<style lang="less">
.el-form {
    margin-left: 0px;
    margin-right: 10px;
}
.el-dialog__footer {
    margin-right: 10px;
}
.note {
    margin-left: 20px;
    color: #999;
}
</style>

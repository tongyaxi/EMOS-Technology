<template>
    <el-dialog
        :title="!dataForm.id ? '新規' : '修正'"
        v-if="isAuth(['ROOT'])"
        :close-on-click-modal="false"
        v-model="visible"
        width="420px"
    >
        <el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="80px">
            <el-form-item label="罰金種類" prop="type">
                <el-input v-model="dataForm.type" size="medium" style="width:100%" clearable />
            </el-form-item>
            <el-form-item label="罰金金額" prop="money">
                <el-input v-model="dataForm.money" size="medium" style="width:100%" clearable />
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
                type: null,
                money: null
            },
            dataRule: {
                type: [{ required: true, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{2,10}$', message: '罰金タイプを正しく入力してください。' }],
                money: [
                    {
                        required: true,
                        pattern: '(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)',
                        message: '罰金金額を正しく入力してください。'
                    }
                ]
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
                    _this.$http('amect_type/searchById', 'POST', { id: id }, true, function(resp) {
                        _this.dataForm.type = resp.type;
                        _this.dataForm.money = resp.money + '';
                    });
                }
            });
        },
        dataFormSubmit: function() {
			
            let _this = this;
            let data = {
                type: _this.dataForm.type,
                money: _this.dataForm.money
            };
            if (_this.dataForm.id) {
                data.id = _this.dataForm.id;
            }
            this.$refs['dataForm'].validate(valid => {
                if (valid) {
                    _this.$http(`amect_type/${!_this.dataForm.id ? 'insert' : 'update'}`, 'POST', data, true, function(resp) {
                        if (resp.rows > 0) {
                            _this.visible = false;
                            _this.$emit('refreshDataList');
                            _this.$message({
                                message: '変更に成功しました。',
                                type: 'success',
                                duration: 1200
                            });
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

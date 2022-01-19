<template>
	
    <el-dialog
        :title="!dataForm.id ? '新規' : '修正'"
        v-if="isAuth(['ROOT', 'DEPT:INSERT', 'DEPT:UPDATE'])"
        :close-on-click-modal="false"
        v-model="visible"
        width="420px"
    >
        <el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="60px">
            <el-form-item label="部門" prop="deptName">
                <el-input v-model="dataForm.deptName" size="medium" style="width:100%" clearable />
            </el-form-item>
            <el-form-item label="携帯" prop="tel">
                <el-input v-model="dataForm.tel" size="medium" style="width:100%" clearable />
            </el-form-item>
            <el-form-item label="メール" prop="email">
                <el-input v-model="dataForm.email" size="medium" style="width:100%" clearable />
            </el-form-item>
            <el-form-item label="備考" prop="desc">
                <el-input v-model="dataForm.desc" style="width:100%" size="medium" maxlength="20" clearable />
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
                deptName: null,
                tel: null,
                email: null,
                desc: null
            },
            dataRule: {
                deptName: [
                    { required: true, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{2,10}$', message: '部門名称を正しく入力してください。' }
                ],
                tel: [
                    {
                        required: false,
                        pattern: '^1\\d{10}$|^(0\\d{2,3}\-){0,1}[1-9]\\d{6,7}$',
                        message: '携帯電話を正しく入力してください。'
                    }
                ],
                email: [
                    {
                        required: false,
                        pattern: '^([a-zA-Z]|[0-9])(\\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$',
                        message: 'メールアドレスを正しく入力してください。'
                    }
                ]
            }
        };
    },

    methods: {
        init: function(id) {
			
			let _this = this;
			_this.visible = true;
            
			// 修正 || 新規(false)
            _this.dataForm.id = id || 0;
			
            _this.$nextTick(() => {
                _this.$refs['dataForm'].resetFields();
                if (id) {
                    _this.$http('dept/searchById', 'POST', { id: id }, true, function(resp) {
                        _this.dataForm.deptName = resp.deptName;
                        _this.dataForm.tel = resp.tel;
                        _this.dataForm.email = resp.email;
                        _this.dataForm.desc = resp.desc;
                    });
                }
            });
        },
        dataFormSubmit: function() {
			
            let _this = this;
            this.$refs['dataForm'].validate(valid => {
				
                if (valid) {
                    _this.$http(`dept/${!_this.dataForm.id ? 'insert' : 'update'}`, 'POST', _this.dataForm, true, function(resp) {
                        
						if (resp.rows == 1) {
                            _this.$message({
                                message: '変更されました。',
                                type: 'success',
                                duration: 1200
                            });
                            _this.visible = false;
							// 刷新父组件
                            _this.$emit('refreshDataList');
                        } else {
                            that.$message({
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

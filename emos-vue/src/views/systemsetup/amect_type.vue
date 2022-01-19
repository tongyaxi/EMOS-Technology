<template>
    <div v-if="isAuth(['ROOT'])">
        <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm">
            <el-form-item prop="type">
                <el-input
                    v-model="dataForm.type"
                    placeholder="罰金タイプ"
                    size="medium"
                    class="input"
                    clearable="clearable"
                />
            </el-form-item>
            <el-form-item>
                <el-button size="medium" type="primary" @click="searchHandle()">検索</el-button>
                <el-button size="medium" type="primary" @click="addHandle()">新規</el-button>
                <el-button size="medium" type="danger" @click="deleteHandle()">一括削除</el-button>
            </el-form-item>
        </el-form>
        <el-table
            :data="dataList"
            border
            v-loading="dataListLoading"
            @selection-change="selectionChangeHandle"
            cell-style="padding: 4px 0"
            style="width: 100%;"
            size="medium"
        >
            <el-table-column
                type="selection"
                :selectable="selectable"
                header-align="center"
                align="center"
                width="50"
            />
            <el-table-column type="index" header-align="center" align="center" width="100" label="番号">
                <template #default="scope">
                    <span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="type" header-align="center" align="center" label="罰金タイプ" />
            <el-table-column header-align="center" align="center" label="罰金金額">
                <template #default="scope">
                    <span>{{ scope.row.money }}円</span>
                </template>
            </el-table-column>
            <el-table-column header-align="center" align="center" label="デフォルト">
                <template #default="scope">
                    <span>{{ scope.row.systemic ? 'はい' : 'いいえ' }}</span>
                </template>
            </el-table-column>
            <el-table-column header-align="center" align="center" label="未支払数">
                <template #default="scope">
                    <span>{{ scope.row.notPay == 0 ? '--' : scope.row.notPay + '個' }}</span>
                </template>
            </el-table-column>
            <el-table-column fixed="right" header-align="center" align="center" width="150" label="操作">
                <template #default="scope">
                    <el-button type="text" size="medium" @click="updateHandle(scope.row.id)">修正</el-button>
                    <el-button
                        type="text"
                        size="medium"
                        :disabled="scope.row.canDelete == 'false'"
                        @click="deleteHandle(scope.row.id)"
                    >
                        削除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
            @size-change="sizeChangeHandle"
            @current-change="currentChangeHandle"
            :current-page="pageIndex"
            :page-sizes="[10, 20, 50]"
            :page-size="pageSize"
            :total="totalCount"
            layout="prev, pager, next"
        ></el-pagination>
        <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="loadDataList"></add-or-update>
    </div>
</template>

<script>
import AddOrUpdate from './amect_type-add-or-update.vue';
export default {
    components: {
        AddOrUpdate
    },
    data: function() {
        return {
            dataForm: {
                type: null
            },
            dataList: [],
            pageIndex: 1,
            pageSize: 10,
            totalCount: 0,
            dataListLoading: false,
            dataListSelections: [],
            addOrUpdateVisible: false,
            dataRule: {
                type: [{ required: false, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{1,10}$', message: '罰金タイプを正しく入力してください。' }]
            }
        };
    },
	created: function() {
	    this.loadDataList();
	},
    methods: {
		
        loadDataList: function() {
			
            let _this = this;
            _this.dataListLoading = true;

            let reqData = {
                type: _this.dataForm.type,
                page: _this.pageIndex,
                length: _this.pageSize
            };

            _this.$http('amect_type/searchAmectTypeByPage', 'POST', reqData, true, function(resp) {
                let page = resp.list;

                _this.dataList = page.list;
                _this.totalCount = page.totalCount;
                _this.dataListLoading = false;
            });
        },
		sizeChangeHandle: function(val) {
		    this.pageSize = val;
		    this.pageIndex = 1;
		    this.loadDataList();
		},
		currentChangeHandle: function(val) {
		    this.pageIndex = val;
		    this.loadDataList();
		},
		// 検索
		searchHandle: function() {
		    this.$refs['dataForm'].validate(valid => {
		        if (valid) {
		            this.$refs['dataForm'].clearValidate();
		            if (this.dataForm.type == '') {
		                this.dataForm.type = null;
		            }
		            if (this.pageIndex != 1) {
		                this.pageIndex = 1;
		            }
		            this.loadDataList();
		        } else {
		            return false;
		        }
		    });
		},
		// 新規
		addHandle: function() {
			
		    this.addOrUpdateVisible = true;
		    this.$nextTick(() => {
		        this.$refs.addOrUpdate.init();
		    });
		},
		// 更新
		updateHandle: function(id) {
		    this.addOrUpdateVisible = true;
		    this.$nextTick(() => {
		        this.$refs.addOrUpdate.init(id);
		    });
		},
        selectable: function(row, index) {
            if (row.canDelete == 'true') {
                return true;
            }
            return false;
        },
        selectionChangeHandle: function(val) {
            this.dataListSelections = val;
        },
		//　削除
        deleteHandle: function(id) {
            
			let _this = this;
            let ids = id
                ? [id]
                : _this.dataListSelections.map(item => {
                      return item.id;
                  });
            if (ids.length == 0) {
                _this.$message({
                    message: '記録を選択してください。',
                    type: 'warning',
                    duration: 1200
                });
            } else {
                _this.$confirm(`削除してもよろしいでしょうか？`, '提示', {
                    confirmButtonText: '削除',
                    cancelButtonText: 'キャンセル',
                    type: 'warning'
                }).then(() => {
                    _this.$http('amect_type/deleteAmectTypeByIds', 'POST', { ids: ids }, true, function(resp) {
                        if (resp.rows > 0) {
                            _this.$message({
                                message: '削除に成功しました。',
                                type: 'success',
                                duration: 1200,
                                onClose: () => {
                                    _this.loadDataList();
                                }
                            });
                        } else {
                            _this.$message({
                                message: '削除に失敗しました。',
                                type: 'warning',
                                duration: 1200
                            });
                        }
                    });
                });
            }
        }
    }
};
</script>

<style lang="less" scoped="scoped"></style>

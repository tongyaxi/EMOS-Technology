<template>
	
    <div v-if="isAuth(['ROOT', 'DEPT:SELECT'])">
        <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm">
            
			<el-form-item prop="deptName">
                <el-input
                    v-model="dataForm.deptName"
                    placeholder="部門名称"
                    size="medium"
                    class="input"
                    clearable="clearable"
                />
            </el-form-item>
			
            <el-form-item>
                <el-button size="medium" type="primary" @click="searchHandle()">検索</el-button>
                <el-button
                    size="medium"
                    type="primary"
                    :disabled="!isAuth(['ROOT', 'DEPT:INSERT'])"
                    @click="addHandle()"
                >
                    新規
                </el-button>
                <el-button
                    size="medium"
                    type="danger"
                    :disabled="!isAuth(['ROOT', 'DEPT:DELETE'])"
                    @click="deleteHandle()"
                >
                    一括削除
                </el-button>
            </el-form-item>
			
        </el-form>
		
        <el-table
            :data="dataList"
            border
            v-loading="dataListLoading"
            @selection-change="selectionChangeHandle"
            cell-style="padding: 4px 0"
            size="medium"
            style="width: 100%;"
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
            <el-table-column prop="deptName" header-align="center" align="center" label="部門名称" min-width="170" />
            <el-table-column prop="tel" header-align="center" align="center" label="携帯電話" min-width="170" />
            <el-table-column prop="email" header-align="center" align="center" label="メールアドレス" min-width="270" />
            <el-table-column header-align="center" align="center" label="社員数" min-width="140">
                <template #default="scope">
                    <span>{{ scope.row.emps }}人</span>
                </template>
            </el-table-column>
            <el-table-column prop="desc" header-align="center" align="center" label="備考" min-width="400" />
            <el-table-column header-align="center" align="center" width="150" label="操作">
                <template #default="scope">
                    <el-button
                        type="text"
                        size="medium"
                        :disabled="!isAuth(['ROOT', 'DEPT:UPDATE'])"
                        @click="updateHandle(scope.row.id)"
                    >
                        修正
                    </el-button>
                    <el-button
                        type="text"
                        size="medium"
                        :disabled="!isAuth(['ROOT', 'DEPT:DELETE']) || scope.row.emps > 0"
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
// 引入弹窗页面
import AddOrUpdate from './dept-add-or-update.vue';
export default {
    components: {
        AddOrUpdate
    },
	// 本页面生命周期函数中调用初始化数据
	created: function() {
	    this.loadDataList();
	},
    data: function() {
        return {
            dataForm: {
                deptName: null
            },
            dataList: [],
            pageIndex: 1,
            pageSize: 10,
            totalCount: 0,
            dataListLoading: false,
            dataListSelections: [],
            addOrUpdateVisible: false,
            dataRule: {
                deptName: [
                    { required: false, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{1,10}$', message: '部門名称を正しく入力してください。' }
                ]
            }
        };
    },
    methods: {
        loadDataList: function() {
            let _this = this;
            _this.dataListLoading = true;
			
            let requestData = {
                deptName: _this.dataForm.deptName,
                page: _this.pageIndex,
                length: _this.pageSize
            };

            _this.$http('dept/searchDeptByPage', 'POST', requestData, true, function(resp) {
                let page = resp.page;
                _this.dataList = page.list;
                _this.totalCount = page.totalCount;
                _this.dataListLoading = false;
            });
        },
		// 每页显示数变化时
		sizeChangeHandle: function(val) {
		    this.pageSize = val;
		    this.pageIndex = 1;
		    this.loadDataList();
		},
		// 当前页变化时
		currentChangeHandle: function(val) {
		    this.pageIndex = val;
		    this.loadDataList();
		},
		// 検索
        searchHandle: function() {
            this.$refs['dataForm'].validate(valid => {
                
				if (valid) {
					
                    this.$refs['dataForm'].clearValidate();
                    if (this.dataForm.deptName == '') {
                        this.dataForm.deptName = null;
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
		// 削除
		selectionChangeHandle: function(val) {
		    this.dataListSelections = val;
		},
        selectable: function(row, index) {
            if (row.emps > 0) {
                return false;
            }
            return true;
        },
        deleteHandle: function(id) {
            let _this = this;
            let ids = id ? [id] : _this.dataListSelections.map(item => {
				return item.id;
			});
			
            if (ids.length == 0) {
                _this.$message({
                    message: 'レコードが選択されませんでした。',
                    type: 'warning',
                    duration: 1200
                });
            } else {
                _this.$confirm(`レコードを削除してもよろしいでしょうか？`, '提示', {
                    confirmButtonText: '削除',
                    cancelButtonText: 'キャンセル',
                    type: 'warning'
                }).then(() => {
                    _this.$http('dept/deleteDeptByIds', 'POST', { ids: ids }, true, function(resp) {
                        if (resp.rows > 0) {
                            _this.$message({
                                message: '削除されました',
                                type: 'success',
                                duration: 1200,
                                onClose: () => {
                                    _this.loadDataList();
                                }
                            });
                        } else {
                            _this.$message({
                                message: '削除に失敗しました',
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

<style></style>

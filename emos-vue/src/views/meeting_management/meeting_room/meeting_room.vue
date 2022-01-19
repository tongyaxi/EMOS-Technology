<template>
    <div>
        <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm">
            <el-form-item prop="name">
                <el-input
                    v-model="dataForm.name"
                    placeholder="会議室名称"
                    size="medium"
                    class="input"
                    clearable="clearable"
                />
            </el-form-item>
			
            <el-form-item>
                <el-select v-model="dataForm.canDelete" class="input" placeholder="条件付き" size="medium">
                    <el-option label="全部" value="all" />
                    <el-option label="削除可能" value="true" />
                    <el-option label="削除不可" value="false" />
                </el-select>
            </el-form-item>
			
            <el-form-item>
                <el-button size="medium" type="primary" @click="searchHandle()">検索</el-button>
                <el-button
                    size="medium"
                    type="primary"
                    :disabled="!isAuth(['ROOT', 'MEETING_ROOM:INSERT'])"
                    @click="addHandle()"
                >
                    新規
                </el-button>
                <el-button
                    size="medium"
                    type="danger"
                    :disabled="!isAuth(['ROOT', 'MEETING_ROOM:DELETE'])"
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
            style="width: 100%;"
            size="medium"
        >
            <el-table-column type="selection" header-align="center" align="center" width="50" />
            <el-table-column type="index" header-align="center" align="center" width="100" label="番号">
                <template #default="scope">
                    <span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="name" header-align="center" align="center" min-width="150" label="会議室名称" />
            <el-table-column header-align="center" align="center" min-width="120" label="最大人数">
                <template #default="scope">
                    <span>{{ scope.row.max }}人</span>
                </template>
            </el-table-column>
            <el-table-column header-align="center" align="center" min-width="100" label="ステータス">
                <template #default="scope">
                    <span>{{ scope.row.status == 1 ? '利用可' : '利用不可' }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="desc" header-align="center" align="center" label="備考" min-width="400" />
            <el-table-column header-align="center" align="center" width="150" label="操作">
                <template #default="scope">
                    <el-button
                        type="text"
                        size="medium"
                        :disabled="!isAuth(['ROOT', 'MEETING_ROOM:UPDATE']) || scope.row.id == 0"
                        @click="updateHandle(scope.row.id)"
                    >
                        修正
                    </el-button>
                    <el-button
                        type="text"
                        size="medium"
                        :disabled="!isAuth(['ROOT', 'MEETING_ROOM:DELETE']) || scope.row.id == 0"
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
import AddOrUpdate from './meeting_room-add-or-update.vue';
export default {
    components: {
        AddOrUpdate
    },
    data: function() {
        return {
            dataForm: {
                name: null,
                canDelete: null
            },
            dataList: [],
            pageIndex: 1,
            pageSize: 10,
            totalCount: 0,
            dataListLoading: false,
            dataListSelections: [],
            addOrUpdateVisible: false,
            dataRule: {
                name: [{ required: false, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{1,20}$', message: '会議室名称を正しく入力してください。' }]
            }
        };
    },
	created: function() {
	    this.loadDataList();
	},
    methods: {
		// 初期化（ページデータ
        loadDataList: function() {
            let _this = this;
            _this.dataListLoading = true;
            let reqData = {
                name: _this.dataForm.name,
                canDelete: _this.dataForm.canDelete == 'all' ? null : _this.dataForm.canDelete,
                page: _this.pageIndex,
                length: _this.pageSize
            };

            _this.$http('meeting_room/searchMeetingRoomByPage', 'POST', reqData, true, function(resp) {
                let page = resp.page;
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
                    if (this.dataForm.name == '') {
                        this.dataForm.name = null;
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
		addHandle: function() {
		    this.addOrUpdateVisible = true;
		    this.$nextTick(() => {
		        this.$refs.addOrUpdate.init();
		    });
		},
		updateHandle: function(id) {
		    this.addOrUpdateVisible = true;
		    this.$nextTick(() => {
		        this.$refs.addOrUpdate.init(id);
		    });
		},
        selectionChangeHandle: function(val) {
            this.dataListSelections = val;
        },
        deleteHandle: function(id) {
            let that = this;
            let ids = id
                ? [id]
                : that.dataListSelections.map(item => {
                      return item.id;
                  });
            if (ids.length == 0) {
                that.$message({
                    message: '没有选中记录',
                    type: 'warning',
                    duration: 1200
                });
            } else {
                that.$confirm(`确定要删除选中的记录？`, '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    that.$http('meeting_room/deleteMeetingRoomByIds', 'POST', { ids: ids }, true, function(resp) {
                        if (resp.rows > 0) {
                            that.$message({
                                message: '操作成功',
                                type: 'success',
                                duration: 1200
                            });
                            that.loadDataList();
                        } else {
                            that.$message({
                                message: '未能删除记录',
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

<style lang="less"></style>

<template>
    <div v-if="isAuth(['ROOT', 'ROLE:SELECT'])">
        <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm">
            <el-form-item prop="roleName">
                <el-input
                    v-model="dataForm.roleName"
                    placeholder="役割名称"
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
                    :disabled="!isAuth(['ROOT', 'ROLE:INSERT'])"
                    @click="addHandle()"
                >
                    新規
                </el-button>
                <el-button
                    size="medium"
                    type="danger"
                    :disabled="!isAuth(['ROOT', 'ROLE:DELETE'])"
                    @click="deleteHandle()"
                >
                    一括削除
                </el-button>
            </el-form-item>
        </el-form>
		
        <el-table
            :data="roleList"
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
            <el-table-column prop="roleName" header-align="center" align="center" label="役割名称" min-width="180"/>
            <el-table-column header-align="center" align="center" label="権限数" min-width="140">
                <template #default="scope">
                    <span>{{ scope.row.permissions }}个</span>
                </template>
            </el-table-column>
            <el-table-column prop="users" header-align="center" align="center" label="関連ユーザー数" min-width="140">
                <template #default="scope">
                    <span>{{ scope.row.users }}人</span>
                </template>
            </el-table-column>
            <el-table-column prop="desc" header-align="center" align="center" label="備考" min-width="450" />
            <el-table-column prop="systemic" header-align="center" align="center" label="ディフォルト役割" min-width="150">
                <template #default="scope">
                    <span>{{ scope.row.systemic ? '〇' : '✕' }}</span>
                </template>
            </el-table-column>
            <el-table-column header-align="center" align="center" width="150" label="操作">
                <template #default="scope">
                    <el-button
                        type="text"
                        size="medium"
                        :disabled="!isAuth(['ROOT', 'ROLE:UPDATE']) || scope.row.id == 0"
                        @click="updateHandle(scope.row.id, scope.row.systemic)"
                    >
                        修正
                    </el-button>
                    <el-button
                        type="text"
                        size="medium"
                        :disabled="!isAuth(['ROOT', 'ROLE:DELETE']) || scope.row.systemic || scope.row.users > 0"
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
import AddOrUpdate from './role-add-or-update.vue';
export default {
    components: {
        AddOrUpdate
    },
	data: function() {
		return {
			dataForm: {
				roleName: null
			},
			roleList: [],
			dataListLoading: false,
			dataListSelections: [],
			pageIndex: 1,
			pageSize: 10,
			totalCount: 0,
			dataRule: {
				roleName: [{required: false, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{1,10}$', message: '正しく入力してください。'}]
			},
			addOrUpdateVisible: false
		}
	},
	created: function() {
		this.loadDataList();
	},
    methods: {
		// ページデータを検索する
		loadDataList: function() {
			let _this = this;
			this.dataListLoading = true;
			let data = {roleName: _this.dataForm.roleName,
						page: _this.pageIndex,
						length: _this.pageSize};
			_this.$http('role/searchRoleByPage', 'POST', data, true, function(resp) {
				_this.roleList = resp.page.list;
				_this.totalCount = resp.page.totalCount;
				_this.dataListLoading = false;
			});
			
		},
		// pageSizeを変更する場合
		sizeChangeHandle: function(val) {
			this.pageSize = val;
			this.pageIndex = 1;
			this.loadDataList();
		},
		// currentPageを変更する場合
		currentChangeHandle: function(val) {
			this.pageIndex = val;
			this.loadDataList();
		},
		// 検索ボタン処理
		searchHandle: function() {
			this.$refs['dataForm'].validate(valid => {
				if(valid) {
					this.$refs['dataForm'].clearValidate();
					if(this.pageIndex != 1) {
						this.pageIndex = 1;
					}
					if(this.dataForm.roleName == '') {
						this.dataForm.roleName = null;
					}
					this.loadDataList();
				}else {
					return false;
				}
			});
		},
		// 新規
		addHandle: function() {
			this.addOrUpdateVisible = true;
			this.$nextTick(() =>{
				this.$refs.addOrUpdate.init();
			});
		},
		// 更新
		updateHandle: function(roleId, systemic) {
			this.addOrUpdateVisible = true;
			this.$nextTick(() =>{
				this.$refs.addOrUpdate.init(roleId, systemic);
			});
		},
		// 削除
		selectionChangeHandle: function(val){
			this.dataListSelections = val;
		},
		selectable: function(row, index){
			// 如果是系统内置角色或者有关联的用户，禁止勾选
			if(row.systemic || row.users > 0){
				return false;
			}
			return true;
		},
		deleteHandle: function(id){
			let _this = this;
			let ids = id ? [id] : _this.dataListSelections.map(item => {
				return item.id;
			});
			if(ids.length == 0){
				_this.$message({
					message: 'レコードが選択されませんでした。',
					type: 'warning',
					duration: 1200
				});
			}else{
				_this.$confirm('レコードを削除してもよろしいでしょうか？','提示',{
					confirmButtonText: '削除',
					cancelButtonText: 'キャンセル',
					type: 'warning'
				}).then(() => {
					_this.$http('role/deleteRoleByIds', 'POST', { ids: ids }, true, function(resp) {
						if (resp.rows > 0) {
							_this.$message({
								message: '削除されました。',
								type: 'success',
								duration: 1200
							});
							_this.loadDataList();
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

<style></style>

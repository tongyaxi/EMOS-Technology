<template>
    <div v-if="isAuth(['ROOT', 'USER:SELECT'])">
        <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm">
            <el-form-item prop="name">
                <el-input
                    v-model="dataForm.name"
                    placeholder="姓名"
                    size="medium"
                    class="input"
                    clearable="clearable"
                />
            </el-form-item>
            <el-form-item>
                <el-select v-model="dataForm.sex" class="input" placeholder="性別" size="medium" clearable="clearable">
                    <el-option label="男" value="男" />
                    <el-option label="女" value="女" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-select v-model="dataForm.role" class="input" placeholder="役割" size="medium" clearable="clearable">
                    <el-option v-for="one in roleList" :label="one.roleName" :value="one.roleName" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-select
                    v-model="dataForm.deptId"
                    class="input"
                    placeholder="部門"
                    size="medium"
                    clearable="clearable"
                >
                    <el-option v-for="one in deptList" :label="one.deptName" :value="one.id" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-select
                    v-model="dataForm.status"
                    class="input"
                    placeholder="ステータス"
                    size="medium"
                    clearable="clearable"
                >
                    <el-option label="在職" value="1" />
                    <el-option label="退職" value="2" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button size="medium" type="primary" @click="searchHandle()">検索</el-button>
                <el-button
                    size="medium"
                    type="primary"
                    :disabled="!isAuth(['ROOT', 'USER:INSERT'])"
                    @click="addHandle()"
                >
                    追加
                </el-button>
                <el-button
                    size="medium"
                    type="danger"
                    :disabled="!isAuth(['ROOT', 'USER:DELETE'])"
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
            <el-table-column prop="username" header-align="center" align="center" min-width="100" label="姓名" />
            <el-table-column prop="sex" header-align="center" align="center" min-width="60" label="性別" />
            <el-table-column prop="tel" header-align="center" align="center" min-width="130" label="電話番号" />
            <el-table-column
                prop="email"
                header-align="center"
                align="center"
                min-width="240"
                label="メールアドレス"
                :show-overflow-tooltip="true"
            />
            <el-table-column prop="hiredate" header-align="center" align="center" min-width="130" label="入社日" />
            <el-table-column
                prop="roles"
                header-align="center"
                align="center"
                min-width="150"
                label="役割"
                :show-overflow-tooltip="true"
            />
            <el-table-column prop="dept" header-align="center" align="center" min-width="120" label="部門" />
            <el-table-column prop="status" header-align="center" align="center" min-width="100" label="ステータス" />
            <el-table-column header-align="center" align="center" width="150" label="操作">
                <template #default="scope">
                    <el-button
                        type="text"
                        size="medium"
                        v-if="isAuth(['ROOT', 'USER:UPDATE'])"
                        @click="updateHandle(scope.row.id)"
                    >
                        修正
                    </el-button>
                    <el-button
                        type="text"
                        size="medium"
                        v-if="isAuth(['ROOT', 'USER:UPDATE'])"
                        :disabled="scope.row.status == '退職' || scope.row.root"
                        @click="dimissHandle(scope.row.id)"
                    >
                        退職
                    </el-button>
                    <el-button
                        type="text"
                        size="medium"
                        :disabled="scope.row.root"
                        v-if="isAuth(['ROOT', 'USER:DELETE'])"
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
        <dimiss v-if="dimissVisible" ref="dimiss" @refreshDataList="loadDataList"></dimiss>
    </div>
</template>

<script>
// 用户管理页面引入弹窗页面
import AddOrUpdate from './user-add-or-update.vue';
import Dimiss from './dimiss.vue';
export default {
    components: {
		// 然后注册给用户管理页面，就可以编写<add-or-update/>标签
        AddOrUpdate,
        Dimiss
    },
    data() {
        return {
            dataForm: {
                name: '',
                sex: '',
                role: '',
                deptId: '',
                status: ''
            },
            dataList: [],
            roleList: [],
            deptList: [],
            pageIndex: 1,
            pageSize: 10,
            totalCount: 0,
            dataListLoading: false,
            dataListSelections: [],
            addOrUpdateVisible: false,
            dimissVisible: false,
            dataRule: {
                name: [{ required: false, pattern: '^[\u4e00-\u9fa5]{1,10}$', message: '姓名格式错误' }]
            }
        };
    },
    methods: {
        loadDataList: function() {
			let _this = this;
			_this.dataListLoading = true;
			let data = {
				page: _this.pageIndex,
				length: _this.pageSize,
				name: _this.dataForm.name,
				sex: _this.dataForm.sex,
				role: _this.dataForm.role,
				deptId: _this.dataForm.deptId,
				status: _this.dataForm.status
			}
			_this.$http("user/searchUserByPage", "POST", data, true, function(resp){
				let page = resp.page;
				let list = page.list;
				for(let one of list) {
					if(one.status == 1) {
						one.status = "在職";
					} else if(one.status == 2) {
						one.status = "退職";
					}
				}
				_this.dataList = list;
				_this.totalCount = page.totalCount;
				_this.dataListLoading = false;
			})
		},
		sizeChangeHandle(val) {
			this.pageSize = val;
			this.pageIndex = 1;
			this.loadDataList();
		},
		currentChangeHandle(val) {
			this.pageIndex = val;
			this.loadDataList();
		},
		loadRoleList: function() {
			let _this = this;
			_this.$http("role/searchAllRole", "GET", null, true, function(resp){
				_this.roleList = resp.allRoles;
			});
		},
		loadDeptList: function() {
			let _this = this;
			_this.$http("dept/searchAllDept", "GET", null, true, function(resp){
				_this.deptList = resp.allDept;
			});
		},
		// 检索
        searchHandle: function() {
			this.$refs['dataForm'].validate(valid => {
				if(valid) {
					this.$refs['dataForm'].clearValidate();
					if(this.dataForm.name == '') {
						// 后端在条件筛选时，用的!null的判断
						this.dataForm.name == null;
					}
					if(this.pageIndex != 1) {
						this.pageIndex = 1;
					}
					this.loadDataList();
				}else {
					return false;
				}
			});
		},
		// 追加
        addHandle: function() {
			// 显示弹窗
            this.addOrUpdateVisible = true;
            this.$nextTick(() => {
				// 调用引用名为标签上的'addOrUpdate'中的init函数
                this.$refs.addOrUpdate.init();
            });
        },
		// 修正
        updateHandle: function(id) {
            this.addOrUpdateVisible = true;
            this.$nextTick(() => {
                this.$refs.addOrUpdate.init(id);
            });
        },
		// 选中复选框触发（会选中整条记录
		selectionChangeHandle: function(val) {
			this.dataListSelections = val;
		},
		// 削除
        deleteHandle: function(id) {
			let _this = this;
			let ids = id ? [id] : _this.dataListSelections.map(item => {
				return item.id;
			});
			
			if(ids.length ==0) {
				_this.$mesage({
					message: '削除したいレコードを選択してください。',
					type: 'warning',
					duration: 1200
				});
			} else {
				_this.$confirm('削除してもよろしいでしょうか？','Warning',{
					confirmButtonText: '削除',
					cancelButtonText: 'キャンセル',
					type: 'warning'
				}).then(() => {
					
					_this.$http('user/delete','POST',{ids:ids},true,function(resp) {
						
						if(resp.rows > 0) {
							_this.$message({
								message: '削除しました。',
								type: 'success',
								duration: 1200
							});
							// 再読み込み
							_this.loadDataList();
							
						} else {
							_this.$message({
								message: '削除に失敗しました。',
								type: 'error',
								duration: 1200
							});
						}
					});
				});
			}
		}
    },
    created: function() {
        this.loadDataList();
        this.loadRoleList();
        this.loadDeptList();
    }
};
</script>

<style lang="less" scoped="scoped"></style>

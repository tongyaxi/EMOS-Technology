<template>
	<div>
		<el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm">
			
			<el-form-item prop="name">
				<el-input
					v-model="dataForm.name"
					placeholder="氏名"
					size="medium"
					class="input"
					clearable="clearable"
				/>
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
					v-model="dataForm.typeId"
					class="input"
					placeholder="罰金タイプ"
					size="medium"
					clearable="clearable"
				>
					<el-option v-for="one in amectTypeList" :label="one.type" :value="one.id" />
				</el-select>
			</el-form-item>
			
			<el-form-item>
				<el-date-picker
					v-model="dataForm.date"
					type="daterange"
					range-separator="~"
					start-placeholder="開始時間"
					end-placeholder="終了時間"
					size="medium"
				></el-date-picker>
			</el-form-item>
			
			<el-form-item>
				<el-select
					v-model="dataForm.status"
					class="input"
					placeholder="ステータス"
					size="medium"
					clearable="clearable"
				>
					<el-option label="未支払" value="1" />
					<el-option label="支払済" value="2" />
				</el-select>
			</el-form-item>
			
			<el-form-item>
				<el-button size="medium" type="primary" @click="searchHandle()">検索</el-button>
				
				<el-button
					size="medium"
					type="primary"
					:disabled="!isAuth(['ROOT', 'AMECT:INSERT'])"
					@click="addHandle()"
				>
					新規
				</el-button>
				
				<el-button
					size="medium"
					type="danger"
					:disabled="!isAuth(['ROOT', 'AMECT:DELETE'])"
					@click="deleteHandle()"
				>
					一括削除
				</el-button>
				
				<!-- <el-button
					size="medium"
					type="warning"
					:disabled="!isAuth(['ROOT', 'AMECT:SELECT'])"
					@click="reportHandle()"
				>
					レポート生成
				</el-button> -->
				
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
			
			<el-table-column width="40px" prop="reason" header-align="center" align="center" type="expand">
				<template #default="scope">
					罰金理由：{{ scope.row.reason }}
				</template>
			</el-table-column>
			
			<el-table-column type="index" header-align="center" align="center" width="100" label="番号">
				<template #default="scope">
					<span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
				</template>
			</el-table-column>
			
			<el-table-column prop="type" header-align="center" align="center" label="罰金タイプ" />
			
			<el-table-column prop="name" header-align="center" align="center" label="当事者" />
			
			<el-table-column prop="deptName" header-align="center" align="center" label="所属部門" />
			
			<el-table-column header-align="center" align="center" label="罰金金額">
				<template #default="scope">
					<span>{{ scope.row.amount }}円</span>
				</template>
			</el-table-column>
			
			<el-table-column prop="status" header-align="center" align="center" label="支払ステータス" />
			
			<el-table-column prop="createTime" header-align="center" align="center" label="作成日" />
			
			<el-table-column fixed="right" header-align="center" align="center" width="150" label="操作">
				<template #default="scope">
					
					<el-button
						type="text"
						size="medium"
						:disabled="!(isAuth(['ROOT', 'AMECT:UPDATE']) && scope.row.status != '支払済')"
						@click="updateHandle(scope.row.id)"
					>
						変更
					</el-button>
					
					<el-button
						type="text"
						size="medium"
						:disabled="!(isAuth(['ROOT', 'AMECT:DELETE']) && scope.row.status != '支払済')"
						@click="deleteHandle(scope.row.id)"
					>
						削除
					</el-button>
					
					<el-button
						type="text"
						size="medium"
						:disabled="!(scope.row.mine == 'true' && scope.row.status == '未支払')"
						@click="payHandle(scope.row.id)"
					>
						支払う
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
		<pay v-if="payVisible" ref="pay" @refreshDataList="loadDataList"></pay>
	</div>
</template>

<script>
import dayjs from 'dayjs';
import AddOrUpdate from './amect-add-or-update.vue';
import Pay from './amect-pay.vue';
export default {
	components: { AddOrUpdate, Pay },
	data: function() {
		return {
			dataForm: {
				name: null,
				deptId: null,
				typeId: null,
				status: null,
				date: null
			},
			deptList: [],
			amectTypeList: [],
			dataList: [],
			pageIndex: 1,
			pageSize: 10,
			totalCount: 0,
			dataListLoading: false,
			dataListSelections: [],
			dataRule: {
				name: [{ required: false, pattern: '^[\u4e00-\u9fa5]{1,10}$', message: '氏名を正しく入力してください。' }]
			},
			addOrUpdateVisible: false,
			payVisible: false
		};
	},
	created: function() {
		this.loadDeptList();
		this.loadAmectTypeList();
	    this.loadDataList();
	},
	methods: {
		// 部門情報を検索する
		loadDeptList: function() {
			let _this = this;
			_this.$http('dept/searchAllDept', 'GET', null, true, function(resp){
				_this.deptList = resp.allDept;
			});
		},
		// 罰金タイプを検索する
		loadAmectTypeList: function() {
			let _this = this;
			_this.$http('amect_type/searchAllAmectType', 'GET', {}, true, function(resp) {
				_this.amectTypeList = resp.list;
			});
		},
        loadDataList:function(){
			let _this = this;
			_this.dataListLoading = true;
            
			let reqData = {
				name: _this.dataForm.name,
				deptId: _this.dataForm.deptId,
				typeId: _this.dataForm.typeId,
				status: _this.dataForm.status,
				page: _this.pageIndex,
				length: _this.pageSize
				
			}
			
            if(_this.dataForm.date != null && _this.dataForm.date.length == 2){
                let startDate = _this.dataForm.date[0];
                let endDate = _this.dataForm.date[1];
				reqData.startDate = dayjs(startDate).format('YYYY-MM-DD');
                reqData.endDate = dayjs(endDate).format("YYYY-MM-DD");
            }
            _this.$http("amect/searchAmectByPage", "POST", reqData, true, function(resp){
                let page = resp.page
                for(let one of page.list){
                    if(one.status==1){
                        one.status="未支払"
                    }
                    else if(one.status==2){
                        one.status="払い済"
                    }
                }
                _this.dataList = page.list;
                _this.totalCount = page.totalCount;
                _this.dataListLoading = false;
            })
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
        searchHandle:function(){
            this.$refs["dataForm"].validate(valid=>{
                if(valid){
                    this.$refs['dataForm'].clearValidate();
                    if(this.dataForm.name == ""){
                        this.dataForm.name = null
                    }
                    if(this.pageIndex != 1){
                        this.pageIndex = 1
                    }
                    this.loadDataList()
                }else{
                    return false
                }
            })
        },
		// 新規
		addHandle: function(){
			this.addOrUpdateVisible = true;
			this.$nextTick(() => {
				this.$refs.addOrUpdate.init();
			});
		},
		// 変更
        updateHandle: function(id){
            this.addOrUpdateVisible = true;
			this.$nextTick(() => {
				this.$refs.addOrUpdate.init(id);
			});
        },
        selectable: function(row,index){
            if(row.status!="支払済"){
                return true;
            }
            return false;
        },
        selectionChangeHandle: function(val){
            this.dataListSelections = val;
        },
		// 削除
        deleteHandle:function(id){
            
			let _this = this;
			// 一括削除の場合、idはundefinedで渡す
			let ids = id ? [id] : _this.dataListSelections.map(item => {
				return item.id;
			});
			
            if(ids.length == 0){
                _this.$message({
                    message: '記録を選択してください。',
                    type: 'warning',
                    duration: 1200,
                });
            }else{
                _this.$confirm('削除してもよろしいでしょうか？','提示',{
                    confirmButtonText: '削除',
                    cancelButtonText: 'キャンセル',
                    type: 'warning'
                }).then(()=>{
                    _this.$http('amect/deleteAmectByIds', "POST", {ids:ids}, true, function(resp){
                        if(resp.rows>0){
                            _this.$message({
                                message: '削除に成功しました。',
                                type: 'success',
                                duration: 1200
                            });
                            _this.loadDataList();
                        }else{
                            _this.$message({
                                message: '削除に失敗しました。',
                                type: 'warning',
                                duration: 1200,
                            });
                        }
                    })
                })
            }
        },
        payHandle: function(id){
			this.payVisible = true;
			this.$nextTick(() => {
				this.$refs.pay.init(id);
			});
        },
        reportHandle: function() {
        }

	}
};
</script>

<style></style>

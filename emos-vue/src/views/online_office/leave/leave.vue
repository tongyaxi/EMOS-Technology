<template>
	<div>
		<el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm">
			<el-form-item prop="name">
				<el-input
					v-model="dataForm.name"
					class="input"
					placeholder="氏名"
					size="medium"
					:disabled="!isAuth(['ROOT', 'LEAVE:SELECT'])"
					clearable="clearable"
				></el-input>
			</el-form-item>
			<el-form-item>
				<el-select
					v-model="dataForm.deptId"
					class="input"
					placeholder="部門"
					size="medium"
					:disabled="!isAuth(['ROOT', 'LEAVE:SELECT'])"
					clearable="clearable"
				>
					<el-option v-for="one in deptList" :key="one.id" :label="one.deptName" :value="one.id" />
				</el-select>
			</el-form-item>
			<el-form-item>
				<el-date-picker
					v-model="dataForm.date"
					style="width: 160px;"
					type="date"
					size="medium"
					placeholder="日付"
					clearable="clearable"
				></el-date-picker>
			</el-form-item>
			<el-form-item>
				<el-select v-model="dataForm.type" class="input" placeholder="タイプ" size="medium" clearable="clearable">
					<el-option label="病気のため" value="1"></el-option>
					<el-option label="私用のため" value="2"></el-option>
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
					<el-option label="承認待ち" value="1"></el-option>
					<el-option label="OK" value="2"></el-option>
					<el-option label="NG" value="3"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item>
				<el-button @click="searchHandle()" type="primary" size="medium">検索</el-button>
				<el-button type="danger" size="medium" @click="addHandle()">休暇申請</el-button>
			</el-form-item>
		</el-form>
		<el-table
			:data="dataList"
			border="border"
			v-loading="dataListLoading"
			cell-style="padding: 4px 0"
			style="width: 100%;"
			size="medium"
		>
			<el-table-column width="40px" prop="reason" header-align="center" align="center" type="expand">
				<template #default="scope">
					理由：{{ scope.row.reason }}
				</template>
			</el-table-column>
			<el-table-column type="index" header-align="center" align="center" width="100" label="番号">
				<template #default="scope">
					<span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
				</template>
			</el-table-column>
			<el-table-column
				prop="name"
				header-align="center"
				align="center"
				label="氏名"
				min-width="150"
			></el-table-column>
			<el-table-column
				prop="deptName"
				header-align="center"
				align="center"
				label="部門"
				min-width="150"
			></el-table-column>
			<el-table-column
				prop="start"
				header-align="center"
				align="center"
				label="開始"
				min-width="180"
			></el-table-column>
			<el-table-column
				prop="end"
				header-align="center"
				align="center"
				label="終了"
				min-width="180"
			></el-table-column>
			<el-table-column
				prop="type"
				header-align="center"
				align="center"
				label="タイプ"
				min-width="150"
			></el-table-column>
			<el-table-column prop="status" header-align="center" align="center" label="ステータス" min-width="120">
				<template #default="scope">
					<span v-if="scope.row.status == '承認待ち'" style="color: orange;">{{ scope.row.status }}</span>
					<span v-if="scope.row.status == 'OK'" style="color: #17B3A3;">{{ scope.row.status }}</span>
					<span v-if="scope.row.status == 'NG'" style="color: #f56c6c;">{{ scope.row.status }}</span>
				</template>
			</el-table-column>
			<el-table-column header-align="center" align="center" width="120" label="休暇PDF" min-width="120">
				<template #default="scope">
					<el-button
						type="text"
						size="medium"
						v-if="scope.row.status == 'OK'"
						@click="pdfHandle(scope.row.id)"
					>
						休暇PDF
					</el-button>
				</template>
			</el-table-column>
			<el-table-column header-align="center" align="center" width="150" label="操作" min-width="120">
				<template #default="scope">
					<el-button
						type="text"
						size="medium"
						:disabled="scope.row.status == 'OK' || scope.row.mine != true"
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
		<leave-add v-if="addVisible" ref="add" @refreshDataList="loadDataList"></leave-add>
		<leave-pdf v-if="pdfVisible" ref="pdf" @refreshDataList="loadDataList"></leave-pdf>
	</div>
</template>

<script>
import dayjs from 'dayjs';
import LeaveAdd from './leave-add.vue';
import LeavePdf from './leave_pdf.vue';
export default {
	components: { LeaveAdd, LeavePdf },
	data: function() {
		return {
			pageIndex: 1,
			pageSize: 10,
			totalPage: 0,
			dataListLoading: false,
			dataListSelections: [],
			dataForm: {
				name: null,
				deptId: null,
				date: null,
				type: null,
				status: null
			},
			dataList: [],
			deptList: [],
			addVisible: false,
			pdfVisible: false,
			dataRule: {
				name: [{ required: false, pattern: '^[\u4e00-\u9fa5]{1,10}$', message: '姓名格式错误' }]
			}
		};
	},
	created: function() {
		this.loadDeptList();
		this.loadDataList();
	},
	methods: {
		// 部門情報
		loadDeptList: function() {
			let _this = this;
			_this.$http('dept/searchAllDept', 'GET', null, true, function(resp) {
				_this.deptList = resp.allDept;
			});
		},
		
		loadDataList: function() {
			
			let _this = this;
			_this.dataListLoading = true;
			
			let reqData = {
				name: _this.dataForm.name,
				deptId: _this.dataForm.deptId,
				date: _this.dataForm.date,
				type: _this.dataForm.type,
				status: _this.dataForm.status,
				page: _this.pageIndex,
				length: _this.pageSize
			};
			if (_this.dataForm.date != null && _this.dataForm.date != '') {
				reqData.date = dayjs(_this.dataForm.date).format('YYYY-MM-DD');
			}
			
			_this.$http('leave/searchLeaveByPage', 'POST', reqData, true, function(resp) {
				
				let page = resp.page;
				for (let one of page.list) {
					if (one.type == 1) {
						one.type = '病気のため';
					} else if (one.type == 2) {
						one.type = '私用のため';
					}
					if (one.status == 1) {
						one.status = '承認待ち';
					} else if (one.status == 2) {
						one.status = 'NG';
					} else if (one.status == 3) {
						one.status = 'OK';
					}
				}
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
		// 新規
		addHandle: function() {
			this.addVisible = true;
			this.$nextTick(() => {
				this.$refs.add.init();
			});
		},
		// 削除
		deleteHandle:function(id){
			let _this = this
			_this.$confirm(`削除してもよろしいでしょうか？`, '提示', {
				confirmButtonText: '削除',
				cancelButtonText: 'キャンセル',
				type: 'warning',
			}).then(() => {
				let data = {
					id:id
				}
				_this.$http("leave/deleteLeaveById","POST",data,true,function(resp){
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
			});
		},
		// PDFをダウンロードする
		pdfHandle: function(id){
			this.pdfVisible = true;
			this.$nextTick(() => {
			   this.$refs.pdf.init(id);
			});
		}
	}
};
</script>

<style lang="less" scoped=""></style>

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
					placeholder="タイプ"
					size="medium"
					clearable="clearable"
				>
					<el-option label="普通报销" value="1" />
					<el-option label="差旅报销" value="2" />
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
					<el-option label="待审批" value="1" />
					<el-option label="已否决" value="2" />
					<el-option label="已同意" value="3" />
					<el-option label="已归档" value="4" />
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
				<el-button size="medium" type="primary" @click="searchHandle()">検索</el-button>
				<el-button size="medium" type="primary" @click="addHandle()">新規</el-button>
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
			
			<el-table-column width="40px" prop="content" header-align="center" align="center" type="expand">
				<template #default="scope">
					<div class="reim-table">
						<div class="row">
							<div class="title">番号</div>
							<div class="title">タイプ</div>
							<div class="title">立替金項目</div>
							<div class="title">備考</div>
							<div class="title">金額</div>
						</div>
						<div class="row" v-for="(one, $index) in scope.row.content">
							<div class="col">{{ $index + 1 }}</div>
							<div class="col">{{ one.type }}</div>
							<div class="col">{{ one.title }}</div>
							<div class="col">{{ one.desc }}</div>
							<div class="col">￥{{ one.money }}</div>
						</div>
					</div>
				</template>
			</el-table-column>
			
			<el-table-column type="index" header-align="center" align="center" width="100" label="番号">
				<template #default="scope">
					<span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
				</template>
			</el-table-column>
			<el-table-column prop="type" header-align="center" align="center" label="タイプ" />
			<el-table-column prop="name" header-align="center" align="center" label="申込者" />
			<el-table-column prop="deptName" header-align="center" align="center" label="所属部門" />
			<el-table-column header-align="center" align="center" label="立替金金額">
				<template #default="scope">
					<span>{{ scope.row.amount }}円</span>
				</template>
			</el-table-column>
			<el-table-column header-align="center" align="center" label="借金">
				<template #default="scope">
					<span>{{ scope.row.anleihen }}円</span>
				</template>
			</el-table-column>
			<el-table-column header-align="center" align="center" label="残高">
				<template #default="scope">
					<span>{{ scope.row.balance }}円</span>
				</template>
			</el-table-column>
			<el-table-column prop="status" header-align="center" align="center" label="ステータス" />
			<el-table-column prop="createTime" header-align="center" align="center" label="申込日時" />
			<el-table-column fixed="right" header-align="center" align="center" width="150" label="操作">
				<template #default="scope">
					<el-button
						type="text"
						size="medium"
						@click="pdfHandle(scope.row.id)"
					>
						詳細はPDFへ
					</el-button>
					<el-button
						type="text"
						size="medium"
						:disabled="!(['審査待ち','審査却下'].includes(scope.row.status) && scope.row.mine=='true')"
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
	</div>
	<add v-if="addVisible" ref="add" @refreshDataList="loadDataList"></add>
	<reim-pdf v-if="pdfVisible" ref="pdf" @refreshDataList="loadDataList"></reim-pdf>
</template>

<script>
import Add from './reim-add.vue';
import dayjs from 'dayjs';
import ReimPdf from './reim_pdf.vue';
export default {
	components: { Add, ReimPdf },
	data: function() {
		return {
			dataForm: {
				name: null,
				deptId: null,
				status: null,
				typeId: null,
				date: null
			},
			deptList: [],
			dataList: [],
			pageIndex: 1,
			pageSize: 10,
			totalCount: 0,
			dataListLoading: false,
			dataListSelections: [],
			dataRule: {
				name: [{ required: false, pattern: '^[\u4e00-\u9fa5]{1,10}$', message: '氏名を正しく入力してください。' }]
			},
			addVisible: false,
			pdfVisible: false
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
				typeId: _this.dataForm.typeId,
				status: _this.dataForm.status,
				page: _this.pageIndex,
				length: _this.pageSize
			};
			
			if (_this.dataForm.date != null && _this.dataForm.date.length == 2) {
			        let startDate = _this.dataForm.date[0];
			        let endDate = _this.dataForm.date[1];
			        reqData.startDate = dayjs(startDate).format('YYYY-MM-DD');
			        reqData.endDate = dayjs(endDate).format('YYYY-MM-DD');
			    }
			
			    _this.$http('reim/searchReimByPage', 'POST', reqData, true, function(resp) {
			        let page = resp.page;
			        let status = {
			            1: '審査待ち',
			            2: '審査却下',
			            3: '審査可決',
			            4: 'アーカイブ済'
			        };
			        let type = { 1: '一般', 2: '出張' };
			        for (let one of page.list) {
			            one.status = status[one.status];
			            one.type = type[one.typeId];
			          	one.content = JSON.parse(one.content);
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
		deleteHandle: function(id) {
		    let _this = this;
		    _this.$confirm(`削除してもよろしいでしょうか？`, '提示', {
				confirmButtonText: '削除',
				cancelButtonText: 'キャンセル',
				type: 'warning',
		    }).then(() => {
		        _this.$http('reim/deleteReimById', 'POST', { id: id }, true, function(resp) {
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
		},
		// PDFをダウンロードする
		pdfHandle:function(id){
			this.pdfVisible = true;
			    this.$nextTick(() => {
			        this.$refs.pdf.init(id);
			});
		}
	}
};
</script>

<style lang="less" scoped="scoped">
@import url('reim.less');
</style>

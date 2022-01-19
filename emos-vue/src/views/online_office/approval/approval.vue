<template>
	<div v-if="isAuth(['ROOT', 'WORKFLOW:APPROVAL', 'FILE:ARCHIVE'])">
		<el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="searchHandle()">
			
			<el-form-item prop="creatorName">
				<el-input v-model="dataForm.creatorName" size="medium" placeholder="申請者" clearable="clearable" />
			</el-form-item>
			
			<el-form-item prop="type">
				<el-select v-model="dataForm.type" placeholder="会議タイプ" size="medium" clearable="clearable">
					<el-option label="会議申請" value="会議申請"></el-option>
					<el-option label="社員休暇" value="社員休暇"></el-option>
					<el-option label="立替金申請" value="立替金申請"></el-option>
				</el-select>
			</el-form-item>
			
			<el-form-item prop="instanceId">
				<el-input v-model="dataForm.instanceId" size="medium" placeholder="インスタンス番号" clearable="clearable" />
			</el-form-item>
			
			<el-form-item>
				<el-button type="primary" size="medium" @click="searchHandle()">検索</el-button>
			</el-form-item>
			
			<el-form-item class="mold">
				<el-radio-group v-model="dataForm.status" size="medium" @change="searchHandle()">
					<el-radio-button label="承認待ち"></el-radio-button>
					<el-radio-button label="申請履歴"></el-radio-button>
					<el-radio-button label="承認済み"></el-radio-button>
				</el-radio-group>
			</el-form-item>
		</el-form>
		
		<el-table
			ref="approvalTable"
			:data="dataList"
			border="border"
			v-loading="dataListLoading"
			cell-style="padding: 4px 0"
			size="medium"
			style="width: 100%;"
			@expand-change="expand"
			:row-key="getRowKeys"
			:expand-row-keys="expands"
		>
			<el-table-column prop="remark" header-align="center" align="center" type="expand">
				<template #default="scope">
					<div class="content-container">
						<table class="content-table">
							<tbody v-if="scope.row.type == '会议申请'">
								<tr>
									<th><span>タイトル</span></th>
									<td :title="scope.row.title">{{ scope.row.title }}</td>
								</tr>
								<tr>
									<th><span>内容</span></th>
									<td :title="content.desc">{{ content.desc }}</td>
								</tr>
								<tr>
									<th><span>日付</span></th>
									<td>{{ content.date }}&nbsp;&nbsp;&nbsp;{{ content.start }} ~ {{ content.end }}</td>
								</tr>
								<tr>
									<th><span>場所</span></th>
									<td>{{ content.place }}</td>
								</tr>
								<tr>
									<th><span>参加者</span></th>
									<td :title="content.members">{{ content.members }}</td>
								</tr>
								<tr>
									<th><span>審査進捗</span></th>
									<td>
										<span v-if="scope.row.status != '已结束'">審査待ち</span>
										<span v-if="scope.row.status == '已结束' && scope.row.result == '同意'">
											審査OK
										</span>
										<span v-if="scope.row.status == '已结束' && scope.row.result == '不同意'">
											審査NG
										</span>
									</td>
								</tr>
							</tbody>
							<tbody v-if="scope.row.type == '员工请假'">
								<tr>
									<th><span>理由</span></th>
									<td :title="content.reason">{{ content.reason }}</td>
								</tr>
								<tr>
									<th><span>タイプ</span></th>
									<td>{{ content.type == 1 ? '病気のため' : '私用のため' }}</td>
								</tr>
								<tr>
									<th><span>申込者</span></th>
									<td>{{ content.name }}</td>
								</tr>
								<tr>
									<th><span>開始時間</span></th>
									<td>{{ content.start }}</td>
								</tr>
								<tr>
									<th><span>終了時間</span></th>
									<td>{{ content.end }}</td>
								</tr>
								<tr>
									<th><span>審査結果</span></th>
									<td>
										<span v-if="scope.row.status != '已结束'">承認待ち</span>
										<span v-if="scope.row.status == '已结束' && scope.row.result == '同意'">
											OK
										</span>
										<span v-if="scope.row.status == '已结束' && scope.row.result == '不同意'">
											NG
										</span>
									</td>
								</tr>
							</tbody>
							<tbody v-if="scope.row.type == '报销申请'">
								<tr>
									<th><span>申込者</span></th>
									<td>{{ content.name }}</td>
								</tr>
								<tr>
									<th><span>金額</span></th>
									<td>{{ content.amount }}元</td>
								</tr>
								<tr>
									<th><span>借金</span></th>
									<td>{{ content.anleihen }}元</td>
								</tr>
								<tr>
									<th><span>実際金額</span></th>
									<td>{{ content.balance }}元</td>
								</tr>
								<tr>
									<th><span>タイプ</span></th>
									<td>{{ content.type }}</td>
								</tr>
								<tr>
									<th><span>審査結果</span></th>
									<td>
										<span v-if="scope.row.status != '已结束'">承認待ち</span>
										<span v-if="scope.row.status == '已结束' && scope.row.result == '同意'">
											OK
										</span>
										<span v-if="scope.row.status == '已结束' && scope.row.result == '不同意'">
											NG
										</span>
									</td>
								</tr>
							</tbody>
						</table>
						<el-image
							class="archive"
							v-if="content.hasOwnProperty('files')"
							:src="content.files[0].url"
							:preview-src-list="archiveList"
						></el-image>
						<el-image class="bpmn" :src="bpmnUrl" :preview-src-list="bpmnList"></el-image>
					</div>
				</template>
			</el-table-column>
			<el-table-column type="index" header-align="center" align="center" label="番号" width="100" />
			<el-table-column prop="title" header-align="center" align="center" label="審査事項" min-width="400" />
			<el-table-column prop="type" header-align="center" align="center" label="タイプ" min-width="180" />
			<el-table-column prop="creatorName" header-align="center" align="center" label="申請者" min-width="150" />
			<el-table-column prop="createDate" header-align="center" align="center" label="申込日時" min-width="180" />
			<el-table-column prop="status" header-align="center" align="center" label="ステータス" min-width="150">
				<template #default="scope">
					<span v-if="scope.row.status != '已结束'" style="color: orange;">承認待ち</span>
					<span v-if="scope.row.status == '已结束' && scope.row.result == '同意'" style="color: #17B3A3;">
						OK
					</span>
					<span v-if="scope.row.status == '已结束' && scope.row.result == '不同意'" style="color: #f56c6c;">
						NG
					</span>
				</template>
			</el-table-column>
			<el-table-column header-align="center" align="center" width="150" label="操作">
				<template #default="scope">
					<el-button
						type="text"
						size="medium"
						v-if="isAuth(['ROOT', 'WORKFLOW:APPROVAL']) && dataForm.status == '承認待ち' && !scope.row.filing"
						@click="approveHandle(scope.row.taskId)"
					>
						承認
					</el-button>
					<el-button
						type="text"
						size="medium"
						v-if="dataForm.status != '承認待ち'"
						@click="viewHandle(scope.row)"
					>
						詳細
					</el-button>
					<el-button
						type="text"
						size="medium"
						v-if="isAuth(['ROOT', 'FILE:ARCHIVE']) && scope.row.filing"
						@click="archive(scope.row.taskId)"
					>
						アーカイブ
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
		<archive v-if="archiveVisible" ref="archive" @refreshDataList="loadDataList"></archive>
	</div>
</template>

<script>
import Archive from './archive.vue';
export default {
    components: {
        Archive
    },
    data: function() {
        return {
            pageIndex: 1,
            pageSize: 10,
            totalPage: 0,
            dataListLoading: false,
            dataForm: {
                creatorName: null,
                type: null,
                status: '承認待ち',
                instanceId: null
            },
			// 審査事項情報の配列
            dataList: [],
			// 会議詳細・審査進捗状況
            content: {},
			bpmnUrl: null,
			bpmnList: [],
			archiveVisible: false,
			archiveList: [],
            dataRule: {
                creatorName: [{ required: false, pattern: '^[\u4e00-\u9fa5]{2,20}$', message: '申請者を正しく入力してください。' }],
                instanceId: [{ required: false, pattern: '^[0-9A-Za-z\\-]{36}$', message: '正しく入力してください。' }]
            },
			expands: [], //  如果数组中保存了某行记录的唯一值，那么这行的折叠面板展开
			getRowKeys(row){ // 返回每一行的唯一值
				return row.taskId;
			}
        };
    },
    methods: {
        // 获取数据列表
        loadDataList: function() {
			let _this = this;
			_this.dataListLoading = true;
			
			let reqData = {
				creatorName: _this.dataForm.creatorName,
				type: _this.dataForm.type,
				status: _this.dataForm.status,
				instanceId: _this.dataForm.instanceId,
				page: _this.pageIndex,
				length: _this.pageSize
			};
			
			// ================================================
			if(_this.dataForm.type == '会議申請'){
				reqData.type = '会议申请';
			}
			if(_this.dataForm.type == '社員休暇'){
				reqData.type = '员工请假';
			}
			if(_this.dataForm.type == '立替金申請'){
				reqData.type = '报销申请';
			}
			if(_this.dataForm.status == '承認待ち'){
				reqData.status = '待审批';
			}
			if(_this.dataForm.status == '申請履歴'){
				reqData.status = '已审批';
			}
			if(_this.dataForm.status == '承認済み'){
				reqData.status = '已结束';
			}
            // ================================================

            _this.$http('approval/searchTaskByPage', 'POST', reqData, true, function(resp) {
                let page = resp.page;
                _this.dataList = page.list;
                _this.totalCount = page.totalCount;
                _this.dataListLoading = false;
            });
        },
		sizeChangeHandle: function(val){
			this.pageSize = val;
			this.pageIndex = 1;
			this.loadDataList();
		},
		currentChangeHandle: function(val){
			this.pageIndex = val;
			this.loadDataList();
		},
		// 検索
		searchHandle: function() {
		    this.$refs['dataForm'].validate(valid=>{
				if(valid){
					this.$refs['dataForm'].clearValidate();
					if(this.dataForm.creatorName == ''){
						this.dataForm.creatorName = null;
					}
					if(this.dataForm.instanceId == ''){
						this.dataForm.instanceId = null;
					}
					if(this.pageIndex != 1){
						this.pageIndex = 1;
					}
					this.loadDataList();
				}else{
					return false;
				}
			});
		},
		//点击折叠按钮的时候触发事件的回调函数,在回调函数中发送Ajax请求.
		expand: function(row, expandedRows){
			
			let _this = this;
			// row:触发折叠按钮的当前行记录
			// expandedRows:数组，长度若>0，说明面板展开状态
			if(expandedRows.length > 0){
				// 关闭所有的折叠面板
				_this.expands = [];
				if(row){
					//展开你想展开的折叠面板
					_this.expands.push(row.taskId);
					
					let reqData = {
						type: row.type,
						instanceId: row.processId,
						status: row.status
					};
					
					_this.$http("approval/searchApprovalContent", "POST", reqData, false, function(resp){
						let content = resp.content;
						_this.content = content;
						
						//如果返回工作流实例存在files，说明工作流实例绑定了归档文件
						if (content.hasOwnProperty('files')) {
							for (let one of content.files) {
								_this.archiveList.push(one.url);
							}
						}
						
						if(row.type == "报销申请"){
							if(content.typeId == 1){
								content.type = "普通报销";
							} else if(content.typeId == 2){
								content.type = "差旅报销";
							}
						}
					});
					
					_this.bpmnUrl = _this.$baseUrl +'approval/searchApprovalBpmn' +
					                       '?instanceId=' + row.processId +
					                       '&time=' + new Date().getTime();
					_this.bpmnList = [_this.bpmnUrl];
				}
			}else{
				// 默认不展开
				_this.expands = [];
			}
		},
		// 审批
		approveHandle: function(taskId){
			let _this = this;
			_this.$confirm('該当の会議申請を審査してください。', '提示', {
				confirmButtonText: 'OK',
				cancelButtonText: 'NG',
				type: 'warning',
				// クローズアイコンを消す
				distinguishCancelAndClose: true,
				callback: function(action){
					if(action == 'confirm'){
						_this.approval(taskId, 'OK');
					}else if(action == 'cancel'){
						_this.approval(taskId, 'NG');
					}
				}
			});
		},
		approval: function(taskId, result){
			let _this = this;
			_this.dataListLoading = true;
			
			//=====================
			if(result == 'OK'){
				result = '同意';
			}else if(result == 'NG'){
				result = '不同意';
			}
			//=====================
			let reqData = {
				taskId: taskId,
				approval: result
			};
			_this.$http('approval/approvalTask', 'POST', reqData, true, function(){
				_this.pageIndex = 1;
				_this.loadDataList();
			});
		},
		viewHandle: function(row) {
					this.$refs.approvalTable.toggleRowExpansion(row, true);
		},
		archive: function(taskId) {
			this.archiveVisible = true;
			this.$nextTick(() => {
				this.$refs.archive.init(taskId);
			});
		}
    },
    created: function() {
        this.loadDataList();
    }
};
</script>

<style lang="less" scoped="scoped">
@import url('approval.less');
</style>

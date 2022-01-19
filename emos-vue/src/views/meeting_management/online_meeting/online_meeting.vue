<template>
	<div>
		<el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm" class="form">
			
			<el-form-item prop="date">
				<el-date-picker
					v-model="dataForm.date"
					type="date"
					placeholder="日付を選択して"
					class="input"
					size="medium"
				></el-date-picker>
			</el-form-item>
			
			<el-form-item>
				<el-button size="medium" type="primary" @click="searchHandle()">検索</el-button>
				<el-button size="medium" type="danger" @click="addHandle()">会議予約</el-button>
			</el-form-item>
			
			<el-form-item class="mold">
				<el-radio-group v-model="dataForm.mold" size="medium" @change="changeHandle">
					<el-radio-button label="全て会議"></el-radio-button>
					<el-radio-button label="自分会議"></el-radio-button>
				</el-radio-group>
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
			<el-table-column width="40px" prop="desc" header-align="center" align="center" type="expand">
				<template #default="scope">
					会議概要：{{ scope.row.desc }}
				</template>
			</el-table-column>
			
			<el-table-column type="index" header-align="center" align="center" width="100" label="番号">
				<template #default="scope">
					<span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
				</template>
			</el-table-column>
			
			<el-table-column prop="title" header-align="center" align="center" label="タイトル" min-width="400" />
			<el-table-column prop="creatorName" header-align="center" align="center" min-width="150" label="申請者" />
			<el-table-column prop="date" header-align="center" align="center" min-width="150" label="日付" />
			
			<el-table-column header-align="center" align="center" min-width="150" label="時間">
				<template #default="scope">
					<span>{{ scope.row.start }} ~ {{ scope.row.end }}</span>
				</template>
			</el-table-column>
			
			<el-table-column prop="num" header-align="center" align="center" min-width="100" label="人数" />
			<el-table-column prop="status" header-align="center" align="center" min-width="100" label="ステータス" />
			
			<el-table-column header-align="center" align="center" width="150" label="操作">
				<template #default="scope">
					<el-button
						type="text"
						size="medium"
						:disabled="!scope.row.canEnterMeeting"
						@click="enterHandle(scope.row.id, scope.row.uuid)"
					>
						入る
					</el-button>
					
					<el-button
						type="text"
						size="medium"
						:disabled="
							!(
								(scope.row.status == '審査待ち' || scope.row.status == '未開始') &&
								scope.row.isCreator == 'true'
							)
						"
						@click="deleteHandle(scope.row)"
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
		<add v-if="addVisible" ref="add" @refresh="refresh"></add>
	</div>
</template>

<script>
import Add from './online_meeting-add.vue';
import dayjs from 'dayjs';

export default {
	components: { Add },
	data: function() {
		return {
			dataForm: {
				date: null,
				mold: '全て会議'
			},
			dataList: [],
			pageIndex: 1,
			pageSize: 10,
			totalCount: 0,
			dataListLoading: false,
			addVisible: false,
			dataRule: {}
		};
	},
	methods: {
		loadDataList: function() {
			let _this = this;
			_this.dataListLoading = true;
			let reqData = {
				mold: _this.dataForm.mold,
				page: _this.pageIndex,
				length: _this.pageSize
			}
			if(_this.dataForm.date != null && _this.dataForm.date != '') {
				reqData.date = dayjs(_this.dataForm.date).format("YYYY-MM-DD");
			}
			
			_this.$http("meeting/searchOnlineMeetingByPage", "POST", reqData, true, function(resp){
				let page = resp.page
				
				for(let one of page.list){
					if(one.status == 1){
						one.status = "審査待ち";
					}else if(one.status == 3){
						one.status = "未開始";
					}else if(one.status == 4){
						one.status = "会議中";
					}else if(one.status == 5){
						one.status = "会議終了";
					}
					
					// 计算会议是否可以进入
					// 1.开会前15分钟可以进入会议
					// 2.会议状态应为3：未开始 4：会议中
					let minute = dayjs(new Date()).diff(dayjs(`${one.date} ${one.start}`), 'minute');
					if (one.mine == 'true' && ((minute >= -15 && minute <= 0 && one.status == '未開始') || one.status == '会議中')) {
						one.canEnterMeeting = true;
					} else {
						one.canEnterMeeting = false;
					}
				}
				_this.dataList = page.list;
				_this.totalCount = page.totalCount;
				_this.dataListLoading = false;
			});
		},
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
		changeHandle: function(val) {
			this.searchHandle();
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
		addHandle: function() {
			this.addVisible = true;
			this.$nextTick(() => {
				this.$refs.add.init();
			});
		},
		deleteHandle: function(json) {
			let _this = this;
			_this.$confirm('会議を削除してもよろしいでしょうか?', '提示', {
				confirmButtonText: '削除',
				cancelButtonText: 'キャンセル',
				type: 'warning'
			}).then(() => {
				let reqData = {
					id: json.id,
					uuid: json.uuid,
					instanceId: json.instanceId,
					reason: '会議削除'
				};
				_this.$http('meeting/deleteMeetingApplication', 'post', reqData, true, function(resp) {
					if (resp.rows == 1) {
						_this.$message({
							message: '会議が削除されました。',
							type: 'success',
							duration: 1200
						});
						_this.searchHandle();
					} else {
						_this.$message({
							message: '会議を削除するには失敗しました。',
							type: 'error',
							duration: 1200
						});
					}
				});
			});
		},
		// 会議室に入る
		enterHandle: function(id, uuid){
			let _this = this;
			_this.$router.push({name: 'MeetingVideo', params: {meetingId: id, uuid: uuid}});
		},
		refresh: function() {
			this.$refs['dataForm'].resetFields();
			this.loadDataList();
		}
	},
	created: function() {
		this.loadDataList();
	}
};
</script>

<style lang="less" scoped="scoped">
@import url('online_meeting.less');
</style>

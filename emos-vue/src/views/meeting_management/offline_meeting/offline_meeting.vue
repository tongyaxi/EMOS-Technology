<template>
    <div>
        <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm" class="form">
            
			<el-form-item prop="name">
                <el-select
                    v-model="dataForm.name"
                    class="input"
                    placeholder="会議室を選択してください."
                    size="medium"
                    clearable="clearable"
                >
                    <el-option v-for="one in roomList" :label="one.name" :value="one.name" />
                </el-select>
            </el-form-item>
            <el-form-item prop="date">
                <el-date-picker
                    v-model="dataForm.date"
                    type="date"
                    placeholder="日付"
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
		
        <div class="gantt" ref="gantt" v-show="mode == 'gantt'">
            <div class="row">
                <div class="cell-time"></div>
                <div class="cell-time" v-for="one in time">
                    <span class="time">{{ one }}</span>
                </div>
            </div>
            <div class="row" v-for="room in gantt.meetingRoom">
                <div class="cell room">{{ room.name }}</div>
                <div class="cell" v-for="one in time">
                    <div
                        v-if="room.meeting.hasOwnProperty(one)"
                        class="meeting"
                        :class="room.meeting[one].split('#')[1]"
                        :style="'width:' + room.meeting[one].split('#')[0] * gantt.cellWidth + 'px'"
                    ></div>
                </div>
            </div>
        </div>
		
        <el-pagination
            v-show="mode == 'gantt'"
            @size-change="sizeChangeHandle"
            @current-change="currentChangeHandle"
            :current-page="pageIndex"
            :page-sizes="[10, 20, 50]"
            :page-size="pageSize"
            :total="totalCount"
            layout="prev, pager, next"
        />

        <div class="calendar" v-show="mode == 'calendar'">
            <div class="row">
                <div class="cell">日付</div>
                <div class="cell" v-for="one in calendar.days">{{ one.date }}（{{ one.day }}）</div>
            </div>
            <div class="row" v-for="(one, index) in time">
                <div class="cell-time" v-if="time[index + 1] != null">{{ one }} ~ {{ time[index + 1] }}</div>
                <div class="cell" v-for="day in calendar.days" v-if="time[index + 1] != null">
                    <div
                        class="meeting"
                        v-if="calendar.map.hasOwnProperty(`${day.date}#${one}`)"
                        :style="
                            'height:' +
                                calendar.map[day.date + '#' + one].time * 31 +
                                'px; line-height:' +
                                calendar.map[day.date + '#' + one].time * 31 +
                                'px'
                        "
                        :ref="`${day.date}#${one}`"
                        @click="
                            infoHandle(calendar.map[day.date + '#' + one].id, calendar.map[day.date + '#' + one].status)
                        "
                    >
                        <SvgIcon
                            name="close"
                            class="icon-svg-close"
                            v-if="
                                calendar.map[`${day.date}#${one}`].isCreator == 'true' &&
                                    [1, 3].includes(calendar.map[`${day.date}#${one}`].status)
                            "
                            @click.stop="deleteHandle(`${day.date}#${one}`)"
                        />
                        {{ calendar.map[`${day.date}#${one}`].title }}
                    </div>
                </div>
            </div>
        </div>
        <add v-if="addVisible" ref="add" @refresh="refresh"></add>
        <info v-if="infoVisble" ref="info"></info>
    </div>
</template>

<script>
import SvgIcon from '../../../components/SvgIcon.vue';
import dayjs from 'dayjs';
import Add from './offline_meeting-add.vue';
import Info from './offline_meeting-info.vue';
export default {
    components: { SvgIcon, Add, Info },
    data: function() {
        return {
            time: [
                '08:30',
                '09:00',
                '09:30',
                '10:00',
                '10:30',
                '11:00',
                '11:30',
                '12:00',
                '12:30',
                '13:00',
                '13:30',
                '14:00',
                '14:30',
                '15:00',
                '15:30',
                '16:00',
                '16:30',
                '17:00',
                '17:30',
                '18:00',
                '18:30'
            ],
            gantt: {
                meetingRoom: [],
                cellWidth: 0 // 每一个单元格的宽度(动态计算)
            },
			// 周日历
            calendar: {
                map: {},
                days: []
            },
			// 会议室下拉列表
            roomList: [],
            dataForm: {
                name: null,
                date: null,
                mold: '全て会議'
            },
            pageIndex: 1,
            pageSize: 10,
            totalCount: 0,
            dataListLoading: false,
			// 弹窗
            addVisible: false,
            infoVisble: false,
			// 不需要键盘录入，因此不需要写规则
            dataRule: {},
			// 显示界面(日程表/周日历)
            mode: 'gantt'
        };
    },
    methods: {
		loadDataList:function(){
			let _this = this;
			_this.dataListLoading = true;
			
			let reqData = {
			    name: _this.dataForm.name,
			    mold: _this.dataForm.mold,
			    page: _this.pageIndex,
			    length: _this.pageSize
			};
			if(_this.dataForm.date == null || _this.dataForm.date==""){
				reqData.date = dayjs(new Date()).format("YYYY-MM-DD")
			}else{
				reqData.date = dayjs(_this.dataForm.date).format("YYYY-MM-DD")
			}
			_this.$http("meeting/searchOfflineMeetingByPage", "POST", reqData, true, function(resp){
				let page = resp.page
				// 对返回的list需要做二次加工
				let temp = []
				
				for(let room of page.list){
					let json = {}
					json.name = room.name
					json.meeting = {}
					
					if(room.hasOwnProperty("meeting")){
						
						for(let meeting of room.meeting){
							
							let color = null
							if(meeting.status == 1){
								color="yellow";
							}else if (meeting.status == 3) {
								color = 'blue';
							}else if (meeting.status == 4) {
								color = 'pink';
							}else if (meeting.status == 5) {
								color = 'gray';
							}
							json.meeting[meeting.start] = meeting.time + "#" + color
						}
					}
					temp.push(json)
				}
				
				_this.gantt.meetingRoom = temp
				_this.totalCount = page.totalCount
				_this.dataListLoading = false
			})
		},
		// 検索
		searchHandle:function(){
			let _this = this
			
			// 没有按照会议室名字查询，显示甘特图。
			if(_this.dataForm.name == null || _this.dataForm.name == ""){
				_this.$refs["dataForm"].validate(valid=>{
					if(valid){
						_this.$refs["dataForm"].clearValidate()
						if(_this.pageIndex != 1){
							_this.pageIndex = 1
						}
						_this.loadDataList();
						_this.mode = "gantt"
					}else{
						return false;
					}
				})
				
			}else{ // 否则显示周日历
				
				_this.$refs["dataForm"].validate(valid=>{
					if(valid){
						_this.$refs["dataForm"].clearValidate()
						let reqDate={
							name: _this.dataForm.name,
							mold: _this.dataForm.mold
						}
						if(_this.dataForm.date != null && _this.dataForm.date != ""){
							_this.date = dayjs(_this.dataForm.date).format("YYYY-MM-DD")
						}
						_this.$http("meeting/searchOfflineMeetingInWeek","POST",reqDate,true,function(resp){
							let map = {};
							// 视图层输出会议卡片做判断遍历数组太麻烦，所以把周日历数据从数组转换成JSON对象
							for(let one of resp.list){
								map[`${one.date}#${one.start}`] = one
							}
							_this.calendar.map = map;
							_this.calendar.days = resp.days;
							_this.mode = "calendar";
						})
						
					}else{
						return false
					}
				});
			}
		},
		// MOLDを選択する。全ての会議、自分の会議を切り替え
		changeHandle:function(){
			this.searchHandle()
		},
		// 表示サイズの変更
		sizeChangeHandle:function(val){
			this.pageSize = val
			this.pageIndex = 1
			this.loadDataList()
		},
		// 当画面の変更
		currentChangeHandle:function(val){
			this.pageIndex = val
			this.loadDataList()
		},
		// 	会議予約
		addHandle:function(){
			this.addVisible = true
			this.$nextTick(()=>{
				this.$refs.add.init()
			})
		},
		// 再読み込み
		refresh:function(){
			this.mode="gantt"
			this.$refs["dataForm"].resetFields();
			this.loadDataList()
		},
		// 加载会议概要
		infoHandle:function(id,status){
			this.infoVisble = true
			this.$nextTick(()=>{
				this.$refs.info.init(id,status);
			})
		},
		// 削除
		deleteHandle:function(key){
			let _this = this;
			_this.$confirm('会議を削除してもよろしいでしょうか?', '提示', {
				confirmButtonText: '削除',
				cancelButtonText: 'キャンセル',
				type: 'warning'
			}).then(()=>{
				let json = _this.calendar.map[key]
				let reqData = {
					id: json.id,
					uuid: json.uuid,
					instanceId: json.instanceId,
					reason: "会議削除"
				}
				_this.$http("meeting/deleteMeetingApplication","POST",reqData,true,function(resp){
					if(resp.rows==1){
						_this.$message({
							message: '会議が削除されました。',
						    type: 'success',
						    duration: 1200
						});
						_this.searchHandle()
					}
					else{
						_this.$message({
							message: '会議を削除するには失敗しました。',
							type: 'error',
							duration: 1200
						});
					}
				})
			})
		},
		backHandle: function() {
		    let that = this;
		    that.mode = 'gantt';
		}
        
    },
    mounted: function() {
        let that = this;
        //根据实际情况设置甘特图每个单元格应该有的宽度
        let rowWidth = that.$refs['gantt'].offsetWidth - 1;
        let cellWidth = rowWidth * 0.042 + 0.01;
        that.gantt.cellWidth = cellWidth;

        //当浏览器窗口尺寸改变的时候，重新设置甘特图单元格的宽度
        window.addEventListener('resize', () => {
            let rowWidth = that.$refs['gantt'].offsetWidth - 1;
            let cellWidth = rowWidth * 0.042 + 0.01;
            that.gantt.cellWidth = cellWidth;
        });
    },
    created: function() {
        //加载会议室列表
        let that = this;

        that.$http('meeting_room/searchAllMeetingRoom', 'GET', null, true, function(resp) {
            that.roomList = resp.list;
        });

        //加载日程表分页数据
        that.loadDataList();
    }
};
</script>

<style lang="less" scoped="scoped">
@import url('offline_meeting.less');
</style>

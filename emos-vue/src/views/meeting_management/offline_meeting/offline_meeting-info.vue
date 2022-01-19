<template>
    <el-dialog title="会議概要" :close-on-click-modal="false" v-model="visible" width="630px">
        <div>
            <div class="banner"></div>
            <el-row class="info">
                <el-col :span="3" class="label">タイトル：</el-col>
                <el-col :span="21">
                    <span class="value">{{ title }}</span>
                </el-col>
            </el-row>
            <el-row class="info">
                <el-col :span="3" class="label">日付：</el-col>
                <el-col :span="9" class="value">{{ date }}</el-col>
                <el-col :span="3" class="label">場所：</el-col>
                <el-col :span="9" class="value">{{ place }}</el-col>
            </el-row>
            <el-row class="info">
                <el-col :span="3" class="label">時間：</el-col>
                <el-col :span="9" class="value">{{ start }} ~ {{ end }}</el-col>
                <el-col :span="3" class="label">ステータス：</el-col>
                <el-col :span="9" class="value">{{ status }}</el-col>
            </el-row>
            <el-row class="info member" v-if="['審査待ち', '未開始'].includes(status)">
                <el-col :span="3" class="label">メンバーズ：</el-col>
                <el-col :span="21" class="value">
                    <ul class="list">
                        <li class="item" v-for="one in members">
                            <img :src="one.photo" class="photo" />
                            <span class="name">{{ one.name }}</span>
                        </li>
                    </ul>
                </el-col>
            </el-row>
            <el-row class="info member" v-if="['会議中', '会議終了'].includes(status)">
                <el-col :span="3" class="label">参加者：</el-col>
                <el-col :span="21" class="value">
                    <ul class="list">
                        <li class="item" v-for="one in present">
                            <img :src="one.photo" class="photo" />
                            <span class="name">{{ one.name }}</span>
                        </li>
                    </ul>
                </el-col>
            </el-row>
            <el-row class="info member" v-if="['会議中', '会議終了'].includes(status)">
                <el-col :span="3" class="label">欠席者：</el-col>
                <el-col :span="21" class="value">
                    <ul class="list">
                        <li class="item" v-for="one in unpresent">
                            <img :src="one.photo" class="photo" />
                            <span class="name">{{ one.name }}</span>
                        </li>
                    </ul>
                </el-col>
            </el-row>
        </div>
        <template #footer>
            <span class="dialog-footer"><el-button size="medium" @click="visible = false">クローズ</el-button></span>
        </template>
    </el-dialog>
</template>

<script>
export default {
    data: function() {
        return {
            visible: false,
            title: null,
            date: null,
            place: null,
            start: null,
            end: null,
            members: [],
            present: [],
            unpresent: [],
            status: null
        };
    },
    methods: {
        init: function(id,status){
			console.log(id + ":" + status)
			let _this = this;
			_this.visible = true
			_this.$nextTick(()=>{
				let reqData={
					id:id,
					status:status
				}
				_this.$http("meeting/searchMeetingInfo","POST",reqData,true,function(resp){
					_this.title = resp.title
					_this.date = resp.date;
					_this.place = resp.place;
					_this.start = resp.start;
					_this.end = resp.end;
					if(resp.status == 1){
						_this.status = "審査待ち"
					}else if(resp.status == 3){
						_this.status = "未開始"
					}else if(resp.status == 4){
						_this.status = "会議中"
					}else if(resp.status == 5){
						_this.status = "会議終了"
					}
					if(resp.hasOwnProperty("members")){
						_this.members = JSON.parse(resp.members)
					}
					if(resp.hasOwnProperty("present")){
						_this.present = JSON.parse(resp.present)
					}
					if(resp.hasOwnProperty("unpresent")){
						_this.unpresent = JSON.parse(resp.unpresent)
					}
				})
			})
		}
    }
};
</script>

<style lang="less" scoped="scoped">
@import url('offline_meeting-info.less');
</style>

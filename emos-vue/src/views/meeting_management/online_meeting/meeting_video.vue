<template>
	<div>
		<el-row :gutter="15">
			<!-- 视频墙 -->
			<el-col :span="19">
				<div class="meeting-container">
					<el-scrollbar height="650px" id="videoListContainer">
						
						<div class="video-list">
							
							<div class="video">
								<div class="user">
									<img class="photo" :src="mine.photo" />
									<span class="name">{{ mine.name }}{{ shareStatus ? '（共享中）' : '' }}</span>
								</div>
								<div id="localStream"></div>
							</div>
							
							<div class="video" v-for="one in memberList">
								<div class="user">
									<img class="photo" :src="one.photo" />
									<span class="name">{{ one.name }}</span>
								</div>
								<div :id="one.id" class="remote-stream" @dblclick="bigVideoHandle(one.id)"></div>
							</div>
							
						</div>
						
					</el-scrollbar>
					
					<div id="videoBig" @dblclick="smallVideoHandle()"></div>
				</div>
				<p class="desc">EMOS Web 会議システム</p>
			</el-col>
			
			<!-- 用户列表区域 -->
			<el-col :span="5">
				<el-card shadow="never">
					<template #header>
						<div class="card-header"><span>参加者</span></div>
					</template>
					<el-scrollbar height="555px">
						<ul class="user-list">
							<li v-for="one in userList">
								<img class="mic" src="../../../assets/trtc/mic.png" />
								<div class="mic-container">
									<img
										class="mic-green"
										:id="'mic-' + one.userId"
										src="../../../assets/trtc/mic-green.png"
									/>
								</div>
								<span>{{ one.dept }} - {{ one.name }}</span>
							</li>
						</ul>
					</el-scrollbar>
				</el-card>
				<div class="meeting-operate">
					<button :class="meetingStatus ? 'phone-btn-off' : 'phone-btn-on'" @click="phoneHandle"></button>
					<button :class="videoStatus ? 'video-btn-on' : 'video-btn-off'" @click="videoHandle"></button>
					<button :class="micStatus ? 'mic-btn-on' : 'mic-btn-off'" @click="micHandle"></button>
					<button :class="shareStatus ? 'share-btn-on' : 'share-btn-off'" @click="shareHandle"></button>
				</div>
			</el-col>
		</el-row>
	</div>
</template>

<script>
import TRTC from 'trtc-js-sdk';
import $ from 'jquery';
export default {
	data: function() {
		return {
			meetingId: null,
			uuid: null,
			mine: {},
			memberList: [],
			roomId: null,
			meetingStatus: false,
			videoStatus: true,
			micStatus: true,
			shareStatus: false,
			appId: null,
			userId: null,
			userSig: null,
			client: null,
			stream: {}, //所有的远端流
			userList: [], //进入会场的用户列表
			localStream: null,
			shareStream: null,
			bigVideoUserId: null
		};
	},
	// 进入会议室面板
	created: function() {

		let _this = this;
		// 路由传入的参数
		let params = _this.$route.params;
		_this.meetingId = params.meetingId;
		_this.uuid = params.uuid;
		
		let reqData = {
			meetingId: _this.meetingId
		};
		// 查出所有参会人
		_this.$http('meeting/searchOnlineMeetingMembers', 'POST', reqData, true, function(resp){
			let members = resp.list;
			if(members != null && members.length > 0){
				// 取得当前用户
				_this.mine = members[0];
				// 取出其他参会人的信息
				for(let i = 1; i < members.length; i++){
					_this.memberList.push(members[i]);
				}
			}
		});
		
		reqData = {
			uuid: _this.uuid
		};

		_this.$http('meeting/searchRoomIdByUUID', 'POST', reqData, true, function(resp){
			if(resp.roomId == null){
				_this.$message({
					message: '該当のビデオ会議は存在しません。',
					type: 'error',
					duration: 1200
				});
			}else{
				_this.roomId = resp.roomId;
			}
		});
	},
	methods: {
		// 用于判断当前本地使用的是本地流还是共享流，并返回相应的流对象
		getStream: function() {
			
		    let _this = this;
		    let stream = null;
		    if (_this.localStream != null) {
		        stream = _this.localStream;
		    } else if (_this.shareStream != null) {
		        stream = _this.shareStream;
		    }
		    return stream;
		},
		phoneHandle: function(){
			let _this = this;
			// 浏览器是否支持在线会议
			TRTC.checkSystemRequirements().then(checkResult => {
				if(!checkResult.result){
					_this.$alert('当ブラウザではオンライン会議はサポートされません。', '提示', {
						confirmButtonText: 'OK'
					});
				}else{
					
					// 电话按钮可以进入会议室，二次点击可以退出会议室，所以需要记录状态
					_this.meetingStatus = !_this.meetingStatus;
					
					if(_this.meetingStatus){
						// 记录摄像头/mic状态
						_this.videoStatus = true;
						_this.micStatus = true;
						
						// TRTC日志输出级别是error
						TRTC.Logger.setLogLevel(TRTC.Logger.LogLevel.ERROR);
						
						// 获取用户签名
						_this.$http('meeting/searchMyUserSig', 'GET', {}, false, function(resp){
							_this.appId = resp.appId;
							_this.userId = resp.userId;
							_this.userSig = resp.userSig;
						});
						
						// 创建TrtcClient对象
						let client = TRTC.createClient({
							mode: 'trtc',
							sdkAppId: _this.appId,
							userId: _this.userId + '',
							userSig: _this.userSig
						});
						_this.client = client;
						
						//1.============================================================
						//监听新增远端流事件（远端有用户上线进入会议室会捕获该事件）
						client.on('stream-added', event => {
							
							let remoteStream = event.stream;
							//订阅远端流
							client.subscribe(remoteStream);
							//从远端流获得远程用户userId(创建TrtcClient对象时候的参数)
							
							let userId = remoteStream.getUserId();
							
							//把新上线的用户添加到页面右侧的在线人员列表中
							_this.$http('user/searchNameAndDept', 'POST', {id: userId}, true, function(resp){
								let dept = resp.dept;
								let name = resp.name;
								_this.userList.push({userId: userId, dept: dept, name: name});
							});

							//把远端流保存到模型层JSON中，将来大屏显示的时候要找到某个远端流停止小窗口播放，切换到大窗口播放
							_this.stream[userId] = remoteStream;
						});
						
						//2.============================================================
						//订阅远端流成功事件(成功的话在页面某个div中播放视频)
						client.on('stream-subscribed', event => {
							
							let remoteStream = event.stream;
							let userId = remoteStream.getUserId();
							//找到视频墙中某个远端用户的格子，把其中用于显示视频的DIV，置顶覆盖用户信息
							$('#' + userId).css({ 'z-index': 1 });
							//在这个置顶的DIV中播放远端音视频讯号
							remoteStream.play(userId + '');
						});
						
						//3.============================================================
						//订阅远端删除流事件（远端用户退出会议室）
						client.on('stream-removed', event => {
							
							let remoteStream = event.stream;
							//取消订阅该远端流
							client.unsubscribe(remoteStream);
							
							let userId = remoteStream.getUserId();
							//在页面右侧的用户列表中删除该用户
							let i = _this.userList.findIndex(function(one){
								return one.userId == userId;
							})
							_this.userList.splice(i,1)
							
							//停止播放远端流视频，并且关闭远端流
							remoteStream.stop();
							remoteStream.close();
							
							//删除模型层JSON中保存的远端流对象
							delete _this.stream[userId];
							//把视频墙中该用户格子的视频DIV控件置底，显示用户基本信息
							$('#' + userId).css({ 'z-index': '-1' });
							$('#' + userId).html('');
						});
						
						// 订阅语音事件（无论远端还是本地说话都会触发）
						client.on("audio-volume",event=>{
							event.result.forEach(({userId,audioVolume,stream})=>{
								if(audioVolume > 5){
									$("#mic-" + userId).css("top",`${100 - audioVolume*3}%`);
								}else{
									$("#mic-" + userId).css("top",`100%`);
								}
							});
						});
						// 开启音量回调函数，并设置每 30ms 触发一次事件
						client.enableAudioVolumeEvaluation(30);
						
						client.
						join({ roomId: _this.roomId })
						.then(() => {
							
							//执行签到
							_this.$http('meeting/updateMeetingPresent','POST', { meetingId: _this.meetingId }, true, function(resp) {
							    let rows = resp.rows;
							    if (rows == 1) {
							        _this.$message({
							            message: '会議のチェックインに成功しました。',
							            type: 'success',
							            duration: 1200
							        });
							    }
							});

							//成功进入会议室，然后创建本地流
							let localStream = TRTC.createStream({
								userId: _this.userId + '',
								audio: true,
								video: true
							});
							_this.localStream = localStream;
							localStream.setVideoProfile('480p'); //设置分辨率

							//把自己添加到上线用户列表中
							_this.$http('user/searchNameAndDept', 'POST', {id: _this.userId}, true, function(resp){
								let dept = resp.dept;
								let name = resp.name;
								_this.userList.push({userId: _this.userId, dept: dept, name: name});
							});

							//初始化本地音视频流
							localStream
								.initialize()
								.catch(error => {
									
									console.error('初始化本地流失败 ' + error);
								})
								.then(() => {
									
									console.log('初始化本地流成功');
									//视频墙中第一个格子中的视频DIV置顶
									$('#localStream').css({ 'z-index': 1 });
									//播放本地音视频流
									localStream.play('localStream'); 
									//向远端用户推送本地流
									client
									.publish(localStream)
									.catch(error => {
										console.error('本地流发布失败 ' + error);
									})
									.then(() => {
										console.log('本地流发布成功');
									});
								});
						})	
						.catch(error => {
							console.error('进入房间失败: ' + error);
						});
					} else{
						
						// 关闭视频会议
						//获取当前本地使用的流，有可能是本地流或者共享流
						let stream = _this.getStream(); 
						_this.client.unpublish(stream).then(() => {
						    
							// 取消发布本地流成功
						    _this.client
						        .leave()
						        .then(() => {
						            console.log('成功退出会议室');
						            //关闭本地流或者共享流
						            stream.stop();
						            stream.close();
									
									//清空模型层的本地流
						            _this.localStream = null;
						            _this.shareStream = null;
									
									//清空模型层的远端流
						            _this.stream = {};
									
									//销毁TrtcClient对象
						            _this.client = null;
						            _this.userList = []; //清空用户列表
						            _this.videoStatus = true;
						            _this.micStatus = true;
						            _this.shareStatus = false;
									
									//视频墙上本地流DIV区域置底
						            $('#localStream').css({ 'z-index': '-1' });
						            $('#localStream').html('');
						            
									// 如果是播放大屏视频的时候退出会议，退出会议后需要隐藏大屏，然后显示视频墙界面
									if (_this.bigVideoUserId != null) {
									    $('#videoBig').hide();
									    $('#videoListContainer').show();
									    _this.bigVideoUserId = null;
									}
						        })
						        .catch(error => {
						            console.error('成功退出会议室失败' + error);
						        });
						});
					}
				}
			});
		},
		// 双击小视频，切换至大屏播放
		bigVideoHandle: function(userId){
			let _this = this;
			_this.bigVideoUserId = userId;
			
			// 隐藏视频墙
			$('#vidoListContainer').hide();
			// 显示大屏控件
			$('#videoBig').show();
			// 停止该用户的远端视频在屏幕墙格子的播放
			_this.stream[userId].stop();
			// 远端视频在大屏播放
			_this.stream[userId].play("videoBig");
		},
		// 双击大屏视频，切换到小屏播放
		smallVideoHandle: function() {
		    
			let _this = this;
		    // 停止大屏播放远端视频
		    _this.stream[_this.bigVideoUserId].stop(); 
		    
			// 在相应的小屏播放远端视频
		    _this.stream[_this.bigVideoUserId].play(_this.bigVideoUserId); 
			
		    _this.bigVideoUserId = null;
		    // 隐藏大屏控件
		    $('#videoBig').hide();
		    // 显示视频墙
		    $('#videoListContainer').show();
		},
		videoHandle: function() {
		    let _this = this;
		    if (_this.shareStatus) {
		        _this.$alert('屏幕共享中无法开关摄像头', '提示信息', {
		            confirmButtonText: '确定'
		        });
		        return;
		    }
		    if (_this.videoStatus) {
		        //关闭摄像头
		        _this.localStream.muteVideo();
		    } else {
		        //开启摄像头
		        _this.localStream.unmuteVideo();
		    }
		    _this.videoStatus = !_this.videoStatus;
		},
		micHandle: function() {
		    let _this = this;
		    let stream = _this.getStream();
		    if (_this.micStatus) {
		        //关闭话筒
		        stream.muteAudio();
		    } else {
		        //开启话筒
		        stream.unmuteAudio();
		    }
		    _this.micStatus = !_this.micStatus;
		},
		// 屏幕共享
		shareHandle: function() {
		    
			let _this = this;
		    // 判断用户是否进入视频会议室
		    if (!_this.meetingStatus) {
		        _this.$alert('画面共有するにはルームに入る必要です。', '提示', {
		            confirmButtonText: 'OK'
		        });
		        return;
		    }
			
		    // 检查浏览器是否支持屏幕共享
		    if (!TRTC.isScreenShareSupported()) {
		        // 提示当前浏览器不支持在线视频会议
		        this.$alert('当ブラウザでは画面共有をサポートしません。', '提示', {
		            confirmButtonText: 'OK'
		        });
		        return;
		    }
		    _this.shareStatus = !_this.shareStatus;
		    
			//开启屏幕共享
		    if (_this.shareStatus) {
		        // 创建共享流
		        let shareStream = TRTC.createStream({
		            audio: _this.micStatus,
		            screen: true,
		            userId: _this.userId
		        });
		        shareStream.setScreenProfile('1080p');
		        _this.shareStream = shareStream;
		        shareStream
		            .initialize()
		            .catch(error => {
		                console.error('初始共享流失败 ' + error);
		            })
		            .then(() => {
		                // 取消推送本地视频流
		                _this.client.unpublish(_this.localStream).then(() => {
		                    // 关闭本地流
							_this.localStream.close();
							// 本地流设置为空
		                    _this.localStream = null;
		                    // 隐藏本地视频窗口
		                    $('#localStream').css({ 'z-index': -1 });
							// 向远端推送共享流
		                    _this.client.publish(shareStream);
		                });
		            });
		    }
		    // 关闭屏幕共享
		    else {
		        // 重建本地视频流
		        let localStream = TRTC.createStream({
		            userId: _this.userId + '',
		            audio: _this.micStatus,
		            video: _this.videoStatus
		        });
		        _this.localStream = localStream;
		        localStream.setVideoProfile('480p');
		        localStream
		            .initialize()
		            .catch(error => {
		                console.error('初始化本地流失败 ' + error);
		            })
		            .then(() => {
		                console.log('初始化本地流成功');
		                // 取消共享流的推流
		                _this.client.unpublish(_this.shareStream).then(() => {
		                    // 关闭共享流
							_this.shareStream.close();
							// 共享流设置为空
		                    _this.shareStream = null;
		                    // 显示本地视频窗口
		                    $('#localStream').css({ 'z-index': 1 });
							//播放本地流
		                    localStream.play('localStream');
		                    // 向远端推送本地视频流
		                    _this.client
		                        .publish(localStream)
		                        .catch(error => {
		                            console.error('本地流发布失败 ' + error);
		                        })
		                        .then(() => {
		                            console.log('本地流发布成功');
		                        });
		                });
		            });
		    }
		}
		
	}
	
};
</script>

<style lang="less" scoped="scoped">
@import url('meeting_video.less');
</style>

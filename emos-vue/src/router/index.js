import {
	createRouter,
	createWebHashHistory
}
from 'vue-router'
import Login from '../views/login.vue'
import Main from "../views/main.vue"
import Home from "../views/home.vue"
import NotFound from "../views/404.vue"

import Role from "../views/group_management/role/role.vue"
import User from "../views/group_management/user/user.vue"
import Dept from "../views/group_management/dept/dept.vue"

import MeetingRoom from '../views/meeting_management/meeting_room/meeting_room.vue'
import OfflineMeeting from "../views/meeting_management/offline_meeting/offline_meeting.vue"
import OnlineMeeting from "../views/meeting_management/online_meeting/online_meeting.vue"
import MeetingVideo from "../views/meeting_management/online_meeting/meeting_video.vue"

import Approval from "../views/online_office/approval/approval.vue"
import Amect from "../views/online_office/amect/amect.vue"
import Leave from "../views/online_office/leave/leave.vue"
import Reim from "../views/online_office/reim/reim.vue"
import AmectReport from "../views/online_office/amect/amect_report.vue"

import AmectType from "../views/systemsetup/amect_type.vue"



const routes = [{
		path: '/login',
		name: 'Login',
		component: Login
	},
	{
		path: '/',
		name: 'Main',
		component: Main,
		children: [{
				path: '/home',
				name: 'Home',
				component: Home,
				meta: {
					title: 'ホームページ',
				}
			},
			{
				path: "/role",
				name: "Role",
				component: Role,
				meta: {
					title: "役割管理",
					isTab: true
				}
			},
			{
				path: '/user',
				name: 'User',
				component: User,
				meta: {
					title: 'ユーザー管理',
					isTab: true
				}
			},
			{
				path: '/dept',
				name: 'Dept',
				component: Dept,
				meta: {
					title: '部門管理',
					isTab: true
				}
			},
			{
				path: '/meeting_room',
				name: 'MeetingRoom',
				component: MeetingRoom,
				meta: {
					title: '会議室',
					isTab: true
				}
			},
			{
				path: '/offline_meeting',
				name: 'OfflineMeeting',
				component: OfflineMeeting,
				meta: {
					title: 'オフライン会議',
					isTab: true
				}
			},
			{
				path: '/online_meeting',
				name: 'OnlineMeeting',
				component: OnlineMeeting,
				meta: {
					title: 'オンライン会議',
					isTab: true
				}
			},
			{
				path: '/meeting_video/:meetingId/:uuid',
				name: 'MeetingVideo',
				component: MeetingVideo,
				meta: {
					title: 'ビデオ会議',
					isTab: true
				}
			}, {
				path: '/approval',
				name: 'Approval',
				component: Approval,
				meta: {
					title: '承認事項',
					isTab: true
				}
			},
			{
				path: '/leave',
				name: 'Leave',
				component: Leave,
				meta: {
					title: '休暇申請',
					isTab: true
				}
			},
			{
				path: '/amect',
				name: 'Amect',
				component: Amect,
				meta: {
					title: '罰金制度',
					isTab: true
				}
			},
			{
				path: '/amect_type',
				name: 'AmectType',
				component: AmectType,
				meta: {
					title: '罰金種類',
					isTab: true
				}
			},
			{
				path: '/amect_report',
				name: 'AmectReport',
				component: AmectReport,
				meta: {
					title: '罰金報告',
					isTab: true
				}
			},
			{
				path: '/reim',
				name: 'Reim',
				component: Reim,
				meta: {
					title: '立替金精算',
					isTab: true
				}
			}
		]
	},
	{
		path: "/404",
		name: "NotFound",
		component: NotFound
	},
	{
		path: '/:pathMatch(.*)*',
		redirect: "/404"
	}
]

const router = createRouter({
	history: createWebHashHistory(),
	routes
})
router.beforeEach((to, from, next) => {
	if (to.name != "Login") {
		let permissions = localStorage.getItem("permissions")
		if (permissions == null || permissions == "") {
			next({
				name: 'Login'
			})
		}
	}
	return next()
})

export default router

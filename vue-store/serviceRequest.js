//-------------------------------------------
//
// 服务请求业务服务目录模块 Store
//
//-------------------------------------------

import {
	ServiceRequestSTS
}
from '@/constant/store'
import router from "@/router"
import { Toast, MessageBox } from 'mint-ui'
import * as Service from '@/service/serviceRequest'
import Vue from 'vue'
import {
	useLocalAPI,
	localAPIBaseURL,
	prodAPIBaseURL
} from '@/config/runtime.env'
let ApiUrl = useLocalAPI ? localAPIBaseURL : prodAPIBaseURL;
//-------------------------------------------
// 子模块：查询业务服务目录
//-------------------------------------------

const queryMTS = ServiceRequestSTS.mutations.query
const queryATS = ServiceRequestSTS.actions.query
const queryGTS = ServiceRequestSTS.getters.query

const queryState = {
	bscResults: [],
	hasNextPage: true,
	queryPageLoaded: false,
	lastScrollTop: 0
}

const queryMutations = {

		[queryMTS.reloadBSC](state, {
			queryResultsTmp,
			hasNextPage
		}) {
			let arr = Object.keys(queryResultsTmp).map(key => queryResultsTmp[key])
			state.bscResults = arr
			state.hasNextPage = hasNextPage
		},
		[queryMTS.loadMore](state, {
			queryResults,
			hasNextPage
		}) {
			Object.keys(queryResults).map(key =>
				state.bscResults.push(queryResults[key]))
			state.hasNextPage = hasNextPage
		},
		//更新查询页面加载状态
		[queryMTS.updateQueryPageLoadStatus](state, isLoaded) {
			state.queryPageLoaded = isLoaded
		},
		//更新滚动条位置
		[queryMTS.updateLastScrollTop](state, lastScrollTop) {
			state.lastScrollTop = lastScrollTop
		}

}

const queryActions = {
	/**异步加载数据（async、await是ES7的新功能） */
	async [queryATS.reloadBSC]({
		commit
	}, {
		params,
		successCallback,
		errorCallback
	}) {

		try {
			let response = await Service.listBSC(params);
			let httpResult = response.data;
			if(httpResult.success) {
				let hasNextPage = httpResult.nextPage;
				let queryResultsTmp = httpResult.data.datas;
				//更新列表
				commit(queryMTS.reloadBSC, {queryResultsTmp,hasNextPage})
			} else {
				throw new Error(`${httpResult.code}:${httpResult.message}`)
			}
			if(successCallback) {
				successCallback()
			}
		} catch(e) {
			if(errorCallback) {
				errorCallback(e)
			}
		}

	},
	async [queryATS.loadMore]({
		commit
	}, {
		params,
		successCallback,
		errorCallback
	}) {
		try {
			let response = await Service.listBSC(params);
			let httpResult = response.data;
			if(httpResult.success) {
				let hasNextPage = httpResult.nextPage;
				let queryResults = httpResult.data.datas;
				//更新列表
				commit(queryMTS.loadMore, {queryResults,hasNextPage})
			} else {
				throw new Error(`${httpResult.code}:${httpResult.message}`)
			}
			if(successCallback) {
				successCallback()
			}
		} catch(e) {
			if(errorCallback) {
				errorCallback(e)
			}
		}

	}

}

const queryGetters = {
		/**
		 * 读取列表总数量
		 */
		[queryGTS.reloadBSC](state, getters, rootState) {
			return state.bscResults
		}
}

/** 组装子模块 Store */
const query = {
	/** 启用命名空间（独立命名空间，防止模块间命名冲突） */
	namespaced: true,
	/** 状态 */
	state: {
		...queryState
	},
	/** 突变（同步）（直接操作state） */
	mutations: {
		...queryMutations
	},
	/** 动作（可同步或异步）（间接操作state） */
	actions: {
		...queryActions
	},
	/** 读取器 （封装常用的读取方法，便于从state里提取值或组装新值）*/
	getters: {
		...queryGetters
	}
}

//-------------------------------------------
// 汇总子模块后，导出总模块 Store
//-------------------------------------------
export default {
	namespaced: true,
	modules: {
		query
	}
}
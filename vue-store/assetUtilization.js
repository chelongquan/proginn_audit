//-------------------------------------------
//
// 配置项模块 Store
//
//-------------------------------------------

import {
	AssetUtilizationSTS
}
from '@/constant/store'
import { Toast, MessageBox } from 'mint-ui'
import * as Service from '@/service/assetUtilization'
import Vue from 'vue'
import {
	useLocalAPI,
	localAPIBaseURL,
	prodAPIBaseURL
} from '@/config/runtime.env'
let ApiUrl = useLocalAPI ? localAPIBaseURL : prodAPIBaseURL;
//-------------------------------------------
// 子模块：查询配置项
//-------------------------------------------

const queryMTS = AssetUtilizationSTS.mutations.query
const queryATS = AssetUtilizationSTS.actions.query
const queryGTS = AssetUtilizationSTS.getters.query

const queryState = {
	serviceMenusResult: [],
	assetDetail: {},
	hasNextPage: true,
	queryPageLoaded: false,
	lastScrollTop: 0,
	historyParamsStatus:false,
	historyParams:{}
	
}

const queryMutations = {

	[queryMTS.reloadServiceMenus](state, {
		queryResultsTmp,
		hasNextPage
	}) {
		let arr = Object.keys(queryResultsTmp).map(key => queryResultsTmp[key])
		state.serviceMenusResult = arr
		state.hasNextPage = hasNextPage
	},
	[queryMTS.loadMore](state, {
		queryResults,
		hasNextPage
	}) {
		Object.keys(queryResults).map(key =>
			state.serviceMenusResult.push(queryResults[key]))
		state.hasNextPage = hasNextPage
	},
	[queryMTS.reloadAssetDetail](state, queryResults) {
		state.assetDetail = queryResults
	},
	//更新查询页面加载状态
	[queryMTS.updateQueryPageLoadStatus](state, isLoaded) {
		state.queryPageLoaded = isLoaded
	},
	//更新滚动条位置
	[queryMTS.updateLastScrollTop](state, lastScrollTop) {
		state.lastScrollTop = lastScrollTop
	},
	//更新简单模式临时存放参数的状态是否有效
	[queryMTS.updateHistoryParamsStatus](state, isLoaded) {
		state.historyParamsStatus = isLoaded
	},
	//更新简单模式存放的参数
	[queryMTS.updateHistoryParams](state, obj) {
		state.historyParams = obj
	}

}

const queryActions = {
	/**异步加载数据（async、await是ES7的新功能） */
	async [queryATS.reloadServiceMenus]({
		commit
	}, {
		successCallback,
		errorCallback
	}) {
		try {
			let response = await Service.listServiceMenus();
			let httpResult = response.data;
			if(httpResult.success) {
				let hasNextPage = httpResult.nextPage;
				let queryResultsTmp = Service.getShowKey(httpResult.data.datas);
				//更新列表
				commit(queryMTS.reloadServiceMenus, {
					queryResultsTmp,
					hasNextPage
				})
			} else {
				throw new Error(`${httpResult.message}`)
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
		pageNo,
		pageSize,
		successCallback,
		errorCallback
	}) {
		try {

			let response = await Service.listServiceMenus(pageNo, pageSize);
			let httpResult = response.data;
			if(httpResult.success) {
				let hasNextPage = httpResult.nextPage;
				let queryResults = Service.getShowKey(httpResult.data.datas);
				//更新列表
				commit(queryMTS.loadMore, {
					queryResults,
					hasNextPage
				})
			} else {
				throw new Error(`${httpResult.message}`)
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
	async [queryATS.submitAsset]({
		commit
	}, {
		params,
		successCallback,
		errorCallback
	}) {
		try {
			let response = await Service.save(params);
			let httpResult = response.data
			if(httpResult.success) {
				successCallback()
			} else {
				throw new Error(`${httpResult.message}`)
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
	[queryGTS.reloadServiceMenus](state, getters, rootState) {
		return state.serviceMenusResult
	},
	[queryGTS.assetDetail](state, getters, rootState) {
		return state.assetDetail
	}
	//	[queryGTS.getTypes](state, getters, rootState) {
	//		var data = state.assetDetail.typeLst
	//		var r = []
	//		for(var obj of data) {
	//			let i = {
	//				label: obj.name,
	//				value: obj.name
	//			}
	//			r.push(i)
	//		}
	//		return r
	//	}
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
// 汇总子模块后，导出配置项总模块 Store
//-------------------------------------------
export default {
	namespaced: true,
	modules: {
		query
	}
}
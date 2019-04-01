//-------------------------------------------
//
// 配置项模块 Store
//
//-------------------------------------------

import {
	TaskSTS
}
from '@/constant/store'
import * as Service from '@/service/task'
//-------------------------------------------
// 子模块：查询配置项
//-------------------------------------------

const queryMTS = TaskSTS.mutations.query
const queryATS = TaskSTS.actions.query
const queryGTS = TaskSTS.getters.query

const queryState = {
	params: {},
	results: [],
	hasNextPage: true,
	queryPageLoaded: false,
	lastScrollTop: 0,
	showNodata: true
}

const queryMutations = {
	[queryMTS.updateParams](state, queryParams) {
		if(null == queryParams) {
			queryParams = {}
		}
		state.params = {
			pageNo: 1,
			pageSize: 10,
			taskIds:'',
			...queryParams
		}
		console.log("state.params",state.params)
	},
	/** 刷新查询列表 */
	[queryMTS.reloadList](state, {
		tmpResult,
		showNodata,
		hasNextPage
	}) {
		let arr = Object.keys(tmpResult).map(key => tmpResult[key])
		state.results = arr
		state.showNodata = showNodata
		state.hasNextPage = hasNextPage
	},
	[queryMTS.loadMore](state, {
		queryResults,
		hasNextPage
	}) {
		Object.keys(queryResults).map(key =>
			state.results.push(queryResults[key]))
		state.hasNextPage = hasNextPage
	},
	//更新滚动条位置
	[queryMTS.updateLastScrollTop](state, lastScrollTop) {
		state.lastScrollTop = lastScrollTop
	},
	//更新查询页面加载状态
	[queryMTS.updateQueryPageLoadStatus](state, isLoaded) {
		state.queryPageLoaded = isLoaded
	}
}

const queryActions = {
	//加载资产记录列表
	async [queryATS.reloadList]({
		commit
	}, {
		queryParams,
		successCallback,
		errorCallback
	}) {
		try {
		console.log(queryParams.taskIds);
			let response = await Service.listTaskResult(queryParams.pageNo,queryParams.pageSize,queryParams.taskIds);
			let httpResult = response.data
			if(httpResult.success) {
				let datas = httpResult.result.datas;
				let hasNextPage = httpResult.result.nextPage; //是否有下一页
				let showNodata = false;
				if(datas.length === 0) {
					showNodata = true;
				}
				commit(queryMTS.reloadList, {
					tmpResult:datas,
					showNodata,
					hasNextPage
				})
			} else {
				throw new Error(`${response.data.message}`)
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
		queryParams,
		successCallback,
		errorCallback
	}) {
		try {
			let response = await Service.listTaskResult(
				queryParams.pageNo,queryParams.pageSize,queryParams.taskIds
			);
			let httpResult = response.data
			if(httpResult.success) {
				let datas = httpResult.result.datas;
				let hasNextPage = httpResult.result.nextPage; //是否有下一页
				//更新列表
				commit(queryMTS.loadMore, {
					queryResults:datas,
					hasNextPage
				})
			} else {
				throw new Error(`${response.data.message}`)
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

const queryGetters = {}

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
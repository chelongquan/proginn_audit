//-------------------------------------------
//
// 知识库模块 Store
//
//-------------------------------------------

import {
  RepositorySTS
}
from '@/constant/store'
import * as Service from '@/service/repository'


//-------------------------------------------
// 子模块：查询知识库
//-------------------------------------------

const queryMTS = RepositorySTS.mutations.query
const queryATS = RepositorySTS.actions.query

/** 状态 */
const queryState = {
  params: {},
  results: [],
  allResultsLoadComplete: false,
  queryPageLoaded: false,
  lastScrollTop: 0
}

/** 突变 */
const queryMutations = {
  // 更新查询参数
  [queryMTS.updateParams](state, queryParams) {
    if (null == queryParams) {
      queryParams = {}
    }
    state.params = {
      pageNo: 1,
      pageSize: 10,
      key: '',
      ...queryParams
    }
  },
  // 更新查询列表
  [queryMTS.updateResults](state, {
    results,
    allResultsLoadComplete
  }) {
    state.results = results
    state.allResultsLoadComplete = allResultsLoadComplete
  },
  // 追加查询结果
  [queryMTS.appendResults](state, {
    results,
    allResultsLoadComplete
  }) {
    if (results && results.length > 0) {
      state.results = state.results.concat(results)
    }
    state.allResultsLoadComplete = allResultsLoadComplete
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

/** 动作 */
const queryActions = {
  // 异步加载知识库查询结果（首页）
  async [queryATS.reloadResults]({
    commit,
    state
  }, {
    queryParams,
    successCallback,
    errorCallback
  }) {
    try {
      let response = await Service.listRepositoryResult(queryParams.pageNo, queryParams.pageSize, queryParams.key);

      if (response.data.success) {
        commit(queryMTS.updateResults, {
          results: response.data.result,
          allResultsLoadComplete: response.data.allResultsLoadComplete
        })
      } else {
        throw new Error(`${response.data.message}`)
      }
      if (successCallback) {
        successCallback()
      }
    } catch (e) {
      if (errorCallback) {
        errorCallback(e)
      }
    }
  },

  //下一页
  async [queryATS.loadNextPageResults]({
    commit,
    state
  }, {
    queryParams,
    successCallback,
    errorCallback
  }) {
    try {
      let response = await Service.listRepositoryResult(queryParams.pageNo, queryParams.pageSize, queryParams.key);
      if (response.data.success) {
        commit(queryMTS.appendResults, {
          results: response.data.result,
          allResultsLoadComplete: response.data.allResultsLoadComplete
        })
      } else {
        throw new Error(`${response.data.message}`)
      }
      if (successCallback) {
        successCallback()
      }
    } catch (e) {
      if (errorCallback) {
        errorCallback(e)
      }
    }
  }
}

/** 读取器 */
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
// 汇总子模块后，导出知识库总模块 Store
//-------------------------------------------
export default {
  namespaced: true,
  modules: {
    query
  }
}

//-------------------------------------------
//
// 配置项模块 Store
//
//-------------------------------------------

import {
  ConfigurationItemSTS
}
from '@/constant/store'
import * as Service from '@/service/configurationItem'


// -------------------------------------------
// 子模块：查询配置项
// -------------------------------------------

const queryMTS = ConfigurationItemSTS.mutations.query
const queryATS = ConfigurationItemSTS.actions.query

/** 状态 */
function initQueryState(state) {
  let tempState = state
  if (!tempState) {
    tempState = {}
  }
  tempState.params = {
    pageNo: 1,
    pageSize: 10,
    name: '',
    ipAddress: '',
    customDepartmentFullName: '',
    operationItemFullName: '',
    responsibleUserName: '',
    physicalLocation: ''
  }
  tempState.results = []
  tempState.allResultsLoadComplete = false
  tempState.advancedQueryMode = false
  tempState.queryPageLoaded = false
  tempState.lastScrollTop = 0
  return tempState
}
const queryState = {
  ...initQueryState()
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
      ...queryParams
    }
  },
  // 更新查询列表
  [queryMTS.updateResults](state, {
    datas,
    nextPage
  }) {
    state.results = datas
    state.allResultsLoadComplete = !nextPage
  },
  // 更新查询列表项
  [queryMTS.updateResultItem](state, resultItem) {
    state.results.forEach((item) => {
      if (item.id == resultItem.id) {
        for (let key in item) {
          item[key] = resultItem[key]
        }
      }
    });
  },
  // 追加查询结果
  [queryMTS.appendResults](state, {
    datas,
    nextPage
  }) {
    if (datas && datas.length > 0) {
      state.results = state.results.concat(datas)
    }
    state.allResultsLoadComplete = !nextPage
  },
  // 启用高级查询模式
  [queryMTS.enableAdvancedQueryMode](state) {
    state.advancedQueryMode = true
  },
  // 更新查询页面加载状态
  [queryMTS.updateQueryPageLoadStatus](state, isLoaded) {
    state.queryPageLoaded = isLoaded
  },
  // 更新滚动条位置
  [queryMTS.updateLastScrollTop](state, lastScrollTop) {
    state.lastScrollTop = lastScrollTop
  },
  //重置State
  [queryMTS.resetStoreState](state) {
    initQueryState(state)
  }
}

/** 动作 */
const queryActions = {


  // 异步加载配置项查询结果（首页）
  async [queryATS.reloadResults]({
    commit,
    state
  }, {
    successCallback,
    errorCallback
  }) {
    try {

      //重置页码
      commit(queryMTS.updateParams, {...state.params,
        pageNo: 1
      })

      //获取数据
      let response = await Service.listQueryResult(state.params);
      if (response.data.success) {
        commit(queryMTS.updateResults, {...response.data.data
        })
      } else {
        throw new Error(`${response.data.message}`)
      }
      successCallback()
    } catch (e) {
      //出错时清空数据
      commit(queryMTS.updateResults, {
        datas: [],
        nextPage: false
      })
      errorCallback(e)
    }
  },

  // 异步加载配置项查询结果（下一页）
  async [queryATS.loadNextPageResults]({
    commit,
    state
  }, {
    successCallback,
    errorCallback
  }) {
    try {
      let nextPageQueryParams = {...state.params,
        pageNo: state.params.pageNo + 1
      }
      let response = await Service.listQueryResult(nextPageQueryParams)
      if (response.data.success) {
        commit(queryMTS.appendResults, {...response.data.data
        })
        commit(queryMTS.updateParams, nextPageQueryParams)
      } else {
        throw new Error(`${response.data.message}`)
      }
      successCallback()
    } catch (e) {
      errorCallback(e)
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
  /** 读取器 （封装常用的读取方法，便于从state里提取值或组装新值） */
  getters: {
    ...queryGetters
  }
}

// -------------------------------------------
// 汇总子模块后，导出配置项总模块 Store
// -------------------------------------------
export default {
  namespaced: true,
  modules: {
    query
  }
}

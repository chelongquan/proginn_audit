//-------------------------------------------
//
// 工单模块 Store
//
//-------------------------------------------

//-------------------------------------------
//
// 工单模块 Store
//
//-------------------------------------------
import {
  TicketSTS
} from '@/constant/store'
import * as Service from '@/service/ticket'
import * as TicketComponentService from '@/service/component'

//-------------------------------------------
// 子模块：查询工单
//-------------------------------------------

const queryMTS = TicketSTS.mutations.query
const queryATS = TicketSTS.actions.query

//-------------------------------------------
// 子模块：处理工单
//-------------------------------------------

const processMTS = TicketSTS.mutations.process
const processATS = TicketSTS.actions.process
const processGTS = TicketSTS.getters.process

/** 状态 */
// 表单数据结构
const __formDataStructure = {
  id: null,
  subject: '',
  serialNumber: '',
  type: null,
  sourceType: null,
  descriptionText: '',
  descriptionH5HTML: '',
  operationDepartmentId: null,
  operationDepartmentFullName: '',
  serviceCatalogId: null,
  serviceCatalogName: '',
  workflowId: null,
  workflowNodeId: null,
  customDepartmentId: null,
  customDepartmentFullName: '',
  operationItemId: null,
  operationItemFullName: '',
  severity: null,
  severityText: '',
  sortId: null,
  sortFullName: '',
  applicantName: '',
  createTime: '',
  approveFlag: null,
  approveFlagText: '',
  status: null,
  statusText: '',
  slaOverTime: null,
  serviceCatalogEditable: false,
  customDepartmentEditable: false,
  operationItemEditable: false,
  severityEditable: false,
  ticketRelatedCIVOs: [],
  businessType: '',
  //仅前端使用的字段如下
  workflowNodeName: '',
  processDescription: '',
  processDescriptionText: '',
  processDescriptionH5Html: '',
  processDescriptionEmailHtml: '',
  isProcessDescriptionEmpty: true,
  isClearRelatedCIs: false,
}

function initProcessState(state) {
  //注意：因为查询界面需要使用processedTicketIds，所以它不能被清除
  let tempState = state
  if (!tempState) {
    tempState = {}
    tempState.processedTicketIds = ''
    tempState.processedServiceDeskTicketIds = ''
    tempState.processedIncidentTicketIds = ''
    tempState.processedProblemTicketIds = ''
    tempState.processedChangeTicketIds = ''
  }
  tempState.ticketId = null
  tempState.ticketType = null
  tempState.formData = {
    ...__formDataStructure
  }
  tempState.originalFormData = {
    ...__formDataStructure
  }
  tempState.pageLoaded = false
  tempState.lastScrollTop = 0
  return tempState
}

// 处理界面数据
const processState = {
  ...initProcessState()
}

/** 状态 */
const queryState = {
  params: {},
  results: [],
  allResultsLoadComplete: false,
  queryPageLoaded: false,
  lastScrollTop: 0,
  //associateci.vue记录数据是否重新加载
  associateciList: [],
  associateciListLoadStatus: false
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
      ticketType: 0,
      queryType: '0',
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
  },
  [queryMTS.updateAssociateciList](state, associateciList) {
    state.associateciList = associateciList
  },
  [queryMTS.updateAssociateciListLoadStatus](state, associateciListLoadStatus) {
    state.associateciListLoadStatus = associateciListLoadStatus
  }
}


/** 突变 */
const processMutations = {
  // 更新页面加载状态
  [processMTS.updatePageLoadedStatus](state, isLoaded) {
    state.pageLoaded = isLoaded
  },
  // 更新滚动条位置
  [processMTS.updateLastScrollTop](state, lastScrollTop) {
    state.lastScrollTop = lastScrollTop
  },
  // 更新工单ID
  [processMTS.updateTicketTypeAndId](state, {
    ticketType,
    ticketId
  }) {
    state.ticketType = ticketType
    state.ticketId = ticketId
  },
  // 更新表单数据
  [processMTS.updateFormData](state, formData) {
    state.formData = {...state.formData,
      ...formData
    }
    state.originalFormData = {...state.originalFormData,
      ...formData
    }
  },
  // 更新单个表单字段值
  [processMTS.updateFormItemValue](state, {
    key,
    value
  }) {
    state.formData[key] = value
  },
  //批量更新表单字段值
  [processMTS.updateFormItemValues](state, keyValueArrays = [
    []
  ]) {
    keyValueArrays.forEach((keyValueArray) => {
      state.formData[keyValueArray[0]] = keyValueArray[1]
    })
  },
  //批量还原表单值
  [processMTS.restoreFormItemValues](state, keys = []) {
    keys.forEach((key) => {
      state.formData[key] = state.originalFormData[key]
    })
  },
  //批量清空表单值
  [processMTS.clearFormItemValues](state, keys = []) {
    keys.forEach((key) => {
      if (typeof state.formData[key] === String) {
        state.formData[key] = ''
      } else {
        state.formData[key] = null
      }
    })
  },
  //增加已处理工单ID
  [processMTS.addProcessedTicketId](state, {
    key,
    id
  }) {
    if (state[key].length > 0) {
      if (`,${state[key]},`.indexOf(`,${id},`) > -1) {
        return
      }
      state[key] += ','
    }
    state[key] += id
    console.log('state.' + key, state[key])
  },
  //重置State
  [processMTS.resetStoreState](state) {
    initProcessState(state)
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
    selfProcessTicketIds,
    successCallback,
    errorCallback
  }) {
    try {
      let response = await TicketComponentService.listPendingTicket(queryParams.pageNo, queryParams.pageSize, queryParams.ticketType, selfProcessTicketIds, queryParams.queryType);

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
    selfProcessTicketIds,
    successCallback,
    errorCallback
  }) {
    try {
      let response = await TicketComponentService.listPendingTicket(queryParams.pageNo, queryParams.pageSize, queryParams.ticketType, selfProcessTicketIds, queryParams.queryType);
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

/** 动作 */
const processActions = {
  // 异步加载工单处理界面表单数据
  async [processATS.reloadFormData]({
    commit,
    state
  }, {
    ticketType,
    ticketId,
    successCallback,
    errorCallback
  }) {
    try {
      let response = await Service.getTicketProcessBasicVOById(ticketType, ticketId);
      if (response.data.success) {
        commit(processMTS.updateFormData, {...response.data.data
        })
      } else {
        errorCallback(response)
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

/** 读取器 */
const processGetters = {

  [processGTS.currentWorkflowInitNodesEditable](state, getters, rootState) {
    return state.formData.serviceCatalogId != state.originalFormData.serviceCatalogId
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

/** 组装子模块 Store */
const processTicket = {
  /** 启用命名空间（独立命名空间，防止模块间命名冲突） */
  namespaced: true,
  /** 状态 */
  state: {
    ...processState
  },
  /** 突变（同步）（直接操作state） */
  mutations: {
    ...processMutations
  },
  /** 动作（可同步或异步）（间接操作state） */
  actions: {
    ...processActions
  },
  /** 读取器 （封装常用的读取方法，便于从state里提取值或组装新值）*/
  getters: {
    ...processGetters
  }
}

export default {
  namespaced: true,
  modules: {
    process: processTicket,
    query
  }

}

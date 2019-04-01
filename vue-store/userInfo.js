//-------------------------------------------
//
// 配置项模块 Store
//
//-------------------------------------------

import {
  UserInfoSTS
}
from '@/constant/store'
//-------------------------------------------
// 子模块：
//-------------------------------------------

const queryMTS = UserInfoSTS.mutations.query
const queryATS = UserInfoSTS.actions.query
const queryGTS = UserInfoSTS.getters.query

const queryState = {
  loginUserVo: {},
  //用于判断首页是否允许返回
  allowBack:false
}

const queryMutations = {

  /** 保存用户信息 */
  [queryMTS.saveUserInfos](state, queryResults) {
    state.loginUserVo = queryResults
  },
  [queryMTS.updateAppSetting](state, allowBack) {
    state.allowBack = allowBack
  }
}

const queryActions = {
  /**异步加载数据（async、await是ES7的新功能） */
  //加载用户列表
  async [queryATS.saveUserInfos]({
    commit
  },{
	  	params
  }) {
    //保存用户信息
    commit(queryMTS.saveUserInfos, params)
  } 

}

const queryGetters = {
		/**
		 * 
		 */
		[queryGTS.getUserInfos](state, getters, rootState) {
			return state.loginUserVo
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

//-------------------------------------------
//
// 运维人员模块 Store
//
//-------------------------------------------

import {
  OperationUserSTS
} from '@/constant/store'


const LocalMTS = OperationUserSTS.mutations;
const LocalGTS = OperationUserSTS.getters;
const LocalATS = OperationUserSTS.actions;

export default {
  namespaced: true
    // modules: {
    //   //查询模块
    //   queryModule: {
    //     namespaced: true,
    //     state: {
    //       results: []
    //     },
    //     mutations: {
    //       [LocalMTS.queryModule.createOrUpdateResult](localState, user) {
    //         localState.results.add(user);
    //       }
    //     },
    //     actions: {
    //       [LocalATS.queryModule.createOrUpdateResult]({
    //         state,
    //         commit,
    //         rootState
    //       }, user) {
    //         commit(LocalMTS.queryModule.createOrUpdateResult, user)
    //       }
    //     },
    //     getters: {
    //       [LocalGTS.queryModule.getResultSize](localState, getters, rootState) {
    //         return localState.results.length
    //       }
    //     }
    //   }
    //   //session模块
    //   sessionModule: {
    //     namespaced: true,
    //     state: {
    //       user: null
    //     },
    //     mutations: {
    //       [LocalMTS.sessionModule.updateUser](localState, user) {
    //         localState.user = {
    //           ...user
    //         }
    //       }
    //     }
    //     getters: {
    //       [LocalGTS.sessionModule.getUser](localState, getters, rootState) {
    //         return localState.user
    //       }
    //     }
    //   }
    // }
}

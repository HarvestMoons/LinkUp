// store/index.js

import { createStore } from 'vuex'
import Cookies from 'js-cookie'

export default createStore({
  state: {
    isLoggedIn: !!Cookies.get('JWT'), // 默认判断是否有 token
    user: JSON.parse(sessionStorage.getItem('user')) || null,
    userId: sessionStorage.getItem('userId') || null,
    token: Cookies.get('JWT') || null,
    friendList: null,
    avatarMap: {}, // 存储头像
    currentLanguage: 'en',
    taskList: null,
    groupList: null,
  },
  mutations: {
    login(state, token) {
      state.token = token
      Cookies.set('JWT', token) // 存储 token 到 cookie
    },
    logout(state) {
      state.isLoggedIn = false
      state.user = null
      state.userId = null
      state.token = null
      state.friendList = null
      state.taskList = null
      state.groupList = null
      sessionStorage.removeItem('user')
      sessionStorage.removeItem('userId')
      Cookies.remove('JWT')
    },
    setLogined(state) {
      state.isLoggedIn = true
    },
    setUser(state, user) {
      state.user = user
      state.userId = user.id
      sessionStorage.setItem('user', JSON.stringify(user)) // 存储用户信息
      sessionStorage.setItem('userId', user.id) // 存储用户信息
    },
    setFriendList(state, friendList) {
      state.friendList = friendList
      sessionStorage.setItem('friendList', JSON.stringify(friendList))
    },
    setAvatarMap(state, avatarMap) {
      state.avatarMap = avatarMap
    },
    setLanguage(state, language) {
      state.currentLanguage = language
    },
    setTaskList(state, taskList) {
      state.taskList = taskList
    },
    setGroupList(state, groupList) {
      state.groupList = groupList
    },
  },
  actions: {
    logout({ commit }) {
      commit('logout')
    },
    login({ commit }, token) {
      commit('login', token)
    },
    setLogined({ commit }) {
      commit('setLogined')
    },
    setUser({ commit }, user) {
      commit('setUser', user)
    },
    setFriendList({ commit }, friendList) {
      commit('setFriendList', friendList)
    },
    loadAvatars({ commit }) {
      const avatarMap = {}
      const images = require.context('@/assets/images/avatars', false, /\.png$/)
      images.keys().forEach((fileName) => {
        const id = fileName.match(/(\d+)\.png$/)[1]
        avatarMap[id] = images(fileName)
      })
      commit('setAvatarMap', avatarMap)
    },
    setLanguage({ commit }, language) {
      commit('setLanguage', language)
    },
    setTaskList({ commit }, taskList) {
      commit('setTaskList', taskList)
    },
    setGroupList({ commit }, groupList) {
      commit('setGroupList', groupList)
    },
  },
  getters: {
    isAuthenticated: (state) => state.isLoggedIn,
    getUser: (state) => state.user,
    getUserId: (state) => state.userId,
    getToken: (state) => state.token,
    getFriendList: (state) => state.friendList,
    getAvatarMap: (state) => state.avatarMap,
    getAvatar: (state) => (avatarId) => {
      return avatarId == null ? require('@/assets/images/icon.png') : state.avatarMap[avatarId]
    },
    getUserAvatar: (state) => {
      return state.user == null
        ? require('@/assets/images/icon.png')
        : state.avatarMap[state.user.avatarId]
    },
    getCurrentLanguage(state) {
      return state.currentLanguage
    },
    getTaskList: (state) => state.taskList,
    getGroupList: (state) => state.groupList,
  },
})

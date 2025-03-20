// store/index.js

import { createStore } from 'vuex';
import Cookies from 'js-cookie';

export default createStore({
    state: {
        isLoggedIn: !!Cookies.get('JWT'), // 默认判断是否有 token
        user: JSON.parse(sessionStorage.getItem('user')) || null,
        userId: sessionStorage.getItem('userId') || null,
        token: Cookies.get('JWT') || null,
        friendList: null,
    },
    mutations: {
        login(state, token) {
            state.isLoggedIn = true;
            state.token = token;
            Cookies.set('JWT', token); // 存储 token 到 cookie
        },
        logout(state) {
            state.isLoggedIn = false;
            state.user = null;
            state.userId = null;
            state.token = null;
            sessionStorage.removeItem('user');
            Cookies.remove('JWT');
        },
        setUser(state, user) {
            state.user = user;
            sessionStorage.setItem('user', JSON.stringify(user)); // 存储用户信息
            sessionStorage.setItem('userId', user.id); // 存储用户信息
        },
        setFriendList(state, friendList) {
            state.friendList = friendList;
            sessionStorage.setItem('friendList', JSON.stringify(friendList));
        }
    },
    actions: {
        logout({ commit }) {
            commit('logout');
        },
        login({ commit }, token) {
            commit('login', token);
        },
        setUser({ commit }, user) {
            commit('setUser', user);
        },
        setFriendList({ commit }, friendList) {
            commit('setFriendList', friendList);
        }
    },
    getters: {
        isAuthenticated: (state) => state.isLoggedIn,
        getUser: (state) => state.user,
        getUserId: (state) => state.userId,
        getToken: (state) => state.token,
        getFriendList: (state) => state.friendList,
    },
});

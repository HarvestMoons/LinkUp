import axios from "axios";
import store from '@/store';  // 导入 Vuex store

export async function fetchFriends(userId) {
    try {
        const token = store.getters.getToken; // 获取存储的 token
        const response = await axios.get(`/friendships/find/${userId}`, {
            headers: {
                'Authorization': `Bearer ${token}`, // 在请求头中添加 token
            },
        });
        const friendList = response.data.map(({ user, friend }) => {
            const isCurrentUser = user.id === Number(userId);
            const target = isCurrentUser ? friend : user; // 取出好友的信息
            return {
                username: target.username,
                id: target.id,
                avatarId: target.avatarId,
                friend: target,
            };
        });
        store.dispatch('setFriendList', friendList);
        return friendList
    } catch (error) {
        console.error("获取好友数据失败:", error);
        return [];
    }
}

export async function getFriendList(userId) {
    const cached = store.getters.getFriendList;
    if (cached) {
        // 后台异步更新
        fetchFriends(userId);
        return cached;
    } else {
        // 没缓存，直接等请求
        return await fetchFriends(userId);
    }
}

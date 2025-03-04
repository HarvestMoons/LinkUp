import axios from "axios";

export async function fetchFriends(userId) {
    try {
        const token = localStorage.getItem('token'); // 获取存储的 token
        const response = await axios.get(`/friendships/find/${userId}`, {
            headers: {
                'Authorization': `Bearer ${token}`, // 在请求头中添加 token
            },
        });
        return response.data.map(({ user, friend }) => {
            const isCurrentUser = user.id === Number(userId);
            const target = isCurrentUser ? friend : user; // 取出好友的信息
            return {
                nickname: target.username,
                id: target.id,
                avatar: target.avatar,
                friend: target,
            };
        });
    } catch (error) {
        console.error("获取好友数据失败:", error);
        return [];
    }
}

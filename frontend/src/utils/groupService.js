import axios from 'axios'
import store from '@/store' // 导入 Vuex store

export async function fetchGroups(userId) {
  try {
    let groupList = []
    const token = store.getters.getToken // 获取存储的 token
    const response = await axios.get(`/user/groups/${userId}`, {
      headers: {
        Authorization: `Bearer ${token}`, // 在请求头中添加 token
      },
    })
    groupList = await Promise.all(
      response.data.map(async (item) => {
        const group = item.taskGroup

        // 获取成员列表
        const membersResponse = await axios.get(`/groups/${group.id}/members`, {
          headers: {
            Authorization: `Bearer ${token}`, // 在请求头中添加 token
          },
        })
        const members = membersResponse.data || []

        // 取前四个成员的头像
        const avatars = members
          .slice(0, 4)
          .map((member) => store.getters.getAvatar(member.user.avatarId))
        console.log()
        return { ...group, avatars }
      })
    )
    store.dispatch('setGroupList', groupList)
    return groupList
  } catch (error) {
    console.error('获取群组数据失败:', error)
    return []
  }
}

export async function getGroupList(userId) {
  const cached = store.getters.getGroupList
  if (cached) {
    // 后台异步更新
    fetchGroups(userId)
    return cached
  } else {
    // 没缓存，直接等请求
    return await fetchGroups(userId)
  }
}

export async function fetchMembers(groupId) {
  try {
    const response = await axios.get(`/groups/${groupId}/members`)
    const groupMembers = response.data.map((item) => {
      return {
        role: item.role,
        ...item.user,
      }
    })
    console.log('members', groupMembers);
    return groupMembers;
  } catch (error) {
    console.error('加载群组成员失败', error)
  }
}

export async function fetchUserRole(groupId, userId) {
  try {
    const response = await axios.get(
      `/groups/${groupId}/members/${userId}/role`
    );
    return response.data;
  } catch (error) {
    console.error("加载群组身份失败", error);
  }
}
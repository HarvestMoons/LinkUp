import axios from 'axios'
import store from '@/store'
import {useI18n} from "vue-i18n"; // 导入 Vuex store

export async function fetchFriends(userId) {
  try {
    const token = store.getters.getToken // 获取存储的 token
    const response = await axios.get(`/friendships/find/${userId}`, {
      headers: {
        Authorization: `Bearer ${token}`, // 在请求头中添加 token
      },
    })
    const friendList = response.data.map(({ user, friend }) => {
      const isCurrentUser = user.id === Number(userId)
      const target = isCurrentUser ? friend : user // 取出好友的信息
      return {
        username: target.username,
        id: target.id,
        avatarId: target.avatarId,
        lastActiveTime: target.lastActiveTime,
        friend: target,
      }
    })
    store.dispatch('setFriendList', friendList)
    return friendList
  } catch (error) {
    console.error('获取好友数据失败:', error)
    return []
  }
}

export async function getFriendList(userId) {
  const cached = store.getters.getFriendList
  if (cached) {
    // 后台异步更新
    fetchFriends(userId)
    return cached
  } else {
    // 没缓存，直接等请求
    return await fetchFriends(userId)
  }
}

export function calcLastActiveString(lastActiveTime) {
  const { t } = useI18n() // 假设你已经配置好了i18n

  if (!lastActiveTime) return t('friends.lastActive.noData')

  const last = new Date(lastActiveTime)
  const now = new Date()
  const diffMs = now - last

  const diffMinutes = Math.floor(diffMs / (1000 * 60))
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))
  const diffWeeks = Math.floor(diffDays / 7)

  if (diffMinutes < 5) {
    return t('friends.lastActive.online')
  } else if (diffMinutes < 60) {
    return t('friends.lastActive.minutesAgo', { minutes: diffMinutes })
  } else if (diffHours < 24) {
    return t('friends.lastActive.hoursAgo', { hours: diffHours })
  } else if (diffDays < 7) {
    return t('friends.lastActive.daysAgo', { days: diffDays })
  } else if (diffWeeks < 4) {
    return t('friends.lastActive.weeksAgo', { weeks: diffWeeks })
  } else {
    return t('friends.lastActive.offlineOverMonth')
  }
}
import axios from 'axios'
import store from '@/store' // 导入 Vuex store
import { fetchUserRole } from '@/utils/groupService'

export async function fetchAllTasks(userId) {
  try {
    let taskList = []
    const token = store.getters.getToken // 获取存储的 token
    const responsePersonalTasks = await axios.get(`/tasks/user/${userId}/personal-tasks`, {
      headers: {
        Authorization: `Bearer ${token}`, // 在请求头中添加 token
      },
    })
    let responseGroupTasks = await axios.get(`/tasks/user/${userId}/group-tasks`, {
      headers: {
        Authorization: `Bearer ${token}`, // 在请求头中添加 token
      },
    })
    responseGroupTasks.data = responseGroupTasks.data.filter((task) => {
      return !task.assignee || task.assignee.id === Number(userId)
    })
    await Promise.all(
      responseGroupTasks.data.map(async (task) => {
        task.userRole = await fetchUserRole(task.taskGroup.id, userId)
      })
    )
    taskList = responsePersonalTasks.data.concat(responseGroupTasks.data)
    store.dispatch('setTaskList', taskList)
    return taskList
  } catch (error) {
    console.error('获取任务数据失败:', error)
    return []
  }
}

export async function getTaskList(userId) {
  const cached = store.getters.getTaskList
  if (cached) {
    // 后台异步更新
    fetchAllTasks(userId)
    return cached
  } else {
    // 没缓存，直接等请求
    return await fetchAllTasks(userId)
  }
}

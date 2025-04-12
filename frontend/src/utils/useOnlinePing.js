import { onMounted, onUnmounted } from 'vue'
import Cookies from 'js-cookie'
import store from '@/store'

let unloadListenerAdded = false
let tokenCheckIntervalId = null
//TODO:此处默认ping周期设置为60000（60s）用于生产环境
export function useOnlinePing(pollInterval = 2000) {
  const getToken = () => store.getters.getToken

  const startPing = () => {
    if (globalThis.__onlinePingIntervalId) return

    console.log('✅ 启动在线 ping')

    globalThis.__onlinePingIntervalId = setInterval(() => {
      const token = getToken()
      if (!token) {
        stopPing() // 如果没有 token 说明登出了
        return
      }

      console.log('📡 pinging to keep session alive...')
      fetch('/api/user/ping', {
        method: 'POST',
        headers: {
          Authorization: 'Bearer ' + token,
        },
      }).catch((err) => {
        console.warn('⚠️ 在线状态 ping 失败:', err)
      })
    }, pollInterval)

    if (!unloadListenerAdded) {
      window.addEventListener('beforeunload', stopPing)
      unloadListenerAdded = true
    }
  }

  const stopPing = () => {
    console.log('🛑 停止在线 ping')

    if (globalThis.__onlinePingIntervalId) {
      clearInterval(globalThis.__onlinePingIntervalId)
      globalThis.__onlinePingIntervalId = null
    }

    if (unloadListenerAdded) {
      window.removeEventListener('beforeunload', stopPing)
      unloadListenerAdded = false
    }
  }

  const monitorTokenChange = () => {
    let lastToken = getToken()

    tokenCheckIntervalId = setInterval(() => {
      const currentToken = getToken()
      if (currentToken !== lastToken) {
        console.log('🔄 检测到 token 变化')
        lastToken = currentToken

        if (currentToken) {
          startPing()
        } else {
          stopPing()
        }
      }
    }, 1000) // 每秒检查一次 token
  }

  const stopTokenMonitor = () => {
    if (tokenCheckIntervalId) {
      clearInterval(tokenCheckIntervalId)
      tokenCheckIntervalId = null
    }
  }

  onMounted(() => {
    monitorTokenChange()
    if (getToken()) startPing()
  })

  onUnmounted(() => {
    stopPing()
    stopTokenMonitor()
  })

  return {
    startPing,
    stopPing,
  }
}

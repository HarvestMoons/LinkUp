import { createI18n } from 'vue-i18n'
import store from '@/store'  // 确保正确引入 Vuex store

// 自动加载所有 locales 下的 JSON 文件
const loadLocaleMessages = () => {
    const locales = require.context(
        '../locales',
        true,
        /[A-Za-z0-9-_,\s]+\.json$/i
    )
    const messages = {}
    locales.keys().forEach(key => {
        const matched = key.match(/([A-Za-z0-9-_]+)\/([A-Za-z0-9-_]+)\.json$/i)
        if (matched) {
            const locale = matched[1] // 语言类型 (en/zh-CN)
            const moduleName = matched[2] // 文件名 (validation)
            messages[locale] = messages[locale] || {}
            messages[locale][moduleName] = locales(key)
        }
    })
    return messages
}

const i18n = createI18n({
    legacy: false,
    locale: localStorage.getItem('userLanguage') || 'en', // 优先使用用户保存的语言
    fallbackLocale: 'en', // 添加回退语言
    messages: loadLocaleMessages() // 加载所有语言文件
})

// 监听语言变化并更新 i18n 的 locale
store.subscribe((mutation) => {
    if (mutation.type === 'setLanguage') {
        i18n.global.locale = mutation.payload
        localStorage.setItem('userLanguage', mutation.payload) // 确保持久化
    }
})

export const t = i18n.global.t
export default i18n

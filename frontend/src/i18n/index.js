import { createI18n } from 'vue-i18n'
import store from '@/store'

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
            const locale = matched[1]
            const moduleName = matched[2]
            messages[locale] = messages[locale] || {}
            messages[locale][moduleName] = locales(key)
        }
    })
    return messages
}

// 初始化时从 localStorage 获取保存的语言
const savedLocale = localStorage.getItem('userLanguage')

// 确保 store 和 localStorage 同步
if (savedLocale && !store.state.language) {
    store.commit('setLanguage', savedLocale)
}

const i18n = createI18n({
    legacy: false,
    locale: store.state.language || savedLocale || 'en', // 三重保障
    fallbackLocale: 'en',
    messages: loadLocaleMessages(),
    sync: true, // 确保与 Vuex 同步
    silentTranslationWarn: true // 禁用警告
})

// 强化同步机制
store.watch(
    state => state.language,
    (newLang) => {
        if (newLang) {
            i18n.global.locale.value = newLang
            localStorage.setItem('userLanguage', newLang)
        }
    },
    { immediate: true }
)

// 添加容错机制
i18n.global.locale.value = i18n.global.locale.value || store.state.language || 'en'

export const t = i18n.global.t
export default i18n
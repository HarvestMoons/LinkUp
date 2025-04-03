// src/i18n.js
import { createI18n } from 'vue-i18n'

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
    locale: 'en',
    messages: loadLocaleMessages() // 加载所有语言文件
})

export const t = i18n.global.t
export default i18n
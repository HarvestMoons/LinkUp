/**
 * 声明全局 Vue 属性（供 IDE 识别类型）
 * @typedef {import('vue').ComponentCustomProperties} ComponentCustomProperties
 * @typedef {import('vue-i18n').TranslateFunction} TranslateFunction
 * @typedef {import('vuex').Store} VuexStore
 */

/**
 * @type {ComponentCustomProperties}
 * @property {TranslateFunction} $t - i18n 的翻译函数
 * @property {VuexStore} $store - Vuex 的 store 实例
 */
const VueGlobalProperties = {}

// 仅用于类型提示，不需要导出实际值
export default VueGlobalProperties

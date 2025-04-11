module.exports = {
  root: true,
  env: {
    browser: true,
    es2021: true,
    node: true,
  },
  extends: [
    'eslint:recommended', // ESLint 基础规则
    'plugin:vue/vue3-recommended', // Vue 3 官方规则
    'plugin:prettier/recommended', // Prettier 兼容规则（必须放在最后）
  ],
  parserOptions: {
    ecmaVersion: 12, // ES2021
    sourceType: 'module',
  },
  rules: {
    // ESLint 规则
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',

    // Vue 规则
    'vue/multi-word-component-names': 'off', // 允许单文件组件名（如 Home.vue）
    'vue/html-self-closing': [
      'error',
      {
        html: { void: 'always' }, // 自动闭合空标签（如 <img />）
      },
    ],

    // Prettier 规则（通过 .prettierrc 配置，这里仅覆盖冲突部分）
    'prettier/prettier': [
      'warn',
      {
        printWidth: 100, // 换行宽度
        semi: false, // 无分号
        singleQuote: true, // 使用单引号
        trailingComma: 'es5', // 尾随逗号
        endOfLine: 'auto', // 自动换行符
      },
    ],
  },
  globals: {
    // 解决 <script setup> 宏的全局变量警告
    defineProps: 'readonly',
    defineEmits: 'readonly',
    defineExpose: 'readonly',
    withDefaults: 'readonly',
  },
}

const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  devServer: {
    port: 3000, // 前端端口 3000
    proxy: {
      '/api': {
        target: "http://localhost:8099/", // 后端端口 8099
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        },
        logLevel: 'debug'
      }
    }
  },
  transpileDependencies: true
})

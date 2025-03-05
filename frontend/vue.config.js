const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  devServer: {
    port: 3000, // 前端运行在 3000 端口
    proxy: {
      '/api': {
        target: "http://localhost:8099/", // 代理 API 请求
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        },
        logLevel: 'debug'
      },
      '/chatroom': {  // 添加 WebSocket 代理
        target: "http://localhost:8099",
        ws: true, // 关键：启用 WebSocket 代理
        changeOrigin: true,
        logLevel: 'debug'
      }
    }
  },
  transpileDependencies: true
});

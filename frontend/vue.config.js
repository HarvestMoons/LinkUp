const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
    devServer: {
        host: '0.0.0.0',
        port: 3000,
        proxy: {
            '/api': {
                target: "http://8.155.47.138:8099",// 代理 API 请求 // localhost
                changeOrigin: true,
                pathRewrite: {
                    '^/api': ''
                },
                logLevel: 'debug'
            },
            '/chatroom': {  // 添加 WebSocket 代理
                target: "http://8.155.47.138:8099",
                ws: true, // 关键：启用 WebSocket 代理
                changeOrigin: true,
                logLevel: 'debug'
            }
        }
    },

    // 生产环境配置
    productionSourceMap: false, // 关闭 source map 减小打包体积
    transpileDependencies: true,

    configureWebpack: {  // 修改Webpack默认配置
        optimization: {    // 优化配置
            splitChunks: {   // 代码分割规则
                chunks: 'all', // 对所有模块进行分割
                maxSize: 244 * 1024 // 单个chunk最大244KB（250,256字节）
            }
        },
        module: {
            rules: [
                {
                    test: /\.md$/,
                    use: "raw-loader",
                },
            ],
        },
    }
});

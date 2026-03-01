# 第一阶段：构建构建产物
FROM node:22 AS build
WORKDIR /app
COPY package*.json ./
# 安装依赖
RUN npm install
COPY . .
RUN npm run build

# 第二阶段：使用 Nginx 运行
FROM nginx:alpine
# 删除默认配置并复制自定义的 Nginx 配置
COPY nginx/default.conf /etc/nginx/conf.d/default.conf
# 将构建产物复制到 Nginx 的默认静态目录
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

# 使用默认 nginx 启动命令
CMD ["nginx", "-g", "daemon off;"]
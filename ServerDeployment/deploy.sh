#!/bin/bash
echo "🚀 开始部署 LinkUp 项目..."

# 拉取最新的镜像
echo "正在拉取后端镜像..."
docker pull beecool/linkup-backend:latest

echo "正在拉取前端镜像..."
docker pull beecool/linkup-frontend:latest

# 启动或更新服务
echo "正在启动容器..."
docker compose up -d --force-recreate

# 清理未使用的闲置镜像 (可选，推荐清理旧版本释放空间)
echo "正在清理旧的悬空镜像..."
docker image prune -f

echo "✅ 部署完成! LinkUp 前端应用访问地址: http://服务器IP:8666"
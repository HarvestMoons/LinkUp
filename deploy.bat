@echo off
chcp 65001 >nul
echo === LinkUp Docker镜像构建与推送脚本 ===

:: --------------------------
:: 1. 登录 Docker Hub (如果需要)
:: --------------------------
echo.
echo 1. 确认Docker登录状态...
docker login
if %errorlevel% neq 0 (
    echo 登录失败，请检查Docker是否运行或账号密码是否正确
    pause
    exit /b 1
)

:: --------------------------
:: 2. 定义镜像变量 (假设您的 DockerHub 用户名为 beecool，请根据实际情况修改)
:: --------------------------
set backend_local=linkup-backend:latest
set backend_remote=beecool/linkup-backend:latest

set frontend_local=linkup-frontend:latest
set frontend_remote=beecool/linkup-frontend:latest

:: --------------------------
:: 3. 构建本地镜像
:: --------------------------
echo.
echo 2. 开始构建本地镜像...
echo 构建后端镜像...
docker build -t %backend_local% ./backend
if %errorlevel% neq 0 (
    echo ❌ 后端镜像构建失败
    pause
    exit /b 1
)

echo 构建前端镜像...
docker build -t %frontend_local% ./frontend
if %errorlevel% neq 0 (
    echo ❌ 前端镜像构建失败
    pause
    exit /b 1
)

:: --------------------------
:: 4. 推送后端镜像
:: --------------------------
echo.
echo 3. 推送后端镜像至 Docker Hub...
docker tag %backend_local% %backend_remote%
docker push %backend_remote%
if %errorlevel% equ 0 (
    echo ✅ 后端镜像推送成功: %backend_remote%
) else (
    echo ❌ 后端镜像推送失败
    pause
    exit /b 1
)

:: --------------------------
:: 5. 推送前端镜像
:: --------------------------
echo.
echo 4. 推送前端镜像至 Docker Hub...
docker tag %frontend_local% %frontend_remote%
docker push %frontend_remote%
if %errorlevel% equ 0 (
    echo ✅ 前端镜像推送成功: %frontend_remote%
) else (
    echo ❌ 前端镜像推送失败
    pause
    exit /b 1
)

echo.
echo 🎉 项目镜像构建与推送全部完成！
pause

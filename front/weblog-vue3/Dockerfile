# ========================================
# 多阶段构建: 构建阶段
# ========================================
FROM node:18-alpine as build-stage

# 设置工作目录
WORKDIR /app

# 复制package.json和package-lock.json
COPY package*.json ./

# 安装依赖(使用国内镜像加速)
RUN npm config set registry https://registry.npmmirror.com && \
    npm install

# 复制源代码
COPY . .

# 构建生产版本
RUN npm run build

# ========================================
# 多阶段构建: 生产阶段
# ========================================
FROM nginx:alpine as production-stage

# 复制构建产物到nginx目录
COPY --from=build-stage /app/dist /usr/share/nginx/html

# 复制nginx配置文件
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 80

# 启动nginx
CMD ["nginx", "-g", "daemon off;"]

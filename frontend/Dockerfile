# ===== Stage 1: Build =====
FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

# Nếu dùng Vite: build ra thư mục "dist"
# Nếu dùng CRA: build ra thư mục "build" -> đổi COPY bên dưới thành /app/build
RUN npm run build

# ===== Stage 2: Serve bằng nginx =====
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
# Đặt trong /etc/nginx/templates/ để image nginx tự động chạy envsubst
# thay ${BACKEND_URL} bằng giá trị thật lúc container start
COPY default.conf.template /etc/nginx/templates/default.conf.template
ENV BACKEND_URL=http://localhost:8080

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
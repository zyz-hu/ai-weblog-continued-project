import axios from "axios";
import { getToken } from "@/composables/cookie";
import { showMessage } from "@/composables/util";
import { useUserStore } from "@/stores/user";

// 创建 Axios 实例
const instance = axios.create({
    baseURL: "/api", // API 基础路径，开发时由 Vite 代理到网关
    timeout: 7000,
});

// 请求拦截：统一带上 Token
instance.interceptors.request.use(function (config) {
    // Allow explicit gateway-prefixed URLs like /api/robot/... without producing /api/api/...
    if (config.url?.startsWith("/api/")) {
        config.baseURL = "";
    }
    const token = getToken();
    if (token) {
        config.headers["Authorization"] = "Bearer " + token;
    }
    return config;
}, function (error) {
    return Promise.reject(error);
});

// 响应拦截：处理统一错误
instance.interceptors.response.use(function (response) {
    return response.data;
}, function (error) {
    let status = error.response?.status;

    // 401 未登录/Token 失效：登出并跳到登录页，避免页面刷新死循环
    if (status === 401) {
        let userStore = useUserStore();
        userStore.logout();
        if (window.location.hash !== "#/login") {
            window.location.hash = "#/login";
        }
        return Promise.reject(error);
    }

    // 其他错误提示
    let errorMsg = error.response?.data?.message || "请求失败";
    showMessage(errorMsg, "error");
    return Promise.reject(error);
});

export default instance;

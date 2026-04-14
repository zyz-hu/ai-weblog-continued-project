<template>
    <div class="min-h-screen grid grid-cols-1 md:grid-cols-2">
        <div class="hidden md:flex flex-col justify-center items-center bg-slate-900 relative overflow-hidden">
            <div class="absolute inset-0 bg-gradient-to-br from-slate-900 via-indigo-950 to-slate-900"></div>
            <div class="absolute top-0 left-0 w-full h-full overflow-hidden">
                <div class="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] rounded-full bg-indigo-600/20 blur-[100px] animate-float"></div>
                <div class="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] rounded-full bg-teal-600/20 blur-[100px] animate-float animation-delay-2000"></div>
            </div>
            
            <div class="relative z-10 text-center px-10">
                <div class="mb-8 animate-float">
                    <img src="@/assets/weblog-logo.png" class="w-24 h-24 mx-auto bg-white rounded-2xl p-2 shadow-2xl" />
                </div>
                <h2 class="text-4xl font-bold text-white mb-4 tracking-tight">Weblog Admin</h2>
                <p class="text-indigo-200 text-lg max-w-md mx-auto">
                    构建属于你的数字花园。极致的写作体验，从这里开始。
                </p>
            </div>
        </div>

        <div class="flex items-center justify-center bg-white dark:bg-slate-950 p-8 sm:p-12 md:p-20 relative">
            <div class="w-full max-w-md space-y-8 relative z-10">
                <div class="text-center md:text-left">
                    <h2 class="text-3xl font-bold tracking-tight text-slate-900 dark:text-white">
                        欢迎回来
                    </h2>
                    <p class="mt-2 text-sm text-slate-600 dark:text-slate-400">
                        请输入您的管理员账号以继续
                    </p>
                </div>

                <el-form class="mt-8 space-y-6" ref="formRef" :rules="rules" :model="form" size="large">
                    <el-form-item prop="username">
                        <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" 
                            class="!rounded-lg h-12" />
                    </el-form-item>
                    
                    <el-form-item prop="password">
                        <el-input type="password" v-model="form.password" placeholder="密码" :prefix-icon="Lock" show-password 
                            class="!rounded-lg h-12" />
                    </el-form-item>

                    <div class="flex items-center justify-between text-sm">
                        <div class="flex items-center">
                            <input id="remember-me" type="checkbox" class="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-600">
                            <label for="remember-me" class="ml-2 block text-slate-700 dark:text-slate-300">记住我</label>
                        </div>
                        <a href="#" class="font-medium text-indigo-600 hover:text-indigo-500">忘记密码?</a>
                    </div>

                    <el-button class="w-full h-12 text-lg font-medium !rounded-lg !bg-indigo-600 !border-indigo-600 hover:!bg-indigo-500 hover:!border-indigo-500 transition-all shadow-lg shadow-indigo-500/30" 
                        type="primary" :loading="loading" @click="onSubmit">
                        登 录
                    </el-button>
                </el-form>
                
                <p class="mt-10 text-center text-sm text-slate-500">
                    Power by Vue 3 + Vite + SpringBoot
                </p>
            </div>
        </div>
    </div>
</template>

<script setup>
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/admin/user'
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { showMessage } from '@/composables/util'
import { setToken } from '@/composables/cookie'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const form = reactive({ username: '', password: '' })
const loading = ref(false)
const formRef = ref(null)

const rules = {
    username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
    password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
}

const onSubmit = () => {
    formRef.value.validate((valid) => {
        if (!valid) return false
        loading.value = true
        login(form.username, form.password).then((res) => {
            if (res.success == true) {
                showMessage('登录成功')
                setToken(res.data.token)
                userStore.setUserInfo()
                    .catch(() => {}) // 忽略用户信息回填失败，仍然刷新前台页面
                    .finally(() => window.location.replace('/'))
            } else {
                showMessage(res.message, 'error')
            }
        }).finally(() => { loading.value = false })
    })
}

function onKeyUp(e) { if (e.key == 'Enter') onSubmit() }
onMounted(() => document.addEventListener('keyup', onKeyUp))
onBeforeUnmount(() => document.removeEventListener('keyup', onKeyUp))
</script>

<style scoped>
@keyframes float {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-10px); }
}
.animate-float {
    animation: float 6s ease-in-out infinite;
}
.animation-delay-2000 {
    animation-delay: 2s;
}
:deep(.el-input__wrapper) {
    background-color: #f8fafc;
    box-shadow: 0 0 0 1px #e2e8f0 inset;
    transition: all 0.3s;
}
:deep(.el-input__wrapper.is-focus) {
    background-color: #fff;
    box-shadow: 0 0 0 2px #4f46e5 inset !important;
}
/* 暗黑模式适配 */
:global(.dark) :deep(.el-input__wrapper) {
    background-color: #1e293b;
    box-shadow: 0 0 0 1px #334155 inset;
}
</style>

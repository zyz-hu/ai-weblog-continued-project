<template>
    <!-- 外层容器 -->
    <el-container class="admin-shell">

        <!-- 左边侧边栏 -->
        <el-aside :width='menuStore.menuWidth' class="transition-all duration-300">
            <AdminMenu></AdminMenu>
        </el-aside>

        <!-- 右边主内容区域 -->
        <el-container>
            <!-- 顶栏容器 -->
            <el-header class="admin-header-bar">
                <AdminHeader></AdminHeader>
            </el-header>

            <el-main class="admin-main">
                <div class="admin-surface">
                    <!-- 标签导航栏 -->
                    <AdminTagList></AdminTagList>

                    <!-- 主内容（根据路由动态展示不同页面） -->
                    <router-view v-slot="{ Component }">
                        <Transition name="fade">
                            <!-- max 指定最多缓存 10 个组件 -->
                            <KeepAlive :max="10">
                                <component :is="Component"></component>
                            </KeepAlive>
                        </Transition>

                    </router-view>
                </div>
            </el-main>

            <!-- 底栏容器 -->
            <el-footer>
                <AdminFooter></AdminFooter>
            </el-footer>
        </el-container>
    </el-container>
</template>

<script setup>
// 引入组件
import AdminFooter from './components/AdminFooter.vue';
import AdminHeader from './components/AdminHeader.vue';
import AdminMenu from './components/AdminMenu.vue';
import AdminTagList from './components/AdminTagList.vue';

import { useMenuStore } from '@/stores/menu'

const menuStore = useMenuStore()
</script>

<style scoped>
.admin-shell {
    min-height: 100vh;
    background: radial-gradient(circle at 15% 15%, rgba(99, 102, 241, 0.12) 0, rgba(99, 102, 241, 0) 32%),
        radial-gradient(circle at 85% 20%, rgba(14, 165, 233, 0.15) 0, rgba(14, 165, 233, 0) 35%),
        radial-gradient(circle at 50% 80%, rgba(45, 212, 191, 0.16) 0, rgba(45, 212, 191, 0) 36%),
        #f6f7fb;
}

.admin-main {
    padding: 24px;
}

.admin-surface {
    border-radius: 24px;
    background: rgba(255, 255, 255, 0.86);
    box-shadow: 0 20px 60px rgba(15, 23, 42, 0.06), 0 8px 20px rgba(15, 23, 42, 0.04);
    border: 1px solid rgba(226, 232, 240, 0.9);
    padding: 12px 12px 24px;
    backdrop-filter: blur(18px);
}

.el-header {
    padding: 0!important;
}

.el-footer {
    padding: 0!important;
}

.admin-header-bar {
    background: transparent;
    padding: 12px 20px 0;
}

/* 内容区域过渡动画：淡入淡出效果 */
/* 刚开始进入时 */
.fade-enter-from {
    /* 透明度 */
    opacity: 0;
}

/* 刚开始结束 */
.fade-enter-to {
    opacity: 1;
}

/* 刚开始离开 */
.fade-leave-from {
  opacity: 1;
}

/* 离开已结束 */
.fade-leave-to {
  opacity: 0;
}

/* 离开进行中 */
.fade-leave-active {
    transition: all 0.3s;
}

/* 进入进行中 */
.fade-enter-active {
    transition: all 0.3s;
    transition-delay: 0.3s;
}
</style>

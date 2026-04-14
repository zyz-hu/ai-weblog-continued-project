<template>
    <div class="fixed overflow-y-auto bg-slate-900 h-screen text-white transition-all duration-300 z-50 border-r border-slate-800" 
         :style="{ width: menuStore.menuWidth }">
        
        <div class="flex items-center justify-center h-[64px] border-b border-slate-800/50 bg-slate-900/50 backdrop-blur">
            <div v-if="menuStore.menuWidth == '250px'" class="flex items-center gap-2 font-bold text-xl tracking-wider animate-fade-in-up">
                <span class="text-indigo-500">W</span>eblog
            </div>
            <div v-else class="font-bold text-xl text-indigo-500">W</div>
        </div>

        <el-menu :default-active="defaultActive" @select="handleSelect" 
                 :collapse="isCollapse" :collapse-transition="false"
                 background-color="#0f172a" text-color="#94a3b8" active-text-color="#fff"
                 class="border-none py-4">
            
            <template v-for="(item, index) in menus" :key="index">
                <el-menu-item :index="item.path" class="my-1 mx-2 rounded-lg hover:bg-slate-800 transition-all duration-200">
                    <el-icon :size="18" class="mr-2">
                        <component :is="item.icon"></component>
                    </el-icon>
                    <span class="font-medium">{{ item.name }}</span>
                </el-menu-item>
            </template>
        </el-menu>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMenuStore } from '@/stores/menu'

const menuStore = useMenuStore()
const route = useRoute()
const router = useRouter()
const isCollapse = computed(() =>  !(menuStore.menuWidth == '250px'))
const defaultActive = ref(route.path)

const handleSelect = (path) => {
    router.push(path)
}

const menus = [
    { 'name': '仪表盘', 'icon': 'Monitor', 'path': '/admin/index' },
    { 'name': '文章管理', 'icon': 'Document', 'path': '/admin/article/list' },
    { 'name': '分类管理', 'icon': 'FolderOpened', 'path': '/admin/category/list' },
    { 'name': '标签管理', 'icon': 'PriceTag', 'path': '/admin/tag/list' },
    { 'name': '博客设置', 'icon': 'Setting', 'path': '/admin/blog/settings' },
    { 'name': '知识库管理', 'icon': 'Notebook', 'path': '/admin/knowledge/base' },
]
</script>

<style scoped>
/* 覆盖 Element Menu 样式以实现更高级的效果 */
:deep(.el-menu-item.is-active) {
    background: linear-gradient(90deg, #4f46e5 0%, #4338ca 100%);
    box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
}
:deep(.el-menu-item) {
    height: 50px;
    line-height: 50px;
}
:deep(.el-menu-item:hover) {
    background-color: #1e293b !important;
    color: #fff !important;
}
</style>

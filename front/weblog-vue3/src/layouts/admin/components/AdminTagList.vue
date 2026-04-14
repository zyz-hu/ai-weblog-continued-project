<template>
    <div class="flex items-center justify-between mb-4 border-b border-slate-100 dark:border-slate-700/50 pb-2">
        
        <div class="flex-1 overflow-hidden">
            <el-tabs 
                v-model="activeTab" 
                type="card" 
                class="admin-tabs" 
                @tab-remove="removeTab" 
                @tab-change="tabChange"
            >
                <el-tab-pane 
                    v-for="item in tabList" 
                    :key="item.path" 
                    :label="item.title" 
                    :name="item.path" 
                    :closable="item.path != '/admin/index'"
                >
                </el-tab-pane>
            </el-tabs>
        </div>

        <div class="ml-4 flex-shrink-0">
            <el-dropdown @command="handleCloseTab" trigger="click">
                <span class="flex items-center justify-center w-8 h-8 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors text-slate-500 cursor-pointer">
                    <el-icon class="transform rotate-90"><MoreFilled /></el-icon>
                </span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item command="closeOthers">
                            <el-icon><Close /></el-icon>关闭其他
                        </el-dropdown-item>
                        <el-dropdown-item command="closeAll">
                            <el-icon><CircleClose /></el-icon>关闭全部
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>

<script setup>
import { useTabList } from '@/composables/useTagList.js'
import { MoreFilled, Close, CircleClose } from '@element-plus/icons-vue'

const { menuStore, activeTab, tabList, tabChange, removeTab, handleCloseTab } = useTabList()
</script>

<style scoped>
/* 深度选择器覆盖 Element Plus 默认样式 */
:deep(.el-tabs__header) {
    margin: 0;
    border-bottom: none !important;
}

:deep(.el-tabs__nav) {
    border: none !important;
}

:deep(.el-tabs__item) {
    height: 34px;
    line-height: 34px;
    border: 1px solid transparent !important;
    border-radius: 8px;
    margin-right: 6px;
    padding: 0 16px !important;
    font-size: 13px;
    color: #64748b; /* slate-500 */
    background: transparent;
    transition: all 0.2s;
}

/* 悬停状态 */
:deep(.el-tabs__item:hover) {
    color: #4f46e5; /* indigo-600 */
    background: #eef2ff; /* indigo-50 */
}

/* 选中状态 */
:deep(.el-tabs__item.is-active) {
    color: #4f46e5 !important;
    background: #e0e7ff !important; /* indigo-100 */
    font-weight: 600;
}

/* 选中状态下的关闭按钮 */
:deep(.el-tabs__item.is-active .el-icon-close) {
    color: #4f46e5;
    width: 14px; /* 调整关闭按钮大小 */
}

:deep(.el-tabs__item .el-icon-close:hover) {
    background-color: #c7d2fe; /* indigo-200 */
    color: #4338ca;
}

/* 导航按钮（当标签过多时出现） */
:deep(.el-tabs__nav-prev), :deep(.el-tabs__nav-next) {
    line-height: 34px;
    font-size: 14px;
}

/* 暗黑模式适配 */
:global(.dark) :deep(.el-tabs__item) {
    color: #94a3b8;
}
:global(.dark) :deep(.el-tabs__item:hover) {
    color: #818cf8;
    background: rgba(99, 102, 241, 0.1);
}
:global(.dark) :deep(.el-tabs__item.is-active) {
    color: #818cf8 !important;
    background: rgba(99, 102, 241, 0.2) !important;
}
</style>
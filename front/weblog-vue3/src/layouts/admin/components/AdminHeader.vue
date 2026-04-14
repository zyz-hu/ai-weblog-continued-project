<template>
    <el-affix :offset="0">
        <div class="admin-header">
            <div class="flex items-center h-[72px] px-4 gap-3">
                <el-tooltip class="box-item" effect="dark" :content="menuStore.menuWidth == '250px' ? '折叠菜单' : '展开菜单'"
                    placement="bottom">
                    <div class="action-btn" @click="handleMenuWidth">
                        <el-icon>
                            <Fold v-if="menuStore.menuWidth == '250px'" />
                            <Expand v-else />
                        </el-icon>
                    </div>
                </el-tooltip>

                <div class="flex items-center gap-3 pr-4 border-r border-white/60">
                    <div class="brand-mark">
                        <span class="text-white font-semibold">WB</span>
                    </div>
                    <div class="leading-tight">
                        <p class="text-sm font-semibold text-slate-800">Weblog Lab</p>
                        <p class="text-xs text-slate-500">写给自己的后台</p>
                    </div>
                </div>

                <div class="flex-1 max-w-[520px] px-4">
                    <el-input v-model="searchValue" placeholder="搜索文章、用户或操作" clearable class="search-input"
                        :prefix-icon="Search" @keyup.enter="handleSearch" />
                </div>

                <div class="ml-auto flex items-center gap-2">
                    <el-tooltip class="box-item" effect="dark" content="消息" placement="bottom">
                        <div class="action-btn" @click="handleOpenMessages">
                            <el-icon>
                                <ChatDotRound />
                            </el-icon>
                        </div>
                    </el-tooltip>

                    <el-tooltip class="box-item" effect="dark" content="通知" placement="bottom">
                        <div class="action-btn" @click="handleOpenNotifications">
                            <el-icon>
                                <BellFilled />
                            </el-icon>
                        </div>
                    </el-tooltip>

                    <el-tooltip class="box-item" effect="dark" :content="themeTooltip" placement="bottom">
                        <div class="action-btn" @click="handleToggleTheme">
                            <el-icon>
                                <Moon v-if="isDark" />
                                <Sunny v-else />
                            </el-icon>
                        </div>
                    </el-tooltip>

                    <el-tooltip class="box-item" effect="dark" content="刷新" placement="bottom">
                        <div class="action-btn" @click="handleRefresh">
                            <el-icon>
                                <Refresh />
                            </el-icon>
                        </div>
                    </el-tooltip>

                    <el-tooltip class="box-item" effect="dark" content="回到前台" placement="bottom">
                        <div class="action-btn" @click="router.push('/')">
                            <el-icon>
                                <Monitor />
                            </el-icon>
                        </div>
                    </el-tooltip>

                    <el-tooltip class="box-item" effect="dark" content="全屏" placement="bottom">
                        <div class="action-btn" @click="toggle">
                            <el-icon>
                                <FullScreen v-if="!isFullscreen" />
                                <Aim v-else />
                            </el-icon>
                        </div>
                    </el-tooltip>

                    <el-dropdown class="avatar-dropdown" @command="handleCommand">
                        <span class="el-dropdown-link flex items-center gap-3 text-gray-700 text-sm">
                            <div class="relative">
                                <el-avatar class="shadow" :size="38"
                                    src="https://avatars.githubusercontent.com/u/143480932?s=400&v=4" />
                                <span class="absolute -right-1 -bottom-1 w-3 h-3 bg-emerald-400 border-2 border-white rounded-full"></span>
                            </div>
                            <div class="text-left leading-tight">
                                <p class="font-semibold text-slate-800">{{ userStore.userInfo.username }}</p>
                                <p class="text-xs text-slate-500">{{ userRole }} · {{ userSubtitle }}</p>
                            </div>
                            <el-icon class="el-icon--right text-slate-500">
                                <arrow-down />
                            </el-icon>
                        </span>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item command="updatePassword">修改密码</el-dropdown-item>
                                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
            </div>
        </div>

        <FormDialog ref="formDialogRef" title="修改密码" destroyOnClose @submit="onSubmit">
            <el-form ref="formRef" :rules="rules" :model="form">
                <el-form-item label="用户名" prop="username" label-width="120px" size="large">
                    <el-input v-model="form.username" placeholder="请输入用户名" clearable disabled />
                </el-form-item>
                <el-form-item label="新密码" prop="password" label-width="120px" size="large">
                    <el-input type="password" v-model="form.password" placeholder="请输入新密码" clearable show-password />
                </el-form-item>
                <el-form-item label="确认新密码" prop="rePassword" label-width="120px" size="large">
                    <el-input type="password" v-model="form.rePassword" placeholder="请确认新密码" clearable show-password />
                </el-form-item>
            </el-form>
        </FormDialog>
    </el-affix>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue';
import { useMenuStore } from '@/stores/menu';
import { useUserStore } from '@/stores/user';
import { useDark, useFullscreen, useToggle } from '@vueuse/core';
import { updateAdminPassword } from '@/api/admin/user';
import { showMessage, showModel } from '@/composables/util';
import { useRouter } from 'vue-router';
import FormDialog from '@/components/FormDialog.vue';
import { BellFilled, ChatDotRound, Moon, Search, Sunny } from '@element-plus/icons-vue';

const router = useRouter();
const { isFullscreen, toggle } = useFullscreen();
const isDark = useDark();
const toggleDark = useToggle(isDark);
const menuStore = useMenuStore();
const userStore = useUserStore();

const userRole = computed(() => {
    if (userStore.userInfo.roles && userStore.userInfo.roles.length > 0) {
        return userStore.userInfo.roles[0];
    }
    return userStore.userInfo.role || '管理员';
});
const userSubtitle = computed(() => userStore.userInfo.title || '持续输出');

const searchValue = ref('');

const handleMenuWidth = () => {
    menuStore.handleMenuWidth();
};

const handleSearch = () => {
    if (!searchValue.value) {
        showMessage('请输入搜索关键字', 'info');
        return;
    }
    showMessage(`搜索 "${searchValue.value}"`, 'success');
};

const handleToggleTheme = () => {
    toggleDark();
};

const handleRefresh = () => location.reload();

const handleOpenMessages = () => {
    showMessage('打开消息中心', 'info');
};

const handleOpenNotifications = () => {
    showMessage('查看通知提醒', 'info');
};

const formDialogRef = ref(false);
const themeTooltip = computed(() => isDark.value ? '切换到浅色' : '切换到深色');

const handleCommand = (command) => {
    if (command == 'updatePassword') {
        formDialogRef.value.open();
    } else if (command == 'logout') {
        logout();
    }
};

function logout() {
    showModel('是否确认要退出登录？').then(() => {
        userStore.logout();
        showMessage('退出登录成功！');
        router.push('/login');
    });
}

const formRef = ref(null);

const form = reactive({
    username: userStore.userInfo.username || '',
    password: '',
    rePassword: ''
});

watch(() => userStore.userInfo.username, (newValue) => {
    form.username = newValue;
});

const rules = {
    username: [
        {
            required: true,
            message: '用户名不能为空',
            trigger: 'blur'
        }
    ],
    password: [
        {
            required: true,
            message: '密码不能为空',
            trigger: 'blur',
        },
    ],
    rePassword: [
        {
            required: true,
            message: '确认密码不能为空',
            trigger: 'blur',
        },
    ]
};

const onSubmit = () => {
    formRef.value.validate((valid) => {
        if (!valid) {
            return false;
        }

        if (form.password != form.rePassword) {
            showMessage('两次密码输入不一致，请检查！', 'warning');
            return;
        }

        formDialogRef.value.showBtnLoading();
        updateAdminPassword(form).then((res) => {
            if (res.success == true) {
                showMessage('密码重置成功，请重新登录');
                userStore.logout();
                formDialogRef.value.close();
                router.push('/login');
            } else {
                let message = res.message;
                showMessage(message, 'error');
            }
        }).finally(() => formDialogRef.value.closeBtnLoading());
    });
};
</script>

<style scoped>
.admin-header {
    background: linear-gradient(120deg, rgba(255, 255, 255, 0.96), rgba(244, 246, 255, 0.9));
    backdrop-filter: blur(12px);
    border-bottom: 1px solid rgba(226, 232, 240, 0.7);
    box-shadow: 0 6px 18px rgba(15, 23, 42, 0.08);
}

.action-btn {
    @apply w-11 h-11 flex items-center justify-center rounded-full bg-white/80 text-gray-700 shadow transition duration-200 ease-out transform hover:-translate-y-0.5 hover:shadow-lg hover:bg-white;
}

.brand-mark {
    @apply w-10 h-10 rounded-2xl bg-gradient-to-br from-indigo-500 via-purple-500 to-blue-400 flex items-center justify-center shadow-md;
}

.avatar-dropdown {
    @apply flex items-center rounded-full bg-white/80 px-3 py-2 shadow border border-white/60 transition hover:-translate-y-0.5 hover:shadow-lg;
}

.search-input :deep(.el-input__wrapper) {
    border-radius: 9999px;
    background: rgba(255, 255, 255, 0.85);
    box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04), 0 8px 20px rgba(15, 23, 42, 0.08);
}

.search-input :deep(.el-input__wrapper:hover) {
    border-color: #6366f1;
}

:global(.dark) .admin-header {
    background: linear-gradient(120deg, rgba(30, 41, 59, 0.9), rgba(30, 41, 59, 0.75));
    border-bottom: 1px solid rgba(51, 65, 85, 0.8);
    box-shadow: 0 6px 18px rgba(0, 0, 0, 0.4);
}
</style>

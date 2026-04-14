<template>
  <div 
    class="ai-robot-shell flex min-h-screen overflow-hidden transition-colors duration-300 relative"
    style="background-color: var(--app-bg); color: var(--app-text);"
  >
    <Sidebar :sidebarOpen="sidebarOpen" :is-mobile="isMobile" @toggle-sidebar="toggleSidebar" />
    <div
      v-if="isMobile && sidebarOpen"
      class="fixed inset-0 bg-black/35 backdrop-blur-sm z-30 md:hidden"
      @click="toggleSidebar"
    ></div>

    <div
      class="flex-1 flex flex-col h-full relative min-w-0 min-h-0 transition-all duration-300"
      :style="{ marginLeft: !isMobile && sidebarOpen ? '280px' : '0px' }"
    >
      <div class="md:hidden sticky top-0 z-30 flex items-center justify-between px-4 py-3 bg-[var(--app-bg)] border-b border-gray-100 dark:border-gray-800 relative">
        <div class="flex items-center gap-2">
          <button
            class="p-2 rounded-lg bg-gray-100 dark:bg-[#1e1f20] text-gray-700 dark:text-gray-200 border border-gray-200 dark:border-gray-700 shadow-sm"
            @click="toggleSidebar"
            type="button"
          >
            <SvgIcon name="sidebar-open" customCss="w-5 h-5" />
          </button>
          <button
            class="px-3 py-2 rounded-lg bg-gray-100 dark:bg-[#1e1f20] text-gray-700 dark:text-gray-200 border border-gray-200 dark:border-gray-700 shadow-sm text-sm font-medium"
            @click="goHome"
            type="button"
          >
            返回博客
          </button>
        </div>
        <div class="text-base font-semibold text-gray-800 dark:text-gray-100 tracking-tight absolute left-1/2 -translate-x-1/2 pointer-events-none">{{ mobileTitle }}</div>
        <div class="flex items-center gap-2">
          <button
            class="p-2 rounded-full bg-gray-100 dark:bg-[#1e1f20] text-gray-700 dark:text-gray-200 border border-gray-200 dark:border-gray-700 shadow-sm"
            @click="openNewChat"
            type="button"
          >
            <SvgIcon name="new-chat" customCss="w-4 h-4" />
          </button>
        </div>
      </div>

      <button
        class="fixed top-4 right-4 z-40 hidden md:flex items-center gap-2 px-3 py-2 rounded-full bg-white/60 dark:bg-[#111112]/60 border border-gray-200/70 dark:border-gray-700/70 shadow-sm backdrop-blur cursor-pointer transition hover:-translate-y-0.5 hover:shadow-md"
        @click="goHome"
        type="button"
      >
        <span class="text-sm font-medium text-gray-500 dark:text-gray-400">返回博客</span>
      </button>

      <main class="flex-1 h-full overflow-hidden flex flex-col relative min-h-0 z-10">
        <slot name="main-content"></slot>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
import Sidebar from '@/components/ai-robot/Sidebar.vue';
import { useUserStore } from '@/stores/user';
import { useRoute, useRouter } from 'vue-router';
import SvgIcon from '@/components/ai-robot/SvgIcon.vue';

const sidebarOpen = ref(true);
const isMobile = ref(false);
const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const displayRole = computed(() => {
  if (userStore.userInfo.roles && userStore.userInfo.roles.length > 0) {
    return userStore.userInfo.roles[0];
  }
  return userStore.userInfo.role || 'Guest';
});
const mobileTitle = computed(() => route.name === 'AiRobotChatPage' ? '对话' : '新对话');

const updateViewport = () => {
  const mobile = window.innerWidth < 768;
  if (mobile !== isMobile.value) {
    isMobile.value = mobile;
    sidebarOpen.value = !mobile;
  } else {
    isMobile.value = mobile;
  }
};

const toggleSidebar = () => {
  sidebarOpen.value = !sidebarOpen.value;
};

const openNewChat = () => router.push('/ai-robot');

const goHome = () => router.push('/');

onMounted(() => {
  updateViewport();
  window.addEventListener('resize', updateViewport);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateViewport);
});
</script>

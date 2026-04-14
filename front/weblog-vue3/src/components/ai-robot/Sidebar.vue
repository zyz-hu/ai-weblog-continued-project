<template>
  <div
    :class="[
      sidebarOpen ? 'translate-x-0 opacity-100' : '-translate-x-full opacity-0',
      widthClass,
      'fixed top-0 left-0 flex-shrink-0 h-screen transition-all duration-300 ease-in-out z-40 border-r flex flex-col min-h-0 bg-[var(--sidebar-bg)] md:w-[280px]'
    ]"
    style="border-color: var(--sidebar-border);"
  >
    <div class="p-5 flex items-center gap-3 select-none border-b border-gray-200/70 dark:border-gray-800">
      <SvgIcon name="ai-robot-logo" customCss="w-8 h-8 text-indigo-600 dark:text-indigo-400" />
      <span class="text-xl font-bold tracking-tight text-gray-800 dark:text-gray-100">元智AI</span>
    </div>

    <div class="px-4 mb-4 pt-3 md:pt-4">
      <button
        @click="jumpToIndexPage"
        class="w-full flex items-center gap-3 bg-[#e5e7eb] hover:bg-[#d1d5db] dark:bg-[#2f3032] dark:hover:bg-[#3b3c3e] text-gray-700 dark:text-gray-200 py-3 px-4 rounded-xl transition-all duration-200 font-medium text-sm group shadow-sm"
      >
        <SvgIcon name="new-chat" customCss="w-5 h-5 text-gray-500 dark:text-gray-400 group-hover:scale-110 transition-transform" />
        <span>开启新对话</span>
      </button>
    </div>

    <div class="flex-1 overflow-y-auto px-3 py-2 space-y-1 custom-scrollbar">
      <div class="text-xs font-semibold text-gray-400 px-3 mb-2 tracking-wider">最近</div>
      
      <div
        v-for="(historyChat, index) in historyChats"
        :key="index"
        @click="jumpToChatPage(historyChat.uuid)"
        class="group relative flex items-center gap-3 px-3 py-2.5 rounded-lg cursor-pointer transition-colors duration-200"
        :class="selectedChatId === historyChat.uuid 
          ? 'bg-white dark:bg-[#2f2f2f] text-indigo-600 dark:text-indigo-400 shadow-sm font-medium border border-gray-200 dark:border-transparent' 
          : 'text-gray-600 dark:text-gray-400 hover:bg-gray-200/50 dark:hover:bg-[#2f2f2f]/50'"
      >
        <span class="truncate text-sm flex-1">{{ historyChat.summary }}</span>
        
        <div 
          class="hidden group-hover:flex items-center justify-center p-1.5 hover:bg-gray-300 dark:hover:bg-gray-600 rounded-md transition-colors text-gray-400 hover:text-red-500 z-10"
          @click="handleDeleteChat($event, historyChat.uuid)"
          title="删除对话"
        >
          <svg class="w-4 h-4" viewBox="0 0 20 20" fill="none" stroke="currentColor">
            <path d="M6.5 6h7M8 6v8m4-8v8m-5.5 2h7c.9 0 1.5-.4 1.5-1.3V6.8c0-.9-.6-1.3-1.5-1.3h-7C7.6 5.5 7 6 7 6.8v7.9c0 .9.6 1.3 1.5 1.3z" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M9.25 4.3h1.5c.5 0 .9.4.9.9V6" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
      </div>
    </div>

    <div class="p-4 border-t border-gray-200 dark:border-gray-800 mt-auto" style="border-color: var(--sidebar-border);">
      <button
        @click="toggleTheme"
        class="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-gray-600 dark:text-gray-400 hover:bg-gray-200 dark:hover:bg-[#2f2f2f] transition-colors"
      >
        <SvgIcon :name="isDarkMode ? 'sun' : 'moon'" customCss="w-5 h-5" />
        <span class="text-sm font-medium">{{ isDarkMode ? '切换亮色模式' : '切换暗色模式' }}</span>
      </button>
    </div>
  </div>

  <div class="fixed top-4 left-4 z-50 hidden md:block" v-if="!sidebarOpen">
    <button 
      @click="toggleSidebar"
      class="p-2 rounded-lg bg-white dark:bg-[#1e1f20] shadow-md text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors border border-gray-100 dark:border-gray-700"
    >
      <SvgIcon name="sidebar-open" customCss="w-5 h-5" />
    </button>
  </div>
  
  <div class="fixed top-4 z-50 transition-all duration-300 hidden md:block" :style="{ left: sidebarOpen ? '290px' : '-100px' }">
     <button 
      v-if="sidebarOpen"
      @click="toggleSidebar"
      class="p-2 rounded-lg text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 hover:bg-gray-200 dark:hover:bg-gray-800 transition-colors"
    >
      <SvgIcon name="sidebar-close" customCss="w-5 h-5" />
    </button>
  </div>
</template>

<script setup>
import { useAiThemeStore } from '@/stores/ai-robot/theme';
import { storeToRefs } from 'pinia';
import { ref, onMounted, computed } from 'vue';
import SvgIcon from '@/components/ai-robot/SvgIcon.vue';
import { useRouter, useRoute } from 'vue-router';
import { findHistoryChatPageList, deleteChat } from '@/api/ai-robot/chat';
import { message } from 'ant-design-vue';

const router = useRouter();
const route = useRoute();
const themeStore = useAiThemeStore();
const { isDarkMode } = storeToRefs(themeStore);
const { toggleTheme } = themeStore;

const props = defineProps({ sidebarOpen: Boolean, isMobile: { type: Boolean, default: false } });
const emit = defineEmits(['toggle-sidebar']);

const widthClass = computed(() => props.isMobile ? 'w-[88vw] max-w-[320px]' : 'w-[280px]');
const toggleSidebar = () => emit('toggle-sidebar');
const historyChats = ref([]);
const selectedChatId = ref(null);
const current = ref(1);
const size = ref(20);

onMounted(() => {
  findHistoryChatPageList(current.value, size.value).then(res => {
    if(res.success) historyChats.value = res.data;
  });
  if(route.params.chatId) selectedChatId.value = route.params.chatId;
});

const jumpToIndexPage = () => {
  selectedChatId.value = null;
  router.push('/ai-robot');
};

const jumpToChatPage = (uuid) => {
  selectedChatId.value = uuid;
  router.push({ name: 'AiRobotChatPage', params: { chatId: uuid } });
};

const handleDeleteChat = async (event, uuid) => {
  event.stopPropagation();
  
  try {
    const res = await deleteChat(uuid);
    if (res.success) {
      message.success('删除成功');
      historyChats.value = historyChats.value.filter(item => item.uuid !== uuid);
      if (selectedChatId.value === uuid) {
        selectedChatId.value = null;
        router.push('/ai-robot');
      }
    } else {
      message.error(res.message || '删除失败');
    }
  } catch (error) {
    console.error(error);
    message.error('操作失败');
  }
};
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 4px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: transparent; border-radius: 4px; }
.custom-scrollbar:hover::-webkit-scrollbar-thumb { background: rgba(156, 163, 175, 0.5); }
</style>


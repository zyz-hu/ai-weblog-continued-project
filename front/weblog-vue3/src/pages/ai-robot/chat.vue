<template>
  <Layout>
    <template #main-content>
      <div class="h-full flex flex-col relative w-full max-w-full chat-content-layer bg-[#f7f8fa] dark:bg-[#050505] md:bg-transparent md:dark:bg-transparent" :style="{ backgroundColor: 'transparent', color: 'var(--app-text)' }">
        <!-- 极端去背景：移除遮罩背景，保留足够底部留白 -->
         <div class="flex-1 overflow-y-auto scroll-smooth custom-scrollbar w-full pt-2 md:pt-4 pb-56 bg-transparent relative z-20" ref="chatContainer">
           <div class="max-w-3xl mx-auto px-4 md:px-6 w-full flex flex-col gap-8">

            <template v-for="(chat, index) in chatList" :key="index">
              <div v-if="chat.role === 'user'" class="flex justify-end w-full pl-12">
                <div class="whitespace-pre-wrap bg-[#f3f4f6] dark:bg-[#2f2f2f] text-gray-800 dark:text-gray-100 px-5 py-3.5 rounded-[20px] rounded-tr-sm max-w-full text-[15px] leading-relaxed break-words transition-colors duration-300">
                  {{ chat.content }}
                </div>
              </div>

              <div v-else class="flex gap-4 w-full pr-2 group">
                <div class="flex-shrink-0 mt-1">
                  <div class="w-8 h-8 rounded-full flex items-center justify-center bg-white border border-gray-100 dark:bg-[#1e1f20] dark:border-gray-700 overflow-hidden transition-colors duration-300">
                    <SvgIcon :name="getModelIcon(chat.modelName)" customCss="w-5 h-5 transition-transform duration-300" />
                  </div>
                </div>

                <div class="flex-1 min-w-0 overflow-hidden">
                  <div class="text-xs text-gray-400 dark:text-gray-500 font-medium mb-1.5 ml-1 select-none transition-colors duration-300">
                    {{ getModelLabel(chat.modelName) }}
                  </div>

                  <div class="prose-content text-[15px] leading-7 transition-colors duration-300" style="position: relative; z-index: 30; background: transparent !important; color: var(--app-text) !important;">
                    <ReasoningBlock
                      v-if="chat.reasoning_content"
                      :content="chat.reasoning_content"
                      :loading="chat.loading && chat.is_reasoning"
                      :startTime="chat.startTime"
                    />

                    <LoadingDots v-if="chat.loading && !chat.content && !chat.reasoning_content" />
                    
                    <StreamMarkdownRender :content="chat.content" />
                  </div>

                  <div v-if="chat.error" class="mt-3 flex items-center gap-2 text-red-500 text-sm bg-red-50 dark:bg-red-900/20 px-3 py-2 rounded-lg w-fit transition-colors duration-300">
                    <span>⚠ {{ chat.errorMessage }}</span>
                    <button @click="retryMessage(index)" class="underline hover:text-red-600 ml-2 transition-colors duration-300">重试</button>
                  </div>
                </div>
              </div>
            </template>
          </div>
        </div>

        <div class="fixed bottom-0 right-0 left-0 md:left-[280px] z-50 pointer-events-none">
          <div class="px-0 pb-0 pt-0 min-h-[80px] pointer-events-auto relative z-20 bg-[var(--app-bg)]">
            <div class="chat-bottom-veil">
              <div class="chat-bottom-inner max-w-4xl mx-auto px-3 md:px-0">
                <ChatInputBox
                  v-model="message"
                  :loading="isGlobalLoading"
                  @sendMessage="sendMessage"
                  @stopGeneration="handleStopGeneration"
                />
                <div class="text-center text-[11px] text-gray-400 dark:text-gray-600 mt-3 select-none transition-colors duration-300">
                  AI 生成内容可能不准确，请仔细甄别
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </Layout>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, watch, computed } from 'vue';
import SvgIcon from '@/components/ai-robot/SvgIcon.vue';
import StreamMarkdownRender from '@/components/ai-robot/StreamMarkdownRender.vue';
import LoadingDots from '@/components/ai-robot/LoadingDots.vue';
import ReasoningBlock from '@/components/ai-robot/ReasoningBlock.vue';
import Layout from '@/layouts/ai-robot/Layout.vue';
import ChatInputBox from '@/components/ai-robot/ChatInputBox.vue';
import { useRoute } from 'vue-router';
import { useAiChatStore } from '@/stores/ai-robot/chat';
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { findChatMessagePageList } from '@/api/ai-robot/chat';
import { getToken } from '@/composables/cookie';

const chatStore = useAiChatStore();
const route = useRoute();

const message = ref(history.state?.firstMessage || '');
const chatContainer = ref(null);
const chatList = ref([]);
const chatId = ref(route.params.chatId || null);
const current = ref(1);
const size = ref(20); 
const hasMore = ref(true);
const isLoadingMore = ref(false);
let abortController = null; 
const modelLookup = computed(() => {
  const map = {};
  (chatStore.models || []).forEach((m) => { map[m.name] = m; });
  return map;
});

const getModelIcon = (modelName) => modelLookup.value[modelName]?.icon || 'qwen-logo';
const getModelLabel = (modelName) => modelName || 'AI';

// 便于排查：在浏览器控制台可查看 window.__AI_CHAT.chatList
if (typeof window !== 'undefined') {
  window.__AI_CHAT = { chatList };
}

const isGlobalLoading = computed(() => chatList.value.some(chat => chat.loading));

watch(() => route.params.chatId, (newChatId) => {
  if (newChatId) {
    chatId.value = newChatId;
    chatList.value = [];
    current.value = 1;
    if (abortController) {
      abortController.abort();
      abortController = null;
    }
    loadHistoryMessages();
  }
});

onMounted(() => {
  loadHistoryMessages();
  if (chatContainer.value) chatContainer.value.addEventListener('scroll', handleScroll);
  
  if (history.state?.firstMessage) {
    message.value = history.state.firstMessage;
    sendMessage({ selectedModel: chatStore.selectedModel, isNetworkSearch: chatStore.isNetworkSearchSelected });
    if (history.replaceState) {
      const newState = { ...history.state }; delete newState.firstMessage;
      history.replaceState(newState, document.title);
    }
  }
});

onBeforeUnmount(() => {
  if (abortController) {
    abortController.abort();
    abortController = null;
  }
  if (chatContainer.value) chatContainer.value.removeEventListener('scroll', handleScroll);
});

const loadHistoryMessages = async () => {
  try {
    isLoadingMore.value = true;
    const res = await findChatMessagePageList(current.value, size.value, chatId.value);
    console.log('历史消息响应', res);
    
    if (res.success) {
      const data = res.data || {};
      const listFromData = Array.isArray(data) ? data : (data.records || data.list || data.rows || []);
      const listFromRoot = Array.isArray(res.records) ? res.records : (res.list || res.rows || []);
      const rawList = Array.isArray(listFromData) ? listFromData : (Array.isArray(listFromRoot) ? listFromRoot : []);

      const total = res.total ?? data.total ?? rawList.length;
      const totalPages = res.pages ?? data.pages ?? (size.value > 0 ? Math.ceil(total / size.value) : 0);
      hasMore.value = totalPages > current.value;

      if (rawList.length > 0) {
        const formattedMessages = rawList.map(msg => {
          let safeRole = (msg.role || 'user').trim().toLowerCase();
          if (safeRole !== 'assistant' && safeRole !== 'system' && safeRole !== 'function') {
            safeRole = 'user';
          }

          return {
            ...msg,
            uuid: msg.id ? String(msg.id) : msg.uuid || `temp-${Date.now()}-${Math.random()}`,
            role: safeRole, 
            loading: false, 
            paused: false,
            error: false,
            errorMessage: msg.errorMessage || '',
            reasoning_content: msg.reasoningContent || msg.reasoning_content || '', 
            content: msg.content ?? msg.message ?? '',
            createTime: msg.createTime || msg.create_time || '',
            modelName: msg.modelName || msg.model_name || '',
          };
        });

        if (formattedMessages.length > 1) {
          const firstTime = new Date(formattedMessages[0].createTime || '').getTime();
          const lastTime = new Date(formattedMessages[formattedMessages.length - 1].createTime || '').getTime();
          if (firstTime && lastTime && firstTime > lastTime) {
            formattedMessages.reverse();
          }
        }

        chatList.value.unshift(...formattedMessages);
        console.log('已写入消息条数', chatList.value.length);
      }
      
      if (current.value === 1) scrollToBottom();
    }
  } catch (error) {
    console.error('加载失败:', error);
  } finally {
    isLoadingMore.value = false;
  }
};

const sendMessage = async (payload) => {
  const textToSend = payload?.text || message.value.trim();
  if (!textToSend) return;

  chatList.value.push({ role: 'user', content: textToSend });
  message.value = ''; 

  await nextTick();
  scrollToBottom();

  const assistantMessage = { 
    role: 'assistant', content: '', reasoning_content: '', 
    loading: true, is_reasoning: false, startTime: Date.now(),
    error: false, errorMessage: '', paused: false,
    modelName: (payload?.selectedModel || chatStore.selectedModel)?.name || '',
  };
  chatList.value.push(assistantMessage);
  const currentMsgRef = chatList.value[chatList.value.length - 1];

  try {
    const selectedModel = payload?.selectedModel || chatStore.selectedModel;
    abortController = new AbortController();
    const token = getToken();

    await fetchEventSource('/api/robot/chat/completion', {
      method: 'POST',
      signal: abortController.signal,
      openWhenHidden: true,
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      body: JSON.stringify({
        message: textToSend,
        chatId: chatId.value,
        modelName: selectedModel?.name || 'deepseek-chat',
        networkSearch: !!payload?.isNetworkSearch,
        temperature: selectedModel?.temperature ?? 0.7,
      }),
      async onopen(response) {
        if (!response.ok) throw new Error(`请求失败: ${response.status}`);
      },
      onmessage(msg) {
        if (!msg.data) return;
        try {
          const parseJson = JSON.parse(msg.data);
          const { v, type } = parseJson;
          if (type === 'reasoning') {
            currentMsgRef.reasoning_content += v;
            currentMsgRef.is_reasoning = true;
          } else if (type === 'ping') {
            // 心跳
          } else {
            currentMsgRef.content += v;
            currentMsgRef.is_reasoning = false;
          }
          scrollToBottom();
        } catch (e) { console.error("SSE Error:", e); }
      },
      onclose() { throw new Error('StreamComplete'); },
      onerror(err) {
        if (err.message === 'StreamComplete' || err.name === 'AbortError') throw err;
        currentMsgRef.error = true;
        currentMsgRef.errorMessage = '连接中断';
        throw err; 
      },
    });
  } catch (error) {
    currentMsgRef.loading = false;
    if (error.name !== 'AbortError' && error.message !== 'StreamComplete') {
      currentMsgRef.error = true;
      currentMsgRef.errorMessage = error.message || '请求失败';
    }
  } finally {
     if (abortController) abortController = null;
  }
};

const handleStopGeneration = () => {
  if (abortController) {
    const lastAssistant = [...chatList.value].reverse().find(msg => msg.role === 'assistant' && msg.loading);
    if (lastAssistant) {
      lastAssistant.loading = false;
      lastAssistant.paused = true;
    }
    abortController.abort();
  }
};

const retryMessage = (index) => {
  const userMsgIndex = index - 1;
  if (userMsgIndex >= 0) {
    const userMsg = chatList.value[userMsgIndex];
    chatList.value.splice(userMsgIndex, 2); 
    sendMessage({ text: userMsg.content });
  }
};

const scrollToBottom = async () => {
  await nextTick();
  if (chatContainer.value) {
    chatContainer.value.scrollTo({ top: chatContainer.value.scrollHeight, behavior: 'smooth' });
  }
};

const handleScroll = () => {
  if (chatContainer.value && chatContainer.value.scrollTop < 50 && hasMore.value && !isLoadingMore.value) {
    const oldScrollHeight = chatContainer.value.scrollHeight;
    current.value += 1;
    loadHistoryMessages().then(() => {
      nextTick(() => {
        if (chatContainer.value) {
            chatContainer.value.scrollTop = chatContainer.value.scrollHeight - oldScrollHeight;
        }
      });
    });
  }
};
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 0px; background: transparent; }
.prose-content { color: var(--app-text); }
.prose-content :where(p, li, code, pre, blockquote, strong, em) { color: inherit; }
.chat-content-layer { position: relative; z-index: 40; pointer-events: auto; }
.chat-bottom-veil {
  position: relative;
  width: 100%;
  border-radius: 14px 14px 0 0;
  padding: 6px 12px 12px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 -6px 18px rgba(15, 23, 42, 0.12);
  transition: background-color 0.3s ease, box-shadow 0.3s ease, border-color 0.3s ease;
  overflow: visible;
  backdrop-filter: blur(6px);
}
.chat-bottom-inner {
  position: relative;
  padding: 2px 0;
}
.dark .chat-bottom-veil {
  background: rgba(10, 10, 10, 0.96);
  border-color: rgba(255, 255, 255, 0.08);
  box-shadow: 0 -6px 18px rgba(0, 0, 0, 0.55);
}
</style>





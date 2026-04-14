<template>
  <Layout>
    <template #main-content>
      <div class="flex-1 relative w-full min-h-screen">
        <div class="hidden md:flex items-center justify-center flex-1 relative min-h-[calc(100vh-80px)] px-4">
          <div class="max-w-3xl w-full">
            <div class="text-center mb-10">
              <div class="flex items-center justify-center mb-3">
                <SvgIcon name="ai-robot-logo" customCss="w-10 h-10 text-gray-700 dark:text-indigo-200 mr-3" />
                <h2 class="text-2xl text-gray-800 dark:text-white">我是元智AI机器人，很高兴见到你</h2>
              </div>
              <p class="text-gray-500 dark:text-slate-300">我可以帮你写代码、写作各类创意内容，请把你的任务交给我吧~</p>
              <div class="flex items-center justify-center gap-3 mt-4">
                <el-button type="success" plain @click="openKnowledgeDialog">知识库智能客服</el-button>
              </div>
            </div>

            <div class="max-w-3xl mx-auto">
              <ChatInputBox
                v-model="userMessage"
                @sendMessage="sendMessage"
              />
            </div>
          </div>
        </div>

        <div class="md:hidden flex flex-col h-full bg-[var(--app-bg)]">
          <div class="flex-1 px-5 pt-6 pb-32">
            <div class="flex items-center gap-3 mb-6">
              <div class="w-12 h-12 rounded-full bg-[#eef2ff] dark:bg-[#1f2937] flex items-center justify-center shadow-sm">
                <SvgIcon name="ai-robot-logo" customCss="w-7 h-7 text-indigo-600 dark:text-indigo-200" />
              </div>
              <div class="flex-1">
                <div class="text-sm text-gray-500 dark:text-slate-300">你好，我是</div>
                <div class="text-lg font-semibold text-gray-900 dark:text-white">元智AI</div>
              </div>
              <el-button size="small" @click="openKnowledgeDialog">知识库</el-button>
            </div>

            <div class="text-center mt-12 px-2">
              <SvgIcon name="ai-robot-logo" customCss="w-20 h-20 text-indigo-500 mx-auto" />
              <div class="mt-6 text-xl font-semibold text-gray-900 dark:text-white">嗨！我是元智机器人</div>
              <p class="mt-3 text-gray-500 dark:text-slate-300 leading-relaxed">我可以帮你搜索、答疑、写作，把你的任务交给我吧～</p>
            </div>
          </div>

          <div class="fixed left-0 right-0 bottom-0 z-40 px-4 pb-[max(16px,_env(safe-area-inset-bottom))] pt-2 bg-[var(--app-bg)] border-t border-gray-200 dark:border-gray-800 shadow-[0_-8px_30px_rgba(0,0,0,0.08)] dark:shadow-[0_-8px_30px_rgba(0,0,0,0.35)]">
            <ChatInputBox
              v-model="userMessage"
              :containerClass="'shadow-lg border border-gray-200 dark:border-gray-800 bg-white dark:bg-[#111] rounded-3xl'"
              @sendMessage="sendMessage"
            />
          </div>
        </div>
      </div>

      <el-dialog
        v-model="knowledgeDialogVisible"
        title="私有知识库智能客服"
        width="620px"
        destroy-on-close
        :fullscreen="isMobile"
        append-to-body
        :class="isMobile ? 'knowledge-dialog-mobile' : ''"
      >
        <div class="space-y-4 knowledge-dialog-body">
          <el-input
            v-model="knowledgeQuestion"
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 6 }"
            placeholder="请输入问题，客服将严格基于知识库回答"
          />
          <div class="flex gap-3 items-center flex-wrap md:flex-nowrap">
            <el-select
              v-model="selectedMdIds"
              multiple
              clearable
              filterable
              class="flex-1 min-w-[200px]"
              placeholder="可选：限定使用的知识文件（默认全库）"
            >
              <el-option
                v-for="item in knowledgeMdOptions"
                :key="item.id"
                :label="item.originalFileName"
                :value="item.id"
              />
            </el-select>
            <el-input-number
              v-model="knowledgeTopK"
              :min="1"
              :max="10"
              label="召回片段数"
              class="w-full md:w-auto"
            />
          </div>
          <el-alert v-if="knowledgeError" :title="knowledgeError" type="error" show-icon />
          <div class="min-h-[160px] rounded-lg border border-dashed border-gray-200 p-3 bg-gray-50">
            <div class="text-xs text-gray-400 mb-2 flex items-center gap-2">
              <el-icon><ChatLineRound /></el-icon>
              <span>回复</span>
              <span v-if="knowledgeLoading" class="text-emerald-500">（生成中...）</span>
            </div>
            <div class="whitespace-pre-wrap text-sm leading-6 text-gray-800">
              {{ knowledgeAnswer || '等待提问...' }}
            </div>
          </div>
        </div>
        <template #footer>
          <div class="flex justify-between items-center flex-wrap gap-3 knowledge-dialog-footer">
            <span class="text-gray-500 text-xs">模型：{{ selectedModelName }}</span>
            <div class="flex gap-2 w-full md:w-auto">
              <el-button class="flex-1 md:flex-none" @click="stopKnowledgeChat" :disabled="!knowledgeLoading">停止</el-button>
              <el-button class="flex-1 md:flex-none" type="primary" :loading="knowledgeLoading" @click="sendKnowledgeChat">发送</el-button>
            </div>
          </div>
        </template>
      </el-dialog>
    </template>
  </Layout>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
import Layout from '@/layouts/ai-robot/Layout.vue';
import SvgIcon from '@/components/ai-robot/SvgIcon.vue';
import ChatInputBox from '@/components/ai-robot/ChatInputBox.vue';
import { newChat } from '@/api/ai-robot/chat';
import { useRouter } from 'vue-router';
import { fetchKnowledgeFiles } from '@/api/ai-robot/knowledge';
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { useAiChatStore } from '@/stores/ai-robot/chat';
import { getToken } from '@/composables/cookie';
import { showMessage } from '@/composables/util';
import { ChatLineRound } from '@element-plus/icons-vue';

const router = useRouter();
const userMessage = ref('');
const knowledgeDialogVisible = ref(false);
const knowledgeQuestion = ref('');
const knowledgeAnswer = ref('');
const knowledgeLoading = ref(false);
const knowledgeError = ref('');
const knowledgeMdOptions = ref([]);
const knowledgeTopK = ref(4);
const selectedMdIds = ref([]);
let knowledgeAbortController = null;
const aiChatStore = useAiChatStore();
const isMobile = ref(false);

const selectedModelName = computed(() => aiChatStore.selectedModel?.name || 'deepseek-chat');
const selectedModelTemperature = computed(() => aiChatStore.selectedModel?.temperature ?? 0.7);

const sendMessage = (payload) => {
  if (!userMessage.value.trim()) return;

  const userMessageTemp = userMessage.value.trim();
  newChat(userMessageTemp).then(res => {
    if (res.success) {
        router.push({
          name: 'AiRobotChatPage',
          params: {
            chatId: res.data.uuid,
          },
          state: {
            firstMessage: userMessageTemp,
          },
        });
    }
  });
};

const openKnowledgeDialog = () => {
  knowledgeDialogVisible.value = true;
  knowledgeQuestion.value = userMessage.value || '';
  knowledgeAnswer.value = '';
  knowledgeError.value = '';
  loadKnowledgeOptions();
};

const loadKnowledgeOptions = () => {
  fetchKnowledgeFiles({ current: 1, size: 50, status: 2 }).then((res) => {
    if (res.success) {
      knowledgeMdOptions.value = res.data || [];
    }
  });
};

const sendKnowledgeChat = async () => {
  if (!knowledgeQuestion.value.trim()) {
    showMessage('请先填写问题', 'warning');
    return;
  }

  if (knowledgeAbortController) {
    knowledgeAbortController.abort();
    knowledgeAbortController = null;
  }

  knowledgeAnswer.value = '';
  knowledgeError.value = '';
  knowledgeLoading.value = true;
  knowledgeAbortController = new AbortController();
  const token = getToken();

  try {
    await fetchEventSource('/api/robot/customer-service/chat', {
      method: 'POST',
      signal: knowledgeAbortController.signal,
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      body: JSON.stringify({
        message: knowledgeQuestion.value.trim(),
        modelName: selectedModelName.value,
        temperature: selectedModelTemperature.value,
        topK: knowledgeTopK.value,
        mdStorageIds: selectedMdIds.value?.length ? selectedMdIds.value : null,
      }),
      onmessage(ev) {
        if (!ev?.data) return;
        try {
          const data = JSON.parse(ev.data);
          if (data.type === 'ping') return;
          knowledgeAnswer.value += data.v || '';
        } catch (err) {
          console.error('parse knowledge chunk error', err);
        }
      },
      onclose() {
        knowledgeLoading.value = false;
        knowledgeAbortController = null;
      },
      onerror(err) {
        if (err?.name === 'AbortError') return;
        knowledgeError.value = '连接中断，请重试';
        knowledgeLoading.value = false;
        knowledgeAbortController = null;
        throw err;
      },
    });
  } catch (error) {
    if (error?.name === 'AbortError') return;
    knowledgeError.value = error?.message || '请求失败';
  } finally {
    knowledgeLoading.value = false;
    knowledgeAbortController = null;
  }
};

const stopKnowledgeChat = () => {
  if (knowledgeAbortController) {
    knowledgeAbortController.abort();
  }
};

const updateViewport = () => {
  isMobile.value = window.innerWidth < 768;
};

onMounted(() => {
  updateViewport();
  window.addEventListener('resize', updateViewport);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateViewport);
});
</script>

<style scoped>
.knowledge-dialog-body {
  max-height: calc(80vh - 120px);
  overflow-y: auto;
}

.knowledge-dialog-mobile :deep(.el-dialog) {
  margin: 0 !important;
  width: 100% !important;
  max-width: 100% !important;
}

.knowledge-dialog-mobile :deep(.el-dialog__body) {
  padding: 12px 12px 0 12px;
}

.knowledge-dialog-mobile :deep(.el-dialog__header) {
  padding: 12px 16px;
}

.knowledge-dialog-mobile :deep(.el-dialog__footer) {
  padding: 12px 16px 16px 16px;
}

@media (max-width: 767px) {
  .knowledge-dialog-body {
    max-height: calc(100vh - 170px);
  }

  .knowledge-dialog-footer {
    width: 100%;
  }
}
</style>

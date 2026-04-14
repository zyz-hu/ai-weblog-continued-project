<template>
  <div :class="containerClass">
    <div
      class="bg-[#f7f8fa] dark:bg-[#121212] rounded-[26px] p-2 md:p-3 relative shadow-[0_12px_32px_rgba(0,0,0,0.25)] border border-gray-200 dark:border-[#2a2a2a] focus-within:border-gray-300 dark:focus-within:border-gray-500 transition-all duration-300"
    >
      <textarea
        placeholder="写点什么给 AI 机器人吧..."
        class="bg-transparent border-none outline-none w-full text-[15px] resize-none px-3 py-1 text-gray-900 dark:text-gray-100 placeholder-gray-500 dark:placeholder-gray-500 max-h-[200px] overflow-y-auto custom-scrollbar"
        rows="1"
        v-model="userMessage"
        @input="autoResize"
        @keydown.enter="handleEnter"
        :disabled="loading"
        ref="textareaRef"
      ></textarea>

      <div class="flex justify-between items-center mt-2 px-1">
        <div class="flex items-center gap-2">
          <div
            class="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-white dark:bg-[#2d2e30] hover:bg-gray-100 dark:hover:bg-[#363739] cursor-pointer transition-colors border border-gray-200 dark:border-gray-700/50"
            @click="!loading && toggleModelDropdown($event)"
            ref="selectRef"
          >
            <SvgIcon :name="currSelectedModel.icon" customCss="w-4 h-4 text-indigo-500" />
            <span class="text-xs font-medium text-gray-700 dark:text-gray-300 select-none">{{ currSelectedModel.name }}</span>
          </div>

          <div
            class="p-1.5 rounded-full cursor-pointer transition-colors border"
            :class="isNetworkSearchSelected
              ? 'bg-blue-50 dark:bg-blue-900/30 border-blue-200 dark:border-blue-800 text-blue-600 dark:text-blue-400'
              : 'bg-white dark:bg-[#2d2e30] border-gray-200 dark:border-gray-700/50 text-gray-500 hover:text-gray-700 dark:hover:text-gray-300'"
            @click="!loading && toggleNetworkSearch()"
          >
            <SvgIcon name="network" customCss="w-4 h-4" />
          </div>
        </div>

        <button
          class="flex items-center justify-center w-8 h-8 rounded-full transition-all duration-200 shadow-sm"
          :class="[
            !userMessage.trim() && !loading
              ? 'bg-gray-300 dark:bg-[#363739] cursor-not-allowed text-gray-500'
              : 'bg-indigo-600 hover:bg-indigo-700 text-white cursor-pointer'
          ]"
          @click="handleSendMessageOrStop"
          :disabled="!userMessage.trim() && !loading"
        >
          <div v-if="loading" class="w-2.5 h-2.5 bg-white rounded-sm animate-pulse"></div>
          <SvgIcon v-else name="up-arrow" customCss="w-4 h-4" />
        </button>
      </div>

      <div
        v-if="isModelDropdownOpen"
        ref="dropdownRef"
        class="absolute bottom-14 left-0 w-64 bg-white dark:bg-[#252628] rounded-xl shadow-2xl border border-gray-100 dark:border-gray-700 overflow-hidden z-50 py-1"
      >
        <div
          v-for="model in models"
          :key="model.id"
          class="px-4 py-3 hover:bg-gray-50 dark:hover:bg-[#2f3032] cursor-pointer flex items-center justify-between group"
          @click="selectModel(model)"
        >
          <div class="flex items-center gap-3">
            <div class="w-8 h-8 rounded-lg bg-gray-100 dark:bg-[#363739] flex items-center justify-center">
              <SvgIcon :name="model.icon" customCss="w-5 h-5 text-gray-700 dark:text-gray-300" />
            </div>
            <div>
              <div class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ model.name }}</div>
              <div class="text-xs text-gray-500 dark:text-gray-400 mt-0.5">{{ model.description }}</div>
            </div>
          </div>
          <SvgIcon
            v-if="model.selected"
            name="check"
            customCss="w-4 h-4 text-indigo-600 dark:text-indigo-400"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick, watch } from 'vue';
import SvgIcon from '@/components/ai-robot/SvgIcon.vue';
import { useAiChatStore } from '@/stores/ai-robot/chat';

const chatStore = useAiChatStore();
const props = defineProps({
  modelValue: { type: String, required: true },
  containerClass: { type: String, default: '' },
  loading: { type: Boolean, default: false },
});
const emit = defineEmits(['update:modelValue', 'sendMessage', 'stopGeneration']);

const userMessage = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
});

const models = computed(() => chatStore.models);
const currSelectedModel = computed(() => chatStore.selectedModel);
const isNetworkSearchSelected = computed(() => chatStore.isNetworkSearchSelected);

const isModelDropdownOpen = ref(false);
const selectRef = ref(null);
const dropdownRef = ref(null);
const textareaRef = ref(null);

const toggleModelDropdown = (e) => {
  e.stopPropagation();
  isModelDropdownOpen.value = !isModelDropdownOpen.value;
};

const handleClickOutside = (e) => {
  if (isModelDropdownOpen.value &&
      selectRef.value && !selectRef.value.contains(e.target) &&
      dropdownRef.value && !dropdownRef.value.contains(e.target)) {
    isModelDropdownOpen.value = false;
  }
};

const selectModel = (model) => {
  chatStore.updateSelectedModel(model);
  isModelDropdownOpen.value = false;
};

const toggleNetworkSearch = () => {
  chatStore.updateNetworkSearchStatus(!chatStore.isNetworkSearchSelected);
};

const autoResize = () => {
  const el = textareaRef.value;
  if (!el) return;
  el.style.height = 'auto';
  el.style.height = Math.min(el.scrollHeight, 200) + 'px';
};

const handleSendMessageOrStop = () => {
  if (props.loading) { emit('stopGeneration'); return; }
  if (!userMessage.value.trim()) return;
  emit('sendMessage', {
    text: userMessage.value.trim(),
    selectedModel: chatStore.selectedModel,
    isNetworkSearch: chatStore.isNetworkSearchSelected,
  });
  userMessage.value = '';
  nextTick(() => { if (textareaRef.value) textareaRef.value.style.height = 'auto'; });
};

const handleEnter = (e) => {
  if (e.isComposing) return;
  if (e.key === 'Enter' && !e.shiftKey && !props.loading) {
    e.preventDefault();
    handleSendMessageOrStop();
  }
};

watch(userMessage, () => nextTick(autoResize));
onMounted(() => { document.addEventListener('click', handleClickOutside); autoResize(); });
onUnmounted(() => document.removeEventListener('click', handleClickOutside));
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 4px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 4px; }
.dark .custom-scrollbar::-webkit-scrollbar-thumb { background: #4b5563; }
</style>

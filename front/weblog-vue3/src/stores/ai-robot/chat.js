import { defineStore } from 'pinia';
import { ref, watch } from 'vue';

const createDefaultModels = () => ([
  { id: 1, name: 'deepseek-chat', icon: 'deepseek-logo', description: '速度更快(deepseek v3)', selected: true, temperature: 0.7 },
  { id: 2, name: 'deepseek-reasoner', icon: 'deepseek-logo', description: '综合能力更强(deepseek r1)', selected: false, temperature: 0.7 },
  { id: 3, name: 'qwen-plus', icon: 'qwen-logo', description: '通义千问 Plus (均衡)', selected: false, temperature: 0.7 },
  { id: 4, name: 'qwen-max', icon: 'qwen-logo', description: '通义千问 Max (最强)', selected: false, temperature: 0.7 },
  { id: 5, name: 'qwen-turbo', icon: 'qwen-logo', description: '通义千问 Turbo (极速)', selected: false, temperature: 0.7 },
  // 火山方舟 DeepSeek v3.2（后端模型名：ark-deepseek-v3-2）
  { id: 6, name: 'ark-deepseek-v3-2', icon: 'deepseek-logo', description: '火山方舟 DeepSeek v3.2', selected: false, temperature: 0.7 },
]);

const defaultModelNameSignature = createDefaultModels().map((model) => model.name).join('|');

// 创建 store
export const useAiChatStore = defineStore('ai-chat', () => {
  const models = ref(createDefaultModels());
  const selectedModel = ref(models.value[0]);
  const isNetworkSearchSelected = ref(false);

  function updateSelectedModel(model) {
    models.value.forEach((m) => {
      m.selected = m.id === model.id;
    });
    selectedModel.value = model;
  }

  function updateNetworkSearchStatus(status) {
    isNetworkSearchSelected.value = status;
  }

  watch(models, (newModels) => {
    const nameSignature = newModels.map((model) => model.name).join('|');
    if (nameSignature !== defaultModelNameSignature) {
      models.value = createDefaultModels();
    }
  }, { deep: true, immediate: true });

  watch(() => selectedModel.value?.name, (modelName) => {
    const matched = models.value.find((model) => model.name === modelName);
    if (matched) {
      if (!matched.selected) {
        updateSelectedModel(matched);
      }
    } else if (models.value.length > 0) {
      updateSelectedModel(models.value[0]);
    }
  }, { immediate: true });

  return { models, selectedModel, isNetworkSearchSelected, updateSelectedModel, updateNetworkSearchStatus };
}, {
  persist: true,
});

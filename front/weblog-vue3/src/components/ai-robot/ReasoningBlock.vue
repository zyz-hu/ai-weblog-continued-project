<template>
  <div class="reasoning-block my-2">
    <div
      class="flex items-center cursor-pointer select-none text-gray-500 hover:text-gray-700 transition-colors py-2"
      @click="toggleExpand"
    >
      <SvgIcon
        name="down-arrow"
        customCss="w-4 h-4 mr-2 transition-transform duration-300"
        :class="isExpanded ? 'rotate-180' : ''"
      />
      <span class="text-sm font-medium">
        {{ loading ? '思考链路生成中...' : '已生成推理链路' }}
      </span>
      <span v-if="!loading && duration" class="text-xs ml-2 bg-gray-100 px-2 py-0.5 rounded text-gray-400">
        耗时 {{ (duration / 1000).toFixed(1) }}s
      </span>
    </div>

    <div v-show="isExpanded" class="pl-4 border-l-2 border-gray-200 ml-2 mb-2">
      <div class="text-gray-500 text-sm leading-relaxed whitespace-pre-wrap">
        {{ content }}
        <span v-if="loading" class="inline-block w-1.5 h-4 ml-1 align-middle bg-gray-400 animate-pulse"></span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import SvgIcon from '@/components/ai-robot/SvgIcon.vue';

const props = defineProps({
  content: String,
  loading: Boolean,
  startTime: Number,
});

const isExpanded = ref(true);
const duration = ref(0);

watch(() => props.loading, (newVal, oldVal) => {
  if (oldVal === true && newVal === false && props.startTime) {
    duration.value = Date.now() - props.startTime;
  }
});

const toggleExpand = () => { isExpanded.value = !isExpanded.value; };
</script>

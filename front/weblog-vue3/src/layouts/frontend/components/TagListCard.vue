<template>
  <div v-if="tags.length > 0" class="glass-card p-5 mb-4">
    <div class="flex items-center justify-between mb-3">
      <p class="section-title">标签</p>
      <span class="text-xs front-text-weaker">共 {{ tags.length }} 个</span>
    </div>
    <div class="flex flex-wrap gap-2">
      <span v-for="(tag, index) in tags" :key="index" @click="goTagArticleListPage(tag.id, tag.name)"
        class="cursor-pointer inline-flex items-center gap-1 px-3 py-1.5 rounded-full bg-slate-900 text-white text-xs font-medium shadow hover:-translate-y-0.5 transition">
        <span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>
        {{ tag.name }}
      </span>
    </div>
  </div>
</template>

<script setup>
import { getTagList } from '@/api/frontend/tag';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const tags = ref([]);
getTagList().then((res) => {
  if (res.success) {
    tags.value = res.data;
  }
});

const goTagArticleListPage = (id, name) => {
  router.push({ path: '/tag/article/list', query: { id, name } });
};
</script>

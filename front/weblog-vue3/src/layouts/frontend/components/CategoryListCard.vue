<template>
  <div v-if="categories.length > 0" class="glass-card p-5 mb-4">
    <div class="flex items-center justify-between mb-3">
      <p class="section-title">分类</p>
      <span class="text-xs front-text-weaker">共 {{ categories.length }} 项</span>
    </div>
    <div class="flex flex-wrap gap-2">
      <button v-for="(category, index) in categories" :key="index"
        @click="goCategoryArticleListPage(category.id, category.name)"
        class="px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 text-sm text-slate-700 dark:text-slate-200 bg-white/70 dark:bg-slate-800/70 hover:border-indigo-500 hover:text-indigo-600 transition">
        {{ category.name }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { getCategoryList } from '@/api/frontend/category';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const goCategoryArticleListPage = (id, name) => {
  router.push({ path: '/category/article/list', query: { id, name } });
};

const categories = ref([]);
getCategoryList().then((res) => {
  if (res.success) {
    categories.value = res.data;
  }
});
</script>

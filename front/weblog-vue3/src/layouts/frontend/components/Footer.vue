<template>
<footer class="mt-16 border-t border-slate-200/70 dark:border-slate-800 bg-white/80 dark:bg-slate-900/70 backdrop-blur">
    <div class="page-shell py-10 flex flex-col gap-3">
      <div class="flex flex-wrap items-center gap-3">
        <p class="text-lg font-semibold text-slate-900 dark:text-slate-100">{{ blogSettingsStore.blogSettings.name }}</p>
        <span class="text-xs px-3 py-1 rounded-full bg-slate-900 text-white">Personal weblog</span>
      </div>
      <p class="text-sm front-text-soft leading-relaxed max-w-3xl">
        不做商业站，只写给自己：把项目、思考和生活碎片放进这个轻松的角落。
      </p>
      <div class="flex flex-wrap items-center gap-3 text-sm front-text-soft">
        <button v-for="(item, index) in navItems" :key="index" @click="router.push(item.path)"
          class="px-3 py-1 rounded-full border border-slate-200 dark:border-slate-700 text-slate-600 dark:text-slate-200 hover:border-indigo-500 hover:text-indigo-600 transition">
          {{ item.name }}
        </button>
        <span class="front-text-weaker">·</span>
        <a v-if="blogSettingsStore.blogSettings.githubHomepage" :href="blogSettingsStore.blogSettings.githubHomepage"
          target="_blank" class="hover:text-indigo-600">GitHub</a>
        <a v-if="blogSettingsStore.blogSettings.giteeHomepage" :href="blogSettingsStore.blogSettings.giteeHomepage"
          target="_blank" class="hover:text-indigo-600">Gitee</a>
        <a v-if="blogSettingsStore.blogSettings.zhihuHomepage" :href="blogSettingsStore.blogSettings.zhihuHomepage"
          target="_blank" class="hover:text-indigo-600">知乎</a>
        <a v-if="blogSettingsStore.blogSettings.csdnHomepage" :href="blogSettingsStore.blogSettings.csdnHomepage"
          target="_blank" class="hover:text-indigo-600">CSDN</a>
      </div>
      <div class="pt-3 text-sm front-text-soft flex flex-wrap gap-3">
        <span>© {{ year }} {{ blogSettingsStore.blogSettings.author || blogSettingsStore.blogSettings.name }}</span>
        <span v-if="blogSettingsStore.blogSettings.websiteRecord">备案号：{{ blogSettingsStore.blogSettings.websiteRecord }}</span>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { useBlogSettingsStore } from '@/stores/blogsettings';

const router = useRouter();
const blogSettingsStore = useBlogSettingsStore();
const year = new Date().getFullYear();

const navItems = [
  { name: '首页', path: '/' },
  { name: '分类', path: '/category/list' },
  { name: '标签', path: '/tag/list' },
  { name: '归档', path: '/archive/list' },
];
</script>

<template>
  <div class="glass-card p-6 mb-4">
    <div class="flex items-start gap-4">
      <div class="relative">
        <img class="w-16 h-16 rounded-2xl object-cover shadow-md border border-white/60"
          :src="blogSettingsStore.blogSettings.avatar" alt="avatar" />
        <span class="absolute -bottom-2 left-2 px-2 py-0.5 rounded-full bg-emerald-500 text-white text-xs shadow">
          Online
        </span>
      </div>
      <div class="flex-1">
        <p class="text-sm uppercase tracking-[0.25em] front-text-weaker mb-1">作者</p>
        <h5 class="text-xl font-semibold text-slate-900 dark:text-white">{{ blogSettingsStore.blogSettings.author }}</h5>
        <p class="text-sm front-text-soft leading-relaxed mt-1">{{ blogSettingsStore.blogSettings.introduction }}</p>
      </div>
    </div>

    <div class="mt-4 grid grid-cols-3 gap-3 text-sm">
      <div class="p-3 rounded-xl bg-slate-50 dark:bg-slate-800/80 border border-slate-100 dark:border-slate-700">
        <p class="text-[11px] uppercase tracking-[0.15em] front-text-weaker">个人站点</p>
        <p class="text-slate-800 dark:text-slate-100 font-semibold mt-1 truncate">
          {{ blogSettingsStore.blogSettings.name || 'My weblog' }}
        </p>
      </div>
      <div class="p-3 rounded-xl bg-slate-50 dark:bg-slate-800/80 border border-slate-100 dark:border-slate-700">
        <p class="text-[11px] uppercase tracking-[0.15em] front-text-weaker">来自</p>
        <p class="text-slate-800 dark:text-slate-100 font-semibold mt-1 truncate">代码与生活</p>
      </div>
      <div class="p-3 rounded-xl bg-slate-50 dark:bg-slate-800/80 border border-slate-100 dark:border-slate-700">
        <p class="text-[11px] uppercase tracking-[0.15em] front-text-weaker">状态</p>
        <p class="text-slate-800 dark:text-slate-100 font-semibold mt-1 truncate">持续更新</p>
      </div>
    </div>

    <div class="mt-5 flex flex-wrap gap-3">
      <button v-if="blogSettingsStore.blogSettings.githubHomepage" @click="jump(blogSettingsStore.blogSettings.githubHomepage)"
        data-tooltip-target="github-tooltip-bottom" data-tooltip-placement="bottom"
        class="inline-flex items-center gap-2 px-3 py-2 rounded-full border border-slate-200 dark:border-slate-700 text-sm text-slate-700 dark:text-slate-200 hover:border-indigo-500 hover:text-indigo-600 transition">
        <span class="w-2 h-2 rounded-full bg-indigo-500"></span> GitHub
      </button>
      <button v-if="blogSettingsStore.blogSettings.giteeHomepage" @click="jump(blogSettingsStore.blogSettings.giteeHomepage)"
        data-tooltip-target="gitee-tooltip-bottom" data-tooltip-placement="bottom"
        class="inline-flex items-center gap-2 px-3 py-2 rounded-full border border-slate-200 dark:border-slate-700 text-sm text-slate-700 dark:text-slate-200 hover:border-indigo-500 hover:text-indigo-600 transition">
        <span class="w-2 h-2 rounded-full bg-emerald-500"></span> Gitee
      </button>
      <button v-if="blogSettingsStore.blogSettings.zhihuHomepage" @click="jump(blogSettingsStore.blogSettings.zhihuHomepage)"
        data-tooltip-target="zhihu-tooltip-bottom" data-tooltip-placement="bottom"
        class="inline-flex items-center gap-2 px-3 py-2 rounded-full border border-slate-200 dark:border-slate-700 text-sm text-slate-700 dark:text-slate-200 hover:border-indigo-500 hover:text-indigo-600 transition">
        <span class="w-2 h-2 rounded-full bg-sky-500"></span> 知乎
      </button>
      <button v-if="blogSettingsStore.blogSettings.csdnHomepage" @click="jump(blogSettingsStore.blogSettings.csdnHomepage)"
        data-tooltip-target="csdn-tooltip-bottom" data-tooltip-placement="bottom"
        class="inline-flex items-center gap-2 px-3 py-2 rounded-full border border-slate-200 dark:border-slate-700 text-sm text-slate-700 dark:text-slate-200 hover:border-indigo-500 hover:text-indigo-600 transition">
        <span class="w-2 h-2 rounded-full bg-amber-500"></span> CSDN
      </button>
    </div>
  </div>
</template>

<script setup>
import { useBlogSettingsStore } from '@/stores/blogsettings';
import { initTooltips } from 'flowbite';
import { onMounted } from 'vue';

onMounted(() => {
  initTooltips();
});

const blogSettingsStore = useBlogSettingsStore();

const jump = (url) => {
  if (!url) return;
  window.open(url, '_blank');
};
</script>

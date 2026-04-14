<template>
  <div class="home-aura">
    <div class="home-noise"></div>

    <Header />

    <section class="relative overflow-hidden">
      <div class="page-shell relative z-10 pt-14 pb-16">
        <div class="grid lg:grid-cols-[1.25fr_1fr] gap-10 xl:gap-14 items-center">
          <div class="space-y-6 text-slate-900 dark:text-white">
            <div class="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-white/10 border border-white/20 backdrop-blur">
              <span class="w-2 h-2 rounded-full bg-emerald-300 animate-pulse"></span>
              <span class="text-xs tracking-[0.24em] uppercase">Personal weblog</span>
            </div>
            <h1 class="text-4xl sm:text-5xl xl:text-6xl font-black leading-tight max-w-3xl">
              把代码、想法和日常灵感串成一条更轻松的时间线
            </h1>
            <p class="text-lg text-slate-700 dark:text-white/80 leading-relaxed max-w-2xl">
              不是商业站点，而是自己的网络花园。更开阔的留白、更舒展的节奏，让阅读像翻开一本随笔本。
            </p>
            <div class="flex flex-wrap gap-3">
              <button @click="scrollToArticles"
                class="inline-flex items-center gap-2 px-7 py-3 rounded-full bg-white text-slate-900 font-semibold shadow-lg ring-1 ring-white/40 hover:-translate-y-0.5 transition">
                开始阅读
              </button>
              <button @click="router.push('/ai-robot')"
                class="inline-flex items-center gap-2 px-7 py-3 rounded-full border border-white/30 text-white font-semibold hover:bg-white/10 transition">
                探索 AI 实验室
              </button>
            </div>
            <div class="flex flex-wrap gap-4 text-slate-700 dark:text-white/70 pt-1">
              <div class="flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
                持续更新
              </div>
              <div class="flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-sky-300"></span>
                保留一点私人的温度
              </div>
            </div>
            <div class="grid grid-cols-3 max-w-xl gap-4 pt-2">
              <div class="stat-chip">
                <p class="text-xs text-slate-700 dark:text-white/70">文章</p>
                <p class="text-lg font-semibold text-slate-900 dark:text-white">精选更新</p>
              </div>
              <div class="stat-chip">
                <p class="text-xs text-slate-700 dark:text-white/70">标签</p>
                <p class="text-lg font-semibold text-slate-900 dark:text-white">话题地图</p>
              </div>
              <div class="stat-chip">
                <p class="text-xs text-slate-700 dark:text-white/70">AI</p>
                <p class="text-lg font-semibold text-slate-900 dark:text-white">实验助手</p>
              </div>
            </div>
          </div>

          <div class="flex flex-col gap-4">
            <div class="hero-panel">
              <div class="flex items-center justify-between">
                <p class="text-sm font-semibold text-white/80">AI 灵感实验室</p>
                <span class="text-xs px-3 py-1 rounded-full bg-white/20 text-white">Live</span>
              </div>
              <p class="text-2xl font-bold text-white pt-3">写作、灵感、检索，一键直达</p>
              <p class="text-sm text-white/70 leading-relaxed">把知识库、提纲和聊天助手收拢在一个工作台里，边逛边记。</p>
              <div class="flex items-center gap-2 pt-4">
                <button @click="router.push('/ai-robot')"
                  class="inline-flex items-center gap-2 px-5 py-2.5 rounded-full bg-white text-slate-900 text-sm font-semibold shadow-lg hover:-translate-y-0.5 transition">
                  打开 AI 助手
                </button>
                <div class="flex -space-x-2">
                  <span class="w-8 h-8 rounded-full border border-white/30 bg-white/20 backdrop-blur"></span>
                  <span class="w-8 h-8 rounded-full border border-white/30 bg-white/20 backdrop-blur"></span>
                  <span class="w-8 h-8 rounded-full border border-white/30 bg-white/20 backdrop-blur"></span>
                </div>
              </div>
            </div>

            <div class="glass-card front-surface p-6 flex flex-col gap-4 shadow-2xl">
              <div class="flex items-center justify-between">
                <p class="text-sm uppercase tracking-[0.25em] front-text-soft">最新文章</p>
                <button class="text-xs px-3 py-1 rounded-full bg-slate-900 text-white shadow hover:-translate-y-0.5 transition"
                  @click="scrollToArticles">查看全部</button>
              </div>
              <div class="space-y-3 max-h-[360px] overflow-hidden">
                <div v-for="(article, index) in articles.slice(0, 4)" :key="index"
                  class="flex items-center gap-3 p-3 rounded-2xl border front-surface-soft front-hover-surface shadow-sm hover:-translate-y-0.5 cursor-pointer"
                  @click="goArticleDetailPage(article.id)">
                  <div class="w-12 h-12 rounded-xl bg-slate-100 dark:bg-slate-800 overflow-hidden">
                    <img v-if="article.cover" :src="article.cover" class="w-full h-full object-cover" />
                    <div v-else class="w-full h-full flex items-center justify-center text-xs front-text-weaker">封面</div>
                  </div>
                  <div class="flex-1">
                    <p class="text-sm font-semibold line-clamp-1 front-text">{{ article.title || '未命名' }}</p>
                    <p class="text-xs front-text-soft">{{ article.createDate }}</p>
                  </div>
                  <span class="text-xs text-indigo-600 hover:underline">阅读全文</span>
                </div>
                <p v-if="!articles.length" class="text-sm front-text-soft">还没有文章，等我写点东西再来吧。</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <main id="articles" class="page-shell -mt-12 pb-20 relative z-10 space-y-6">
      <div class="flex flex-wrap items-center justify-between gap-3">
        <div class="space-y-1">
          <p class="text-xs uppercase tracking-[0.25em] front-text-soft">Latest stream</p>
          <h2 class="text-2xl font-semibold text-slate-900 dark:text-white">正在更新的内容</h2>
        </div>
      </div>

      <div class="grid grid-cols-12 gap-8">
        <div class="col-span-12 lg:col-span-8 space-y-6">
          <template v-if="loading">
            <div v-for="i in 3" :key="i" class="glass-card front-surface-soft h-44 animate-pulse"></div>
          </template>

          <template v-else>
            <div v-if="articles.length === 0" class="glass-card p-10 text-center">
              <p class="front-text-soft text-lg">暂无文章，等我写点有趣的再来。</p>
            </div>

            <div class="stream-grid">
              <article v-for="(article, index) in articles" :key="index"
                class="glass-card overflow-hidden hover:-translate-y-1 transition duration-300 border front-border shadow-lg">
                <div class="relative aspect-[4/3] bg-slate-900/5 cursor-pointer" @click="goArticleDetailPage(article.id)">
                  <img v-if="article.cover" :src="article.cover"
                    class="w-full h-full object-cover transition duration-500 hover:scale-105" />
                  <div v-else class="w-full h-full flex items-center justify-center text-4xl front-text-weaker">✏️</div>
                  <div class="absolute inset-0 bg-gradient-to-t from-slate-900/40 to-transparent"></div>
                  <div class="absolute top-4 left-4 px-3 py-1 rounded-full text-xs font-semibold bg-white/90 text-slate-800 dark:bg-slate-100 dark:text-slate-900">
                    {{ article.category?.name || '未分类' }}
                  </div>
                </div>

                <div class="p-6 space-y-3">
                  <div class="flex items-center text-xs front-text-soft gap-3">
                    <span class="inline-flex items-center gap-1">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z">
                        </path>
                      </svg>
                      {{ article.createDate }}
                    </span>
                    <span class="inline-flex items-center gap-1">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z">
                        </path>
                      </svg>
                      {{ article.readNum || 0 }}
                    </span>
                  </div>
                  <h2 class="text-xl font-semibold text-slate-900 dark:text-white cursor-pointer hover:text-indigo-600"
                    @click="goArticleDetailPage(article.id)">
                    {{ article.title }}
                  </h2>
                  <p class="front-text-soft text-sm leading-relaxed line-clamp-2">
                    {{ article.summary || '这篇文章还没有摘要，点击阅读全文吧。' }}
                  </p>
                  <div class="flex flex-wrap gap-2 pt-1">
                    <span v-for="tag in article.tags" :key="tag.id" @click.stop="goTagArticleListPage(tag.id, tag.name)"
                      class="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs bg-slate-900 text-white cursor-pointer hover:-translate-y-0.5 transition">
                      #{{ tag.name }}
                    </span>
                  </div>
                </div>
              </article>
            </div>
          </template>

          <div v-if="pages > 1" class="flex justify-center pt-4">
            <div class="front-pagination inline-flex items-center gap-2 rounded-full px-2 py-1">
              <button @click="getArticles(current - 1)" :disabled="current <= 1"
                class="front-pagination-btn px-3 py-2 rounded-full text-sm font-semibold">上一页</button>
              <button v-for="p in pages" :key="p" @click="getArticles(p)"
                class="front-pagination-btn w-9 h-9 rounded-full text-sm font-semibold"
                :class="p === current ? 'front-pagination-btn-active' : ''">
                {{ p }}
              </button>
              <button @click="getArticles(current + 1)" :disabled="current >= pages"
                class="front-pagination-btn px-3 py-2 rounded-full text-sm font-semibold">下一页</button>
            </div>
          </div>
        </div>

        <aside class="col-span-12 lg:col-span-4 space-y-4">
          <UserInfoCard />
          <CategoryListCard />
          <TagListCard />
        </aside>
      </div>
    </main>

    <ScrollToTopButton />
    <Footer />
  </div>
</template>

<script setup>
import Header from '@/layouts/frontend/components/Header.vue';
import Footer from '@/layouts/frontend/components/Footer.vue';
import UserInfoCard from '@/layouts/frontend/components/UserInfoCard.vue';
import CategoryListCard from '@/layouts/frontend/components/CategoryListCard.vue';
import TagListCard from '@/layouts/frontend/components/TagListCard.vue';
import ScrollToTopButton from '@/layouts/frontend/components/ScrollToTopButton.vue';
import { initTooltips } from 'flowbite';
import { onMounted, ref } from 'vue';
import { getArticlePageList } from '@/api/frontend/article';
import { useRouter } from 'vue-router';

const router = useRouter();

onMounted(() => {
  initTooltips();
});

const articles = ref([]);
const loading = ref(true);
const current = ref(1);
const size = ref(10);
const total = ref(0);
const pages = ref(0);

function getArticles(currentNo) {
  if (currentNo < 1 || (pages.value > 0 && currentNo > pages.value)) return;
  loading.value = true;
  getArticlePageList({ current: currentNo, size: size.value }).then((res) => {
    if (res.success) {
      articles.value = res.data;
      current.value = res.current;
      size.value = res.size;
      total.value = res.total;
      pages.value = res.pages;
    }
  }).finally(() => {
    loading.value = false;
  });
}
getArticles(current.value);

const goArticleDetailPage = (articleId) => {
  router.push('/article/' + articleId);
};

const goTagArticleListPage = (id, name) => {
  router.push({ path: '/tag/article/list', query: { id, name } });
};

const scrollToArticles = () => {
  const el = document.getElementById('articles');
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }
};
</script>

<style scoped>
.home-aura {
  position: relative;
  min-height: 100vh;
  background: #f7f9ff;
  color: #0f172a;
}

.home-noise {
  display: none;
}

:global(.dark) .home-aura {
  background:
    radial-gradient(80% 60% at 20% 20%, rgba(94, 138, 255, 0.16), transparent 55%),
    radial-gradient(70% 50% at 80% 0%, rgba(109, 230, 210, 0.14), transparent 45%),
    #050912;
  color: #e5e7eb;
  transition: background-color 0.3s ease;
}

.hero-panel {
  border-radius: 24px;
  padding: 22px;
  background: linear-gradient(135deg, rgba(104, 224, 207, 0.25), rgba(165, 107, 255, 0.22));
  border: 1px solid rgba(255, 255, 255, 0.24);
  box-shadow: 0 30px 90px rgba(10, 22, 51, 0.38), inset 0 1px 0 rgba(255, 255, 255, 0.28);
  backdrop-filter: blur(10px);
}

.stat-chip {
  border-radius: 18px;
  padding: 12px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(10px);
}
</style>

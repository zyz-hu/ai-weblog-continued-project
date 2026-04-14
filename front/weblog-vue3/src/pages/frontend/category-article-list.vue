<template>
    <Header></Header>

    <section class="relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-to-r from-indigo-600 via-slate-900 to-emerald-500 opacity-90"></div>
        <div class="page-shell relative z-10 py-12">
            <p class="text-xs uppercase tracking-[0.35em] text-white/70 mb-2">Category</p>
            <h1 class="text-3xl font-extrabold text-white mb-3">{{ categoryName }}</h1>
            <p class="text-white/80 max-w-2xl">这一组文章属于 {{ categoryName }}，简单、安静、为自己而写。</p>
        </div>
    </section>

    <main class="page-shell -mt-8 pb-16 relative z-10">
        <div class="grid grid-cols-12 gap-8">
            <div class="col-span-12 lg:col-span-8 space-y-6">
                <div v-if="articles.length === 0" class="glass-card p-10 text-center">
                    <p class="front-text-soft">这个分类还没有内容，稍等我再来填满它。</p>
                </div>
                <div class="stream-grid">
                    <article v-for="(article, index) in articles" :key="index"
                        class="glass-card p-5 hover:-translate-y-1 transition cursor-pointer" @click="goArticleDetailPage(article.id)">
                        <div class="flex items-center justify-between mb-3 text-xs front-text-soft">
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
                        <div class="space-y-2">
                            <h2 class="text-xl font-semibold front-text hover:text-indigo-600">
                                {{ article.title }}
                            </h2>
                            <p class="text-sm front-text-soft line-clamp-2">
                                {{ article.summary || '这篇文章还没有摘要，点击阅读正文吧。' }}
                            </p>
                        </div>
                    </article>
                </div>

                <div v-if="pages > 1" class="flex justify-center pt-4">
                    <div class="front-pagination inline-flex items-center gap-2 rounded-full px-2 py-1">
                        <button @click="getCategoryArticles(current - 1)" :disabled="current <= 1"
                            class="front-pagination-btn px-3 py-2 rounded-full text-sm font-semibold">上一页</button>
                        <button v-for="p in pages" :key="p" @click="getCategoryArticles(p)"
                            class="front-pagination-btn w-9 h-9 rounded-full text-sm font-semibold"
                            :class="p === current ? 'front-pagination-btn-active' : ''">
                            {{ p }}
                        </button>
                        <button @click="getCategoryArticles(current + 1)" :disabled="current >= pages"
                            class="front-pagination-btn px-3 py-2 rounded-full text-sm font-semibold">下一页</button>
                    </div>
                </div>
            </div>

            <aside class="col-span-12 lg:col-span-4 space-y-4">
                <UserInfoCard></UserInfoCard>
                <CategoryListCard></CategoryListCard>
                <TagListCard></TagListCard>
            </aside>
        </div>
    </main>

    <ScrollToTopButton></ScrollToTopButton>
    <Footer></Footer>
</template>

<script setup>
import Header from '@/layouts/frontend/components/Header.vue';
import Footer from '@/layouts/frontend/components/Footer.vue';
import UserInfoCard from '@/layouts/frontend/components/UserInfoCard.vue';
import TagListCard from '@/layouts/frontend/components/TagListCard.vue';
import CategoryListCard from '@/layouts/frontend/components/CategoryListCard.vue';
import ScrollToTopButton from '@/layouts/frontend/components/ScrollToTopButton.vue';
import { ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getCategoryArticlePageList } from '@/api/frontend/category';

const route = useRoute();
const router = useRouter();

const articles = ref([]);
const categoryName = ref(route.query.name);
const categoryId = ref(route.query.id);

watch(route, (newRoute) => {
    categoryName.value = newRoute.query.name;
    categoryId.value = newRoute.query.id;
    getCategoryArticles(current.value);
});

const current = ref(1);
const size = ref(10);
const total = ref(0);
const pages = ref(0);

function getCategoryArticles(currentNo) {
    if (currentNo < 1 || (pages.value > 0 && currentNo > pages.value)) return;
    getCategoryArticlePageList({ current: currentNo, size: size.value, id: categoryId.value }).then((res) => {
        if (res.success) {
            articles.value = res.data;
            current.value = res.current;
            size.value = res.size;
            total.value = res.total;
            pages.value = res.pages;
        }
    });
}
getCategoryArticles(current.value);

const goArticleDetailPage = (articleId) => {
    router.push('/article/' + articleId);
};
</script>

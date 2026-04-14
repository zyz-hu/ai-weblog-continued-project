<template>
    <div class="min-h-screen flex flex-col">
        <Header></Header>

        <section class="relative overflow-hidden">
            <div class="absolute inset-0 bg-gradient-to-r from-slate-900 via-indigo-700 to-slate-900 opacity-95"></div>
            <div class="page-shell relative z-10 py-12">
                <p class="text-xs uppercase tracking-[0.35em] text-white/70 mb-2">Archive</p>
                <h1 class="text-3xl font-extrabold text-white mb-3">归档</h1>
                <p class="text-white/80 max-w-2xl">翻阅旧文就像翻笔记本——记得当时的心情，也留下更新的脚印。</p>
            </div>
        </section>

        <main class="page-shell -mt-8 pb-16 relative z-10 flex-1">
            <div class="grid grid-cols-12 gap-8">
                <div class="col-span-12 lg:col-span-8 space-y-6">
                    <div v-if="archives.length === 0" class="glass-card p-10 text-center">
                        <p class="front-text-soft">暂时没有归档内容。</p>
                    </div>

                    <div class="stream-grid">
                        <div v-for="(archive, index) in archives" :key="index" class="glass-card p-6 space-y-4">
                            <div class="flex items-center justify-between">
                                <p class="text-sm font-semibold front-text">{{ archive.year }} · {{ archive.month }}</p>
                                <p class="text-xs front-text-soft">本月共 {{ getCount(archive) }} 篇</p>
                            </div>
                            <div class="space-y-3">
                                <div v-for="(article, idx) in archive.articles" :key="idx"
                                    class="flex items-center gap-3 p-3 rounded-xl front-hover-surface cursor-pointer transition"
                                    @click="goArticleDetailPage(article.id)">
                                    <div class="flex-1">
                                        <p class="font-semibold front-text">{{ article.title }}</p>
                                        <p class="text-xs front-text-soft">{{ article.createTime }}</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div v-if="pages > 1" class="flex justify-center pt-4">
                        <div class="front-pagination inline-flex items-center gap-2 rounded-full px-2 py-1">
                            <button @click="getArchives(current - 1)" :disabled="current <= 1"
                                class="front-pagination-btn px-3 py-2 rounded-full text-sm font-semibold">上一页</button>
                            <button v-for="p in pages" :key="p" @click="getArchives(p)"
                                class="front-pagination-btn w-9 h-9 rounded-full text-sm font-semibold"
                                :class="p === current ? 'front-pagination-btn-active' : ''">
                                {{ p }}
                            </button>
                            <button @click="getArchives(current + 1)" :disabled="current >= pages"
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

        <Footer class="mt-auto"></Footer>
    </div>
</template>

<script setup>
import Header from '@/layouts/frontend/components/Header.vue';
import Footer from '@/layouts/frontend/components/Footer.vue';
import UserInfoCard from '@/layouts/frontend/components/UserInfoCard.vue';
import TagListCard from '@/layouts/frontend/components/TagListCard.vue';
import CategoryListCard from '@/layouts/frontend/components/CategoryListCard.vue';
import ScrollToTopButton from '@/layouts/frontend/components/ScrollToTopButton.vue';
import { getArchivePageList } from '@/api/frontend/archive';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const archives = ref([]);
const current = ref(1);
const size = ref(10);
const total = ref(0);
const pages = ref(0);

const getCount = (archive) => {
    return archive?.articleCount ?? archive?.count ?? archive?.total ?? archive?.totalCount ?? archive?.articleNum ?? 0;
};

function getArchives(currentNo) {
    if (currentNo < 1 || (pages.value > 0 && currentNo > pages.value)) return;
    getArchivePageList({ current: currentNo, size: size.value }).then((res) => {
        if (res.success) {
            archives.value = res.data;
            current.value = res.current;
            size.value = res.size;
            total.value = res.total;
            pages.value = res.pages;
        }
    });
}
getArchives(current.value);

const goArticleDetailPage = (articleId) => {
    router.push('/article/' + articleId);
};
</script>

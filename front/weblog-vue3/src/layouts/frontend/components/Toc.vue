<template>
    <div v-if="titles && titles.length > 0" class="glass-card sticky top-24 text-sm/[26px] w-full p-5 mb-3">
        <div class="flex items-center justify-between mb-3">
            <p class="section-title">文章目录</p>
            <span class="text-xs front-text-weaker">{{ titles.length }} 节</span>
        </div>
        <div class="border-l border-slate-200 dark:border-slate-700/70">
            <ul class="space-y-2">
                <li v-for="(h2, index) in titles" :key="index" class="pl-4"
                    :class="[h2.index == activeHeadingIndex ? 'text-indigo-600 font-semibold' : 'front-text-soft']">
                    <span @click="scrollToView(h2.offsetTop)" class="hover:text-indigo-600 cursor-pointer">{{ h2.text }}</span>
                    <ul v-if="h2.children && h2.children.length > 0" class="mt-1 space-y-1">
                        <li v-for="(h3, index2) in h2.children" :key="index2" class="pl-4 text-xs"
                            :class="[h3.index == activeHeadingIndex ? 'text-indigo-600 font-semibold' : 'front-text-soft']">
                            <span @click="scrollToView(h3.offsetTop)" class="hover:text-indigo-600 cursor-pointer">{{ h3.text }}</span>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';

const titles = ref([]);
onMounted(() => {
    const container = document.querySelector('.article-content');
    const observer = new MutationObserver(mutationsList => {
        for (let mutation of mutationsList) {
            if (mutation.type === 'childList') {
                titles.value = [];
                initTocData(container);
                const images = container.querySelectorAll('img');
                images.forEach(img => {
                    img.addEventListener('load', () => {
                        initTocData(container);
                    });
                });
                window.addEventListener('scroll', handleContentScroll);
            }
        }
    });
    const config = { childList: true, subtree: true };
    observer.observe(container, config);
});

const activeHeadingIndex = ref(-1);
function handleContentScroll() {
    let scrollY = window.scrollY;
    titles.value.forEach(title => {
        let offsetTop = title.offsetTop;
        if (scrollY >= offsetTop) {
            activeHeadingIndex.value = title.index;
        }
        let children = title.children;
        if (children && children.length > 0) {
            children.forEach(child => {
                let childOffsetTop = child.offsetTop;
                if (scrollY >= childOffsetTop) {
                    activeHeadingIndex.value = child.index;
                }
            });
        }
    });
}

onBeforeUnmount(() => window.removeEventListener('scroll', handleContentScroll));

function scrollToView(offsetTop) {
    window.scrollTo({ top: offsetTop, behavior: "smooth" });
}

function initTocData(container) {
    let levels = ['h2', 'h3'];
    let headings = container.querySelectorAll(levels);
    let titlesArr = [];
    let index = 1;
    headings.forEach(heading => {
        let headingLevel = parseInt(heading.tagName.substring(1));
        let headingText = heading.innerText;
        let offsetTop = heading.offsetTop - 95;
        if (headingLevel === 2) {
            titlesArr.push({
                index,
                level: headingLevel,
                text: headingText,
                offsetTop,
                children: []
            });
        } else {
            let parentHeading = titlesArr[titlesArr.length - 1];
            parentHeading.children.push({
                index,
                level: headingLevel,
                text: headingText,
                offsetTop
            });
        }
        index++;
    });
    titles.value = titlesArr;
}
</script>

import { defineStore } from 'pinia';
import { ref, computed, watch } from 'vue';

export const useAiThemeStore = defineStore('ai-theme', () => {
  const theme = ref(localStorage.getItem('ai-theme') || 'light');
  const isDarkMode = computed(() => theme.value === 'dark');

  function toggleTheme() {
    theme.value = theme.value === 'light' ? 'dark' : 'light';
  }

  watch(
    theme,
    (newTheme) => {
      localStorage.setItem('ai-theme', newTheme);
      if (newTheme === 'dark') {
        document.documentElement.classList.add('dark');
      } else {
        document.documentElement.classList.remove('dark');
      }
    },
    { immediate: true }
  );

  return { theme, isDarkMode, toggleTheme };
});

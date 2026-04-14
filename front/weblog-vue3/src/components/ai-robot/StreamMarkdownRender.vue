<template>
  <div class="markdown-body">
    <div v-html="renderedContent"></div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue';
import MarkdownIt from 'markdown-it';
import hljs from 'highlight.js';
import markdownItHighlightJs from 'markdown-it-highlightjs';
import { message } from 'ant-design-vue';

const props = defineProps({ content: { type: String, default: '' } });
const renderedContent = ref('');

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  breaks: true,
});

md.use(markdownItHighlightJs, { hljs, auto: true, code: true });

const defaultLinkRender = md.renderer.rules.link_open || function (tokens, idx, options, env, renderer) {
  return renderer.renderToken(tokens, idx, options);
};

md.renderer.rules.link_open = function (tokens, idx, options, env, renderer) {
  const token = tokens[idx];
  if (!token.attrGet('target')) {
    token.attrPush(['target', '_blank']);
  }
  if (!token.attrGet('rel')) {
    token.attrPush(['rel', 'noopener noreferrer']);
  }
  return defaultLinkRender(tokens, idx, options, env, renderer);
};

const defaultRender = md.renderer.rules.fence || function (tokens, idx, options, env, renderer) {
  return renderer.renderToken(tokens, idx, options);
};

md.renderer.rules.fence = function (tokens, idx, options, env, renderer) {
  const token = tokens[idx];
  const info = token.info ? md.utils.unescapeAll(token.info).trim() : '';
  const langName = info ? info.split(/\s+/g)[0].toLowerCase() : '';
  const codeContent = token.content;
  const codeId = `code-${Math.random().toString(36).substr(2, 9)}`;
  const originalContent = defaultRender(tokens, idx, options, env, renderer);

  return `
    <div class="code-wrapper group">
      <div class="code-header">
        <span class="lang-tag">${langName}</span>
        <button class="copy-btn" onclick="copyCode('${codeId}')">
          <span class="copy-icon">📋</span>
        </button>
      </div>
      <div id="${codeId}" data-code="${encodeURIComponent(codeContent)}" class="code-body">
        ${originalContent}
      </div>
    </div>
  `;
};

const setupCopy = () => {
  if (!window.copyCode) {
    window.copyCode = async (id) => {
      const el = document.getElementById(id);
      if (!el) return;
      try {
        await navigator.clipboard.writeText(decodeURIComponent(el.getAttribute('data-code')));
        message.success('已复制');
      } catch (e) {
        message.error('复制失败');
      }
    };
  }
};

watch(() => props.content, (val) => {
  if (val) {
    let safe = val.replace(/(^|[\r\n]+)(\s*)(#{1,6})([^\s#])/g, '$1$2$3 $4');
    safe = safe.replace(/\[来源(\d+)\]/g, '<sup class="citation">[$1]</sup>');
    renderedContent.value = md.render(safe);
    nextTick(setupCopy);
  } else {
    renderedContent.value = '';
  }
}, { immediate: true });
</script>

<style>
/* --- 容器基础 --- */
.markdown-body {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
  font-size: 15px;
  line-height: 1.75;
  color: #1f2937;
}

.dark .markdown-body {
  color: #e5e7eb !important;
}

.markdown-body h1, .markdown-body h2, .markdown-body h3 {
  font-weight: 600;
  margin-top: 1.5em;
  margin-bottom: 0.8em;
  line-height: 1.3;
  color: #111827;
}
.dark .markdown-body h1, .dark .markdown-body h2, .dark .markdown-body h3 {
  color: #ffffff !important;
}

.markdown-body p { margin-bottom: 1em; }
.markdown-body ul, .markdown-body ol { padding-left: 1.2em; margin-bottom: 1em; }
.markdown-body li { margin-bottom: 0.25em; }

.markdown-body a { color: #2563eb; text-decoration: none; font-weight: 500; }
.markdown-body a:hover { text-decoration: underline; }
.dark .markdown-body a { color: #60a5fa !important; }

.markdown-body blockquote {
  border-left: 3px solid #e5e7eb;
  padding-left: 1em;
  color: #6b7280;
  margin: 1em 0;
}
.dark .markdown-body blockquote {
  border-left-color: #4b5563;
  color: #9ca3af !important;
}

.markdown-body :not(pre) > code {
  background-color: rgba(175, 184, 193, 0.2);
  border-radius: 4px;
  padding: 0.2em 0.4em;
  font-size: 85%;
  font-family: ui-monospace, SFMono-Regular, SF Mono, Menlo, Consolas, monospace;
}
.dark .markdown-body :not(pre) > code {
  background-color: rgba(110, 118, 129, 0.4) !important;
  color: #e5e7eb !important;
}

.code-wrapper {
  margin: 1em 0;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
  background: #f9fafb;
}
.dark .code-wrapper {
  border-color: #30363d;
  background: #0d1117 !important;
}

.code-header {
  display: flex;
  justify-content: space-between;
  padding: 6px 12px;
  background: #f3f4f6;
  border-bottom: 1px solid #e5e7eb;
  font-size: 12px;
  color: #6b7280;
}
.dark .code-header {
  background: #161b22 !important;
  border-bottom-color: #30363d;
  color: #8b949e;
}

.code-body {
  padding: 12px;
  overflow-x: auto;
  background: transparent !important;
}
.markdown-body pre {
  margin: 0;
  padding: 0;
  background: transparent !important;
}
.markdown-body pre code.hljs {
  padding: 0;
  background: transparent !important;
  font-size: 13px;
  line-height: 1.5;
}

.copy-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  background: transparent;
  border: none;
  cursor: pointer;
  color: inherit;
  opacity: 0.7;
}
.copy-btn:hover { opacity: 1; }

.citation {
  color: #2563eb;
  font-weight: bold;
  margin-left: 2px;
  cursor: pointer;
}
.dark .citation { color: #60a5fa; }

.dark .hljs-keyword, .dark .hljs-selector-tag { color: #ff7b72 !important; }
.dark .hljs-string, .dark .hljs-doctag { color: #a5d6ff !important; }
.dark .hljs-title, .dark .hljs-section { color: #d2a8ff !important; }
.dark .hljs-comment, .dark .hljs-quote { color: #8b949e !important; font-style: italic; }
.dark .hljs-variable { color: #79c0ff !important; }
.dark .hljs-literal, .dark .hljs-number { color: #79c0ff !important; }
</style>

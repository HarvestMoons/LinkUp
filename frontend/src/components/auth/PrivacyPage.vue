<template>
  <div class="container">
    <!-- 添加加载状态提示 -->
    <div v-if="loading">{{$t('common.loading')}}</div>
    <div v-else v-html="renderedMarkdown"></div>
  </div>
</template>

<script>
import { ref, watch, onMounted } from "vue";
import MarkdownIt from "markdown-it";
import { useI18n } from "vue-i18n";

export default {
  setup() {
    const { locale } = useI18n();
    const mdParser = new MarkdownIt();
    const renderedMarkdown = ref("");
    const loading = ref(true);
    const error = ref(null);

    const loadMarkdown = async () => {
      loading.value = true;
      error.value = null;
      try {
        // 关键修改：显式指定文件路径格式
        const markdownModule = await import(
            /* @vite-ignore */ `@/assets/markdown/${locale.value}-privacy.md?raw`
            );
        renderedMarkdown.value = mdParser.render(markdownModule.default);
      } catch (err) {
        console.error("Error loading markdown:", err);
        error.value = err;
      } finally {
        loading.value = false;
      }
    };

    // 用 onMounted 确保组件挂载后加载
    onMounted(loadMarkdown);

    // 监听语言变化
    watch(locale, loadMarkdown);

    return { renderedMarkdown, loading, error };
  },
};
</script>

<style scoped>
.container {
  text-align: left; /* 让文本左对齐 */
  padding: 20px;    /* 为容器添加内边距，避免文本紧贴边缘 */
}

.container p {
  margin-bottom: 10px; /* 设置段落之间的间距 */
}

.container ul, .container ol {
  margin-left: 20px; /* 为列表添加左边距 */
}
</style>

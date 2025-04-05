<template>
  <div class="container">
    <div v-html="renderedMarkdown"></div>
  </div>
</template>

<script>
import { ref, watch } from "vue";
import MarkdownIt from "markdown-it";
import { useI18n } from "vue-i18n";

export default {
  setup() {
    const { locale } = useI18n();  // 获取当前的翻译函数和语言
    const mdParser = new MarkdownIt();
    let renderedMarkdown = ref("");

    // 动态加载和渲染Markdown文件
    const loadMarkdown = async () => {
      try {
        const markdownModule = await import(`@/assets/markdown/${locale.value}-privacy.md`);  // 动态导入 Markdown 文件
        const text = markdownModule.default;  // 默认导出的文件内容
        renderedMarkdown.value = mdParser.render(text);  // 渲染 Markdown 内容
      } catch (error) {
        console.error("Error loading markdown file:", error);
      }
    };

    // 监听语言变化，切换Markdown文件
    watch(locale, () => {
      loadMarkdown();
    });

    // 初始加载
    loadMarkdown();

    return { renderedMarkdown };
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

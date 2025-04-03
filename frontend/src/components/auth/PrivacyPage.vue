<template>
  <div class="container">
    <div v-html="renderedMarkdown"></div>
  </div>
</template>

<script>
import { ref } from "vue";
import MarkdownIt from "markdown-it";
import privacyMd from "@/assets/privacy.md"; // 现在可以正确加载

export default {
  setup() {
    const mdParser = new MarkdownIt();
    const renderedMarkdown = ref(mdParser.render(privacyMd));
    return { renderedMarkdown };
  },
};
</script>

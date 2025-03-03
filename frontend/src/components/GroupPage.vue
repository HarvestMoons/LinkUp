<!-- GroupPage.vue -->
<template>
  <div>
    <h1>群组详情</h1>
    <p>群组 ID: {{ groupId }}</p>
    <div v-if="groupData">
      <h2>{{ groupData.name }}</h2>
      <p>{{ groupData.description }}</p>
    </div>
    <div v-else>
      <p>加载中...</p>
    </div>
  </div>
</template>

<script>
import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";
import { useRoute } from "vue-router";

export default {
  name: "GroupPage",
  data() {
    return {
      groupId: null,
      groupData: { id: 1, name: "群组名称", description: "群组描述" },
      isMember: false,
      loading: true,
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  async mounted() {
    this.groupId = useRoute().params.id;
    //await this.checkMembership();
    //if (this.isMember) {
    this.fetchGroup();
    //}
  },
  methods: {
    async checkMembership() {
      try {
        // TODO: 需要后端做一个检查用户是否在群组中的url
        const response = await this.$axios.get(
          `/groups/${this.groupId}/members/check-member`
        );
        this.isMember = response.data;
        if (!this.isMember) {
          showToast(this.toast, "你不是该群组的成员，无法访问！", "error");
          this.$router.push("/"); // 重定向到首页
        }
      } catch (error) {
        console.error("检查群组权限失败", error);
        showToast(this.toast, "权限检查失败，请重试！", "error");
        this.$router.push("/");
      } finally {
        this.loading = false;
      }
    },
    async fetchGroup() {
      try {
        const response = await this.$axios.get(`/task-group/${this.groupId}`);
        this.groupData.value = response.data;
      } catch (error) {
        console.error("加载群组失败", error);
      }
    },
  },
};
</script>

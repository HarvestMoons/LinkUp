<!-- SidebarMenu.vue -->
<template>
  <div class="sidebarMenu">
    <router-link
      v-if="$store.getters.isAuthenticated"
      to="/user"
      class="userContainer"
    >
      <img
        :src="$store.getters.getUserAvatar"
        :alt="$t('common.avatarAlt')"
        class="userAvatar"
      />
      <span class="userNickname"
        >{{ $store.getters.getUser?.username || "" }} (#{{
          $store.getters.getUserId
        }})
      </span>
    </router-link>
    <router-link to="/home" @click="navigateAndClose" class="tab">{{
      $t("dashboard.home")
    }}</router-link>
    <router-link to="/friends" @click="navigateAndClose" class="tab">{{
      $t("dashboard.friends")
    }}</router-link>
    <router-link to="/tasks" @click="navigateAndClose" class="tab">{{
      $t("dashboard.tasks")
    }}</router-link>
    <router-link to="/groups" @click="navigateAndClose" class="tab">{{
      $t("dashboard.groups")
    }}</router-link>

    <select
      v-model="$i18n.locale"
      class="languageSelect"
      @change="changeLanguage"
    >
      <option value="en">English</option>
      <option value="zh-CN">中文</option>
      <option value="es">Español</option>
      <option value="ja">日本語</option>
    </select>

    <button
      v-if="$store.getters.isAuthenticated"
      class="button normalButton logoutButton"
      @click="logout"
    >
      {{ $t("dashboard.logout") }}
    </button>
  </div>
</template>

<script>
export default {
  methods: {
    changeLanguage(event) {
      const lang = event.target.value;
      this.$store.commit("setLanguage", lang);
      localStorage.setItem("userLanguage", lang);
      this.$i18n.locale = lang;
    },
    logout() {
      this.$store.dispatch("logout");
      this.$router.push("/login");
      this.$emit("request-close");
    },
    navigateAndClose() {
      this.$emit("request-close");
    },
  },
};
</script>

<style scoped>
.sidebarMenu {
  margin: 20px;
  display: flex;
  flex-direction: column;
  width: 100%;
  align-items: center;
}

.sidebarMenu a {
  text-decoration: none;
  color: #433f3e;
  font-weight: bold;
}

.userAvatar {
  height: 50px;
  border-radius: 50%;
  margin-right: 1.25%; /* 头像和昵称之间的间距 */
}

.userContainer {
  display: flex;
  align-items: center; /* 确保头像和文字都在导航栏中垂直居中 */
  justify-content: center; /* 水平居中对齐 */
  padding-bottom: 20px;
}

.tab {
  width: 80%;
  font-size: 15px;
  padding-top: 20px;
  padding-bottom: 20px;
  border-top: 1px solid #433f3e;
}

.languageSelect,
.button {
  width: 80%;
  margin-top: 10px;
  margin-bottom: 10px;
}
</style>

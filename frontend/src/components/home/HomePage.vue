<!-- HomePage.vue -->
<template>
  <div class="container">
    <h1>Welcome to the Home Page</h1>
    <p>Hello! You have successfully logged in.</p>
    <img src="@/assets/images/icon.png" style="padding: 10px" alt="" />

    <div>
      <button @click="logout" class="longButton normalButton">Logout</button>
    </div>
  </div>
</template>

<script>
export default {
  name: "HomePage",
  methods: {
    async logout() {
      this.$store.dispatch("logout");
      this.$router.push("/login");
    },
  },
};
</script>

<style scoped>
.longButton {
  width: 50%;
}
</style>
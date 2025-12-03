<script setup>
import { RouterView } from 'vue-router'
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

onMounted(() => {
  if (authStore.token && !authStore.user) {
    authStore.fetchUser()
  }
})

</script>

<template>
  <main>
    <RouterView />
  </main>
</template>

<style scoped>

main {
  width: 100%;
  min-height: 100vh;
}
</style>
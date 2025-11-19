<template>
  <div class="auth-status" v-if="username">
    Zalogowany jako: <strong>{{ username }}</strong><br />
    Rola: <strong>{{ role }}</strong>
  </div>

  <div v-else>
    Nie jesteś zalogowany.
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const username = ref(null)
const role = ref(null)

onMounted(async () => {
  const token = localStorage.getItem('token') || sessionStorage.getItem('token')
  if (!token) return

  try {
    const response = await axios.get('http://localhost:8080/api/users/me', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    username.value = response.data.username
    role.value = response.data.role

  } catch (error) {
    console.error('Błąd pobierania użytkownika:', error)
  }
})
</script>

<style scoped>
.auth-status {
  font-weight: 500;
  font-size: 1rem;
  color: #fff;
}
</style>






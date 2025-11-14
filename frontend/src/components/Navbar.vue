<template>
  <nav class="navbar navbar-expand-lg navbar-dark sticky-top">
    <div class="container-fluid">
      <router-link to="/" class="navbar-brand mx-md-3">Singly</router-link>
      <div v-if="showSearch" class="mx-auto search-wrapper">
        <slot></slot>
      </div>
      <div class="d-flex">
        <button v-if="!loggedIn" @click="goLogin" class="btn btn-outline-light me-3">
          Zaloguj się
        </button>
        <button v-if="!loggedIn" @click="goRegister" class="btn btn-primary">
          Zarejestruj się
        </button>
        <button v-if="loggedIn" @click="logout" class="btn btn-outline-light">
          Wyloguj
        </button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

defineProps({
  showSearch: {
    type: Boolean,
    default: false
  }
})

const loggedIn = ref(false)
const router = useRouter()

function checkLoginStatus() {
  const token = localStorage.getItem('token') || sessionStorage.getItem('token')
  loggedIn.value = !!token
}

onMounted(checkLoginStatus)

function goLogin() {
  router.push('/login')
}

function goRegister() {
  router.push('/register')
}

function logout() {
  localStorage.removeItem('token')
  sessionStorage.removeItem('token')
  loggedIn.value = false
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  background: rgba(24, 24, 24, 0.6);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.navbar-brand {
  font-weight: bold;
  color: #ffffff;
  font-size: 1.5rem;
}

.btn-outline-light {
  padding: 10px 35px;
  font-weight: bold;
}

.btn-primary {
  background-color: #6C63FF;
  border-color: #6C63FF;
  padding: 12px 35px;
  font-weight: bold;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(108, 99, 255, 0.3);
}

.btn-primary:hover {
  background-color: #574feb;
  border-color: #574feb;
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(108, 99, 255, 0.4);
}

.search-wrapper {
  display: flex;
  justify-content: center;
  width: 300px;
}
</style>




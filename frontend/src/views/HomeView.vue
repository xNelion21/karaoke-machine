<template>
  <div class="landing-page d-flex flex-column min-vh-100 text-white">
    <Navbar />

    <main class="hero-section flex-grow-1 d-flex align-items-center justify-content-center py-5 py-lg-6">
      <div class="container">
        <div class="row justify-content-center">
          <div class="col-md-9 text-center">
            <h1 class="display-2 fw-bold hero-title">
              Twoja scena. Twoje słowa. Twój głos.
            </h1>
            <p class="lead mt-4 mb-5 hero-subtitle">
              Śpiewaj ulubione utwory z tekstem zsynchronizowanym w idealnym momencie. Dołącz do społeczności pasjonatów muzyki.
            </p>
            <router-link v-if="!loggedIn" to="/register" class="btn btn-primary btn-lg cta-button">
              Dołącz za darmo
            </router-link>
            <router-link v-else to="/app" class="btn btn-primary btn-lg cta-button">
              Zacznij śpiewac!
            </router-link>
          </div>
        </div>
      </div>
    </main>

    <footer class="footer d-flex flex-wrap justify-content-between align-items-center py-3 px-4">
      <div class="col-md-4 d-flex align-items-center ">
        <span class="mb-3 mb-md-0 fw-bold">© 2025 Singly</span>
      </div>

      <ul class="nav col-md-4 justify-content-end list-unstyled d-flex">
        <li class="ms-3"><a href="https://x.com" target="_blank" class=""><i class="bi bi-twitter-x"></i></a></li>
        <li class="ms-3"><a href="https://instagram.com" target="_blank" class=""><i class="bi bi-instagram"></i></a></li>
        <li class="ms-3"><a href="https://facebook.com" target="_blank" class=""><i class="bi bi-facebook"></i></a></li>
      </ul>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Navbar from '@/components/Navbar.vue'

const loggedIn = ref(false)

onMounted(() => {
  const token = localStorage.getItem('token') || sessionStorage.getItem('token')
  loggedIn.value = !!token
})

</script>

<style scoped>
.landing-page {
  background: linear-gradient(135deg, #181818 0%, #121020 100%);
  font-family: 'Arial', sans-serif;
  overflow-x: hidden;
  position: relative;
}

.hero-title {
  background: -webkit-linear-gradient(45deg, #6C63FF, #a9a5ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 2px 2px 15px rgba(0,0,0,0.3);
  padding: 10px;
}

.hero-subtitle {
  color: #aab2c2;
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

.footer {
  background: transparent;
  color: rgba(200, 200, 200, 0.7);
  font-size: 0.9rem;
}

.footer .nav a {
  color: rgba(200, 200, 200, 0.7);
  transition: color 0.3s ease;
}

.footer .nav a:hover {
  color: #6C63FF;
}
</style>
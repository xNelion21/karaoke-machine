<template>
  <div class="app-page">
    <nav class="navbar navbar-expand-lg navbar-dark sticky-top">
      <div class="container-fluid">
        <router-link to="/" class="navbar-brand mx-md-3">Singly</router-link>

        <div class="d-flex">
          <button class="btn btn-outline-light me-3">Wyloguj</button>
        </div>
      </div>
    </nav>

    <main class="app-main container mt-4">
      <SearchBar v-model="searchQuery" />

      <Player :song="currentSong" />
    </main>

    <footer class="footer d-flex flex-wrap justify-content-between align-items-center py-3 px-4 mt-auto">
      <div class="col-md-4 d-flex align-items-center">
        <span class="mb-3 mb-md-0 fw-bold">© 2025 Singly</span>
      </div>

      <ul class="nav col-md-4 justify-content-end list-unstyled d-flex">
        <li class="ms-3">
          <a href="https://x.com" target="_blank"><i class="bi bi-twitter-x"></i></a>
        </li>
        <li class="ms-3">
          <a href="https://instagram.com" target="_blank"><i class="bi bi-instagram"></i></a>
        </li>
        <li class="ms-3">
          <a href="https://facebook.com" target="_blank"><i class="bi bi-facebook"></i></a>
        </li>
      </ul>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import SearchBar from '@/components/SearchBar.vue'
import Player from '@/components/Player.vue'

const searchQuery = ref('')
const currentSong = ref({
  title: 'Przykładowa piosenka',
  artist: 'Artysta',
  videoUrl: '',
  lyrics: 'Tekst piosenki pojawi się tutaj...'
})
</script>

<style scoped>
.app-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: linear-gradient(135deg, #181818 0%, #121020 100%);
  color: #f0f0f0;
}

.navbar {
  background: rgba(24,24,24,0.6);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.navbar-brand { font-weight: bold; font-size: 1.5rem; color: #fff; }

.footer {
  background-color: rgba(17, 17, 19, 0.27);
  color: rgba(170, 178, 194, 0.71);
}

.footer .nav a {
  color: rgba(170, 178, 194, 0.75);
  font-size: 1rem;
  transition: color 0.3s ease;
}

.footer .nav a:hover { color: #6C63FF; }
</style>







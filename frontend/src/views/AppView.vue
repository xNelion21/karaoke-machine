<template>
  <div class="app-page">
    <Navbar />

    <div class="search-auth-row">
      <div class="search-wrapper">
        <SearchBar @song-selected="handleSongSelected" />
      </div>
      <AuthStatus />
    </div>

    <main class="app-main mt-4">
      <div class="main-grid">
        <aside class="left-block">
          <h2>Moja biblioteka</h2>
        </aside>

        <section class="center-block player-block">
          <Player :song-details="currentSongDetails" />
        </section>

        <aside class="right-block">
          <h2>Inne informacje</h2>
          <div v-if="currentSongDetails" class="song-metadata">
            <p><strong>ID Piosenki:</strong> {{ currentSongDetails.id }}</p>
          </div>
          <p v-else class="metadata-placeholder">Wybierz piosenkę, aby zobaczyć więcej.</p>
        </aside>
      </div>
    </main>

    <footer class="footer d-flex flex-wrap justify-content-between align-items-center py-3 px-4 mt-auto">
      <div class="col-md-4 d-flex align-items-center">
        <span class="mb-3 mb-md-0 fw-bold">© 2025 Singly</span>
      </div>

      <ul class="nav col-md-4 justify-content-end list-unstyled d-flex">
        <li class="ms-3"><a href="https://x.com" target="_blank"><i class="bi bi-twitter-x"></i></a></li>
        <li class="ms-3"><a href="https://instagram.com" target="_blank"><i class="bi bi-instagram"></i></a></li>
        <li class="ms-3"><a href="https://facebook.com" target="_blank"><i class="bi bi-facebook"></i></a></li>
      </ul>
    </footer>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";

import Navbar from '@/components/Navbar.vue'
import AuthStatus from '@/components/AuthStatus.vue'
import SearchBar from '@/components/SearchBar.vue'
import Player from '@/components/Player.vue'

const currentSongDetails = ref(null);
const handleSongSelected = async (songResponseDTO) => {

  const token = localStorage.getItem('token') ||
      sessionStorage.getItem('token')

  console.log("App.vue: Otrzymałem zdarzenie 'song-selected' z piosenką (SongResponseDTO):", songResponseDTO);
  if (songResponseDTO && songResponseDTO.id) {
    try {
      const response = await axios.get(`http://localhost:8080/api/songs/${songResponseDTO.id}`, {
        headers: { Authorization: `Bearer ${token}` }, }
      );
      currentSongDetails.value = response.data;
      console.log("App.vue: Pobrane SongDetailsDTO:", currentSongDetails.value);
    } catch (error) {
      console.error("App.vue: Błąd podczas pobierania szczegółów piosenki:", error);
      currentSongDetails.value = null;
    }
  } else {
    currentSongDetails.value = null;
  }
}
</script>

<style scoped>
.app-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: linear-gradient(135deg, #181818 0%, #121020 100%);
  color: #fff;
  font-family: 'Arial', sans-serif;
  overflow-x: hidden;
  position: relative;
}

.search-auth-row {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  position: relative;
  margin-bottom: 1rem;
}

.search-wrapper {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  width: 33%;
  margin-top: 45px;
}

.search-auth-row > :not(.search-wrapper) {
  margin-left: auto;
  margin-right: 2rem;
}

.app-main {
  flex: 1;
  width: 100%;
  padding: 0 20px;
}

.main-grid {
  display: grid;
  grid-template-columns: 1.5fr 3fr 2fr;
  gap: 2rem;
  min-height: calc(100vh - 80px - 60px - 90px);
  width: 100%;
}

.left-block, .right-block {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 1rem;
  padding: 1rem;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.center-block.player-block {
  background: transparent;
  padding: 0;
  display: block;
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

.song-metadata {
  margin-top: 1rem;
  font-size: 1.1rem;
  color: #bbb;
}
.metadata-placeholder {
  font-style: italic;
  color: #aaa;
  margin-top: 1rem;
}


@media (max-width: 992px) {
  .main-grid {
    grid-template-columns: 1fr;
    min-height: auto;
  }
  .search-wrapper {
    width: 80%;
    margin-top: 35px;
  }
}
</style>
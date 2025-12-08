<template>
  <div class="app-page">
    <Navbar />

    <div class="search-auth-row mb-5">
      <div class="search-wrapper">
        <SearchBar @song-selected="handleSongSelected" />
      </div>
    </div>

    <main class="app-main container-fluid">
      <div class="row main-row g-4">

        <aside class="col-lg-3 col-md-12 left-block order-2 order-lg-1">
          <div class="section-content-centered">
            <h2 class="h5 sidebar-title">
              <i class="bi bi-heart-fill heart me-2 text-accent"></i> Ulubione
            </h2>

            <div v-if="songsStore.favoriteSongsList.length > 0" class="mt-4 w-100 sidebar-content-scroll">
              <ul class="list-unstyled sidebar-list">
                <li
                    v-for="song in songsStore.favoriteSongsList"
                    :key="song.id"
                    @click="handleSongSelected(song)"
                    class="sidebar-song-item"
                >
                  <span class="fw-bold song-title">{{ song.title }}</span>
                  <small class="text-muted d-block song-artist">{{ song.artist }}</small>
                </li>
              </ul>
            </div>
            <div v-else class="text-light mt-4 py-5">
              <i class="bi bi-bookmark-heart display-6 mb-3 text-light"></i>
              <p class="m-3">Brak ulubionych utworów.</p>
            </div>
          </div>
        </aside>

        <section class="col-lg-6 col-md-12 center-block player-block order-1 order-lg-2">
          <div v-if="!songsStore.currentSong && isLoadingDetails" class="text-center mt-5">
            <div class="spinner-border text-primary" role="status"></div>
          </div>
          <Player v-else :songDetails="songsStore.currentSong" />
        </section>

        <aside class="col-lg-3 col-md-12 right-block order-3">
          <RelatedSongs
              :current-song="songsStore.currentSong"
              @select-song="handleSongSelected"
          />
        </aside>

      </div>
    </main>

    <footer class="footer mt-auto py-3 text-center">
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useSongsStore } from '@/stores/songs.js'
import Navbar from '@/components/Navbar.vue'
import SearchBar from '@/components/SearchBar.vue'
import Player from '@/components/Player.vue'
import RelatedSongs from '@/components/RelatedSongs.vue'

const songsStore = useSongsStore()
const isLoadingDetails = ref(false)

onMounted(() => {
  songsStore.fetchSongs()
})

async function handleSongSelected(song) {
  songsStore.clearSearch()

  isLoadingDetails.value = true;
  await songsStore.fetchSongDetails(song.id);
  isLoadingDetails.value = false;

  if (window.innerWidth < 992) {
    window.scrollTo({ top: 0, behavior: 'smooth' });
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
}

.search-wrapper {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  width: 40%;
  max-width: 650px;
  margin-top: 45px;
  z-index: 1000;
}

.app-main {
  flex: 1;
  width: 100%;
  padding: 0;
  margin-top: 25px;
}

.app-main .row {
  margin-left: 0;
  margin-right: 0;
  width: 100%;
  padding: 0 40px;
}

.left-block, .right-block {
  background: rgba(30, 30, 35, 0.7);
  backdrop-filter: blur(5px);
  border-radius: 1rem;
  padding: 1.5rem;
  height: 100%;
  min-height: calc(100vh - 220px);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.left-block h2, .right-block h2 {
  font-size: 1.5rem;
  font-weight: bold;
  color: #6C63FF;
  margin-bottom: 1rem;
}

.section-content-centered {
  width: 100%;
}

.center-block.player-block {
  background: transparent;
  display: block;
}

.footer {
  background: transparent;
  color: rgba(200, 200, 200, 0.7);
  font-size: 0.9rem;
}

.text-accent {
  color: #9d96ff !important;
}

.sidebar-list {
  padding: 0;
  list-style: none;
  position: relative;
  z-index: 10;
}

.sidebar-song-item {
  display: flex;
  flex-direction: column;
  padding: 10px 15px;
  margin-bottom: 10px;
  border-radius: 8px;
  cursor: pointer !important;
  transition: background-color 0.2s, transform 0.2s;
  background: rgba(0, 0, 0, 0.15);
  width: 100%;
  align-items: center;
  text-align: center;
}

.sidebar-song-item:hover {
  background-color: rgba(108, 99, 255, 0.2);
  color: #fff;
  transform: translateY(5px);
}

.sidebar-content-scroll {
  position: relative;
  z-index: 5;
  max-height: calc(100vh - 350px);
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

@media (min-width: 992px) {
  .main-row {
    min-height: calc(100vh - 220px);
  }
}

@media (max-width: 991px) {
  .search-wrapper {
    width: 90%;
    position: relative;
    left: 0;
    transform: none;
    margin-top: 20px;
  }

  .app-main .row {
    padding: 0 15px;
  }

  .left-block, .right-block {
    min-height: auto;
    margin-bottom: 20px;
    padding: 1.5rem;
  }

  .sidebar-song-item {
    padding: 15px 10px;
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
  }

  .sidebar-song-item:hover {
    transform: none;
    background: rgba(108, 99, 255, 0.3);
  }

  .song-title {
    font-size: 1.2rem;
  }

  .song-artist {
    font-size: 1rem;
    margin-top: 4px;
  }

  .sidebar-title {
    font-size: 1.8rem;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .sidebar-content-scroll {
    max-height: 400px;
  }
}
</style>
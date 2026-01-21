<template>
  <div class="app-page">
    <Navbar />

    <div class="search-auth-row mb-3 ">
      <div class="search-wrapper">
        <SearchBar @song-selected="handleSongSelected" />
      </div>
    </div>

    <main class="app-main container-fluid">
      <div class="row main-row g-4">

        <aside class="col-lg-3 col-md-12 left-block order-2 order-lg-1">
          <div class="section-content-centered">
            <h2 class="h5 sidebar-title">
              <i class="bi bi-heart-fill heart me-2 text-accent"></i> {{ $t('app.favorites') }}
            </h2>

            <div v-if="favoriteStore.favoritesList.length > 0" class="mt-4 w-100 sidebar-content-scroll">

              <TransitionGroup
                  name="list"
                  tag="ul"
                  class="list-unstyled sidebar-list"
              >
                <li
                    v-for="song in favoriteStore.favoritesList"
                    :key="song.id || song.videoId"
                    @click="handleSongSelected(song)"
                    class="sidebar-song-item"
                >
                  <img
                      :src="song.coverUrl"
                      alt="Cover"
                      class="song-thumb me-3"
                  />

                  <div class="song-text">
                    <span class="fw-bold song-title">{{ song.title }}</span>
                    <small class="text-light d-block song-artist">{{ song.artist }}</small>
                  </div>
                </li>
              </TransitionGroup>
            </div>

            <div v-else class="text-light mt-4 py-5">
              <i class="bi bi-bookmark-heart display-6 mb-3 text-light"></i>
              <p class="m-3">{{ $t('app.no_favorites') }}</p>
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
import { useFavoritesStore } from "@/stores/favorites.js";
import { useAuthStore } from "@/stores/auth.js";
import Navbar from '@/components/Navbar.vue'
import SearchBar from '@/components/SearchBar.vue'
import Player from '@/components/Player.vue'
import RelatedSongs from '@/components/RelatedSongs.vue'

const songsStore = useSongsStore()
const favoriteStore = useFavoritesStore()
const authStore = useAuthStore()

const isLoadingDetails = ref(false)

onMounted(async () => {
  if (!songsStore.songs?.length) {
    await songsStore.fetchSongs()
  }
  if (authStore.isAuthenticated) {
    await favoriteStore.fetchFavorites()
  }
})

async function handleSongSelected(song) {
  songsStore.clearSearch()

  isLoadingDetails.value = true;
  await songsStore.fetchSongDetails(song);
  isLoadingDetails.value = false;

}

function handleSongSelection(song) {
  songsStore.currentSong = song;
}

</script>

<style scoped>

@keyframes gradient-move {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.app-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: linear-gradient(-45deg, #121020, #1c1c4a, #1a1a1a, #0f0c29);
  background-size: 400% 400%;
  animation: gradient-move 40s ease infinite;
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
  position: relative;
  width: 40%;
  max-width: 650px;
  z-index: 1000;
}

.app-main {
  flex: 1;
  width: 100%;
  padding: 0;

}

.app-main .row {
  margin-left: 0;
  margin-right: 0;
  width: 100%;
  padding: 0 40px;
}

.left-block, .right-block {
  background: rgba(30, 34, 42, 0.65);
  backdrop-filter: blur(16px);
  border-radius: 1rem;
  padding: 1.5rem;
  height: 100%;
  max-height: calc(100vh - 200px);
  min-height: calc(100vh - 200px);

  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.left-block h2, .right-block h2 {
  font-size: 1.5rem;
  font-weight: bold;
  color: #6C63FF;
  margin-bottom: 1rem;
  flex-shrink: 0;
}

.section-content-centered {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-content-scroll {
  width: 100%;
  flex: 1;
  overflow-y: auto;
  min-height: 0;
  padding-right: 5px;
  margin-top: 1.5rem !important;
  scrollbar-width: none;
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
  flex-direction: row;
  padding: 10px 15px;
  margin-bottom: 10px;
  border-radius: 8px;
  cursor: pointer !important;
  transition: background-color 0.2s, transform 0.2s;
  background: rgba(0, 0, 0, 0.15);
  width: 100%;
  align-items: center;
  text-align: left;
}

.song-thumb {
  width: 80px;
  height: 45px;
  object-fit: cover;
  border-radius: 6px;
  flex-shrink: 0;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.sidebar-song-item:hover .song-thumb {
  transform: scale(1.05);
  box-shadow: 0 0 15px rgba(108, 99, 255, 0.3);
  border-color: rgba(108, 99, 255, 0.5);
}

.song-text {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-song-item:hover {
  background-color: rgba(108, 99, 255, 0.2);
  color: #fff;
  transform: translateY(2px);
}

.song-title {
  font-size: 0.95rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.song-artist {
  font-size: 0.8rem;
  opacity: 0.7;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}


.sidebar-list {
  position: relative;
  padding: 0;
  list-style: none;
  z-index: 10;
  overflow: visible;
}

.list-enter-active,
.list-leave-active {
  transition: all 0.5s cubic-bezier(0.55, 0, 0.1, 1);
}

.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

.list-move {
  transition: transform 0.5s cubic-bezier(0.55, 0, 0.1, 1);
}

.list-leave-active {
  position: absolute;
  width: 100%;
  left: 0;
  top: auto;
  z-index: 0;
  pointer-events: none;
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
    height: auto;
    max-height: none;
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
    flex: none;
    max-height: 400px;
  }

  .sidebar-content-scroll::-webkit-scrollbar {
    display: none;
  }
}
</style>
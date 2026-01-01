<template>
  <div class="related-songs-list mt-3">
    <h3 class="section-header h6 text-uppercase fw-bold mb-3 d-flex align-items-center text-muted-light spacing-header">
      <i class="bi bi-cloud-download me-2 text-accent glow-icon"></i>
      {{ $t('app.related') || 'Z sieci' }}

      <span v-if="searchQuery" class="search-badge ms-auto">
        <i class="bi bi-search me-1"></i> {{ searchQuery }}
      </span>
    </h3>

    <div v-if="songsStore.isRelatedLoading" class="text-center py-5">
      <div class="spinner-border text-accent" role="status"></div>
      <p class="mt-2 text-light small">{{  $t('app.loading') }}</p>
    </div>

    <div v-else-if="songsStore.relatedSongs.length > 0" class="related-scroll-area">
      <ul class="list-unstyled mb-0 list-container">
        <li
            v-for="song in songsStore.relatedSongsNormalized"
            :key="song.videoId || song.id"
            class="related-song-item"
            @click="playInternal(song)"
        >
          <div class="item-grid" v-if="normalizeSong(song)">
            <div class="thumb-wrapper">
              <img
                  :src="normalizeSong(song).coverUrl"
                  alt="cover"
                  class="yt-thumb"
                  loading="lazy"
              />
              <div class="overlay">
                <i class="bi bi-play-fill"></i>
              </div>
            </div>

            <div class="song-info">
              <p class="mb-0 fw-bold song-title">
                {{ normalizeSong(song).title }}
              </p>
            </div>

            <div class="play-btn-wrapper">
              <i class="bi bi-play-circle-fill play-icon"></i>
            </div>
          </div>
        </li>
      </ul>
    </div>

    <div v-else class="empty-state text-center py-5">
      <i class="bi bi-music-note-list display-6 mb-3 text-muted-dark d-block"></i>
      <p class="mb-0 text-muted-light">{{  $t('app.no_results') }} <b class="text-accent">{{ searchQuery }}</b></p>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useSongsStore } from '@/stores/songs';
import { normalizeSong } from '@/utils/songUtils';

const props = defineProps({
  currentSong: { type: Object, default: null }
});

const emit = defineEmits(['select-song']);
const songsStore = useSongsStore();
const searchQuery = ref('');

const playInternal = (rawSong) => {
  const song = normalizeSong(rawSong);
  song.isOnline = true;
  song.source = 'youtube';

  emit('select-song', song);
};

watch(() => props.currentSong, async (newSong) => {
  if (newSong) {
    const safeSong = normalizeSong(newSong);
    const artistToSearch = safeSong.artist !== 'Nieznany wykonawca' ? safeSong.artist : safeSong.title;

    searchQuery.value = artistToSearch;

    if (artistToSearch) {
      await songsStore.fetchRelatedSongs(artistToSearch);
    }
  } else {
    songsStore.relatedSongs = [];
    searchQuery.value = '';
  }
}, { immediate: true, deep: true });
</script>

<style scoped>
.text-accent { color: #9d96ff !important; }
.text-muted-light { color: #ccc; }
.text-muted-dark { color: #888; }

.spacing-header {
  font-size: 0.9rem;
  letter-spacing: 1px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding-bottom: 15px;
  margin-bottom: 20px;
}

.glow-icon {
  text-shadow: 0 0 10px rgba(157, 150, 255, 0.4);
}

.search-badge {
  font-size: 0.7rem;
  padding: 4px 10px;
  max-width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  background: rgba(108, 99, 255, 0.15);
  color: #9d96ff;
  border: 1px solid rgba(108, 99, 255, 0.3);
  border-radius: 20px;
}

.related-songs-list {
  width: 100%;
  height: 100%;        /* Zajmuje 100% wysokości rodzica (right-block) */
  display: flex;       /* Flexbox */
  flex-direction: column; /* Układ pionowy */
  overflow: hidden;    /* Zapobiega rozpychaniu kontenera */
}

/* --- OBSZAR PRZEWIJANIA (tak jak w AppView .sidebar-content-scroll) --- */

.related-scroll-area {
  width: 100%;
  flex: 1;             /* Zajmuje całe dostępne miejsce poniżej nagłówka */
  overflow-y: auto;    /* Włącza przewijanie */
  min-height: 0;       /* Ważne dla Flexboxa */
  padding-right: 5px;

  /* Ukrywanie paska (Firefox) */
  scrollbar-width: none;
  -ms-overflow-style: none;
}

/* Ukrywanie paska (Chrome, Safari, Edge) */
.related-scroll-area::-webkit-scrollbar {
  display: none;
  width: 0;
  height: 0;
}

.related-song-item {
  margin-bottom: 12px;
  cursor: pointer;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.2s ease-in-out;
  overflow: hidden;
}

.item-grid {
  display: grid;
  grid-template-columns: 70px 1fr 40px;
  gap: 15px;
  align-items: center;
  padding: 10px;
}

.related-song-item:hover {
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.08) 0%, rgba(255, 255, 255, 0.03) 100%);
  border-color: rgba(108, 99, 255, 0.4);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.thumb-wrapper {
  position: relative;
  width: 70px;
  height: 50px;
  border-radius: 6px;
  overflow: hidden;
}

.yt-thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.overlay i { color: #fff; font-size: 1.2rem; }

.related-song-item:hover .yt-thumb { transform: scale(1.1); }
.related-song-item:hover .overlay { opacity: 1; }

.song-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow: hidden;
}

.song-title {
  font-size: 0.9rem;
  font-weight: 600;
  line-height: 1.3;
  color: #f0f0f0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: left;
}

.play-btn-wrapper { display: flex; justify-content: flex-end; }
.play-icon {
  font-size: 1.8rem;
  color: rgba(255, 255, 255, 0.2);
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.related-song-item:hover .play-icon {
  color: #9d96ff;
  transform: scale(1.1);
  text-shadow: 0 0 8px rgba(157, 150, 255, 0.6);
}

@media (max-width: 991px) {
  .list-container { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
  .related-song-item { margin-bottom: 0; }

  .related-scroll-area {
    flex: none;
    max-height: 400px;
  }

  .related-scroll-area::-webkit-scrollbar {
    display: none;
  }
}

@media (max-width: 576px) {
  .list-container { display: block; }
  .related-song-item { margin-bottom: 10px; }
  .related-scroll-area { max-height: 350px; }
}
</style>
<template>
  <div class="related-songs-list">
    <h3 class="h5 fw-bold mb-4 d-flex align-items-center justify-content-center section-header">
      <i class="bi bi-shuffle me-2 text-primary fs-4 icon-mobile"></i>
      Powiązane utwory
      <span v-if="genre" class="badge ms-2">{{ genre }}</span>
    </h3>

    <div v-if="songsStore.isRelatedLoading" class="text-center text-muted py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Ładowanie...</span>
      </div>
      <p class="mt-2">Ładowanie rekomendacji...</p>
    </div>

    <div v-else-if="!genre" class="text-light text-center py-5">
      <i class="bi bi-music-note-list display-6 mb-3 text-light"></i>
      <p class="m-3">Wybierz piosenkę, aby zobaczyć powiązane utwory.</p>
    </div>

    <ul v-else-if="songsStore.relatedSongs.length > 0" class="list-unstyled">
      <li
          v-for="song in songsStore.relatedSongs"
          :key="song.id"
          class="related-song-item p-3 p-md-4 mb-3 rounded d-flex justify-content-between align-items-center"
          @click="$emit('select-song', song)"
      >
        <div class="song-info d-flex flex-column">
          <p class="mb-0 fw-bold fs-6 text-truncate song-title">{{ song.title }}</p>
          <small class="text-muted-light song-artist">{{ song.artist }}</small>
        </div>
        <i class="bi bi-play-circle-fill fs-3 text-primary-light ms-3 play-icon"></i>
      </li>
    </ul>

    <div v-else class="text-light text-center py-5">
      <i class="bi bi-box-seam-fill display-6 mb-4 text-light"></i>
      <p class="m-4">Nie znaleziono więcej utworów z gatunku <b>{{ genre }}</b>.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useSongsStore } from '@/stores/songs';

const props = defineProps({
  currentSong: {
    type: Object,
    default: null
  },
  genre: {
    type: String,
    default: ''
  }
});

defineEmits(['select-song']);

const songsStore = useSongsStore();
const genre = ref('');

watch(() => props.currentSong, (newSong) => {
  if (newSong) {
    const categories = newSong.categories || newSong.song?.categories;
    const currentSongId = newSong.id || newSong.song?.id;
    const currentCategory = categories && categories.length > 0 ? categories[0] : null;

    if (currentCategory && currentSongId) {
      genre.value = currentCategory;
      songsStore.fetchRelatedSongs(currentCategory, currentSongId);
    } else {
      songsStore.relatedSongs = [];
      genre.value = '';
    }
  } else {
    songsStore.relatedSongs = [];
    genre.value = '';
  }
}, { immediate: true });
</script>

<style scoped>

.related-songs-list { color: #E0E0E0; }
.text-primary { color: #6C63FF !important; }
.text-primary-light { color: #9d96ff !important; }
.text-muted-light { color: #ccc; font-size: 0.9rem; }

.related-song-item {
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: background-color 0.3s, transform 0.3s, border-color 0.3s;
  cursor: pointer;
  position: relative;
}

.related-song-item:hover {
  background: rgba(108, 99, 255, 0.2);
  border-color: #6C63FF;
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(108, 99, 255, 0.2);
}

.badge {
  background-color: #6C63FF;
  color: white;
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 0.75em;
  white-space: nowrap;
}

@media (max-width: 991px) {
  .section-header {
    font-size: 1.8rem;
    margin-top: 1.5rem;
  }

  .related-song-item {
    padding: 20px !important;
  }

  .song-title {
    font-size: 1.2rem !important;
    white-space: normal !important;
  }

  .song-artist {
    font-size: 1rem;
    margin-top: 5px;
  }

  .play-icon {
    font-size: 2.5rem !important;
  }

  .badge {
    font-size: 0.9rem;
    padding: 6px 12px;
  }
}
</style>
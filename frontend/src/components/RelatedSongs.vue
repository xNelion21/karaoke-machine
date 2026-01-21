<template>
  <div class="section-content-centered">
    <h2 class="h5 sidebar-title">
      <i class="bi bi-shuffle me-2 text-accent"></i>
      {{ $t('app.related') }}
      <span v-if="category" class="badge-genre ms-2">{{ category }}</span>
    </h2>

    <div v-if="songsStore.isRelatedLoading" class="text-center mt-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else-if="songsStore.relatedSongs.length > 0" class="mt-4 w-100 sidebar-content-scroll">
      <ul class="list-unstyled sidebar-list">
        <li
            v-for="song in songsStore.relatedSongs"
            :key="song.id || song.videoId"
            @click="$emit('select-song', song)"
            class="sidebar-song-item"
        >
          <div class="song-icon-placeholder me-3">
            <i class="bi bi-music-note-beamed"></i>
          </div>

          <div class="song-text">
            <span class="fw-bold song-title">{{ song.title || song.song?.title }}</span>
            <small class="text-light d-block song-artist">
              {{ getArtistName(song) }}
            </small>
          </div>

          <div class="ms-auto play-hint">
            <i class="bi bi-play-fill"></i>
          </div>
        </li>
      </ul>
    </div>

    <div v-else class="text-light mt-4 py-5 text-center">
      <i class="bi bi-music-note-list display-6 mb-3 text-light"></i>
      <p class="m-3 text-light">{{ $t('app.no_related') }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useSongsStore } from '@/stores/songs';

const props = defineProps({
  currentSong: { type: Object, default: null }
});

defineEmits(['select-song']);

const songsStore = useSongsStore();
const category = ref('');

const getArtistName = (song) => {
  const data = song.song || song;
  if (Array.isArray(data.authors)) return data.authors.join(', ');
  return data.artist || 'Nieznany wykonawca';
};

watch(() => props.currentSong, (newSong) => {
  if (newSong) {
    const currentId = newSong.id || newSong.song?.id || newSong.videoId || newSong.song?.videoId;
    const categories = newSong.categories || newSong.song?.categories || [];

    const currentCategory = categories.length > 0 ? categories[0] : null;

    genre.value = currentCategory || '';

    if (currentCategory) {
      songsStore.fetchRelatedSongs(currentCategory, currentId);
    } else {
      if (currentId) {
        songsStore.fetchRelatedSongs(null, currentId);
      }
    }
  }
}, { immediate: true, deep: true });
</script>

<style scoped>

.section-content-centered {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-title {
  font-size: 1.5rem;
  font-weight: bold;
  color: #6C63FF;
  margin-bottom: 1rem;
  flex-shrink: 0;
}

.text-accent { color: #9d96ff !important; }

.badge-genre {
  font-size: 0.7rem;
  background: rgba(108, 99, 255, 0.2);
  padding: 4px 10px;
  border-radius: 10px;
  color: #9d96ff;
  border: 1px solid rgba(108, 99, 255, 0.3);
  vertical-align: middle;
}

.sidebar-content-scroll {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
  padding-right: 5px;
  scrollbar-width: none;
}
.sidebar-content-scroll::-webkit-scrollbar { display: none; }

.sidebar-list {
  padding: 0;
  list-style: none;
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

.sidebar-song-item:hover {
  background-color: rgba(108, 99, 255, 0.2);
  transform: translateY(2px);
}

.song-icon-placeholder {
  width: 45px;
  height: 45px;
  background: linear-gradient(135deg, #6C63FF 0%, #3a3399 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.4rem;
  flex-shrink: 0;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.song-text {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.song-title {
  font-size: 0.95rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #fff;
}

.song-artist {
  font-size: 0.8rem;
  opacity: 0.7;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #ddd;
}

.play-hint {
  color: #9d96ff;
  opacity: 0;
  transition: opacity 0.2s;
}

.sidebar-song-item:hover .play-hint {
  opacity: 1;
}
</style>
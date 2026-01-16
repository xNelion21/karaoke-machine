<template>
  <div v-if="songDetails" class="karaoke-player-container">
    <div class="header-info">
      <h3>{{ displayTitle }}</h3>

      <button
          class="btn-favorite ms-3"
          @click="handleToggleFavorite"
          :class="{ 'is-favorite': isFav }"
          aria-label="Dodaj do ulubionych"
      >
        <i :class="isFav ? 'bi bi-heart-fill' : 'bi bi-heart'"></i>
      </button>
      <button
          class="btn-suggest-icon ms-2"
          @click="showSuggestModal = true"
          aria-label="Zgłoś zmianę"
          title="Zgłoś zmianę"
      >
        <i class="bi bi-pencil-square"></i>
      </button>





      <div class="meta-tags mb-2">
        <span v-if="displayArtist" class="fw-bold tag artist">
          <i class="bi bi-mic-fill"></i> {{ displayArtist }}
        </span>

        <template v-if="songDetails.song?.categories || songDetails.categories">
          <span
              v-for="(category, index) in (songDetails.song?.categories || songDetails.categories)"
              :key="index"
              class="tag category"
          >
          <i class="bi bi-music-note-beamed"></i> {{ category }}
          </span>
        </template>
      </div>
    </div>

    <div class="video-player-wrapper" v-if="videoId">
      <div ref="youtubeTarget" class="youtube-target"></div>
    </div>


    <div class="lyrics-display">

      <div v-if="availableVersions.length > 1" class="version-control-wrapper">
        <button @click="tryNextVersion" class="btn-version">
        <span class="version-counter">
          {{ currentVersionIndex + 1 }} / {{ availableVersions.length }}
        </span>
          <span class="version-label">{{ $t ('player.diff_text')}}</span>
          <i class="bi bi-arrow-repeat icon-refresh"></i>
        </button>
      </div>

      <div v-if="isLyricsLoading && sortedLyricLines.length === 0" class="fallback-lyrics loading">
        <i class="bi bi-arrow-repeat spin-animation"></i>
        <p class="mt-3">{{  $t('app.loading') }}</p>
      </div>

      <div v-if="sortedLyricLines && sortedLyricLines.length > 0" class="teleprompter-container" ref="teleprompterRef">
        <div class="teleprompter-content">
          <p
              v-for="(line, index) in sortedLyricLines"
              :key="line.id || index"
              :class="{
                'line': true,
                'active': activeLineIndex === index,
                'past': index < activeLineIndex,
                'future': index > activeLineIndex && index > (activeLineIndex)
              }"
              @click="seekTo(line.timeStampStart)"
              :ref="el => { if (activeLineIndex === index) activeLineRef = el }"
          >
            {{ line.text }}
          </p>
        </div>
      </div>

      <div v-else-if="songDetails.song?.lyrics || songDetails.lyrics" class="fallback-lyrics">
        <pre>{{ songDetails.song?.lyrics || songDetails.lyrics }}</pre>
      </div>

      <div v-else-if="!isLyricsLoading" class="fallback-lyrics empty">
        <i class="bi bi-music-note-list"></i>
        <p>{{ $t('player.no_text') }}</p>
      </div>
    </div>

  </div>
  <div v-else class="no-song-selected">
    <i class="bi bi-disc spin-animation"></i>
    <p>{{ $t('player.select') }}</p>
  </div>
  <SuggestChange
      v-if="showSuggestModal"
      :song-id="currentSongId"
      @close="showSuggestModal = false"
      @submitted="showSuggestModal = false"
  />

</template>

<script setup>
import { ref, computed, watch, onUnmounted, nextTick } from 'vue';
import { useFavoritesStore } from '@/stores/favorites';
import { fetchSyncedLyrics } from '@/services/lyricsService';
import { parseLRC } from '@/utils/lrcParser';
import { normalizeSong, extractYoutubeId } from '@/utils/songUtils';
import SuggestChange from '@/components/SuggestChange.vue'


const emit = defineEmits(['favorite-toggled']);

const props = defineProps({
  songDetails: {
    type: Object,
    default: null,
  },
});

const favoritesStore = useFavoritesStore();

const player = ref(null);
const currentTime = ref(0);
const youtubeTarget = ref(null);
const teleprompterRef = ref(null);
const activeLineRef = ref(null);
let playbackInterval = null;

const fetchedLyricLines = ref([]);
const isLyricsLoading = ref(true);

const availableVersions = ref([]);
const currentVersionIndex = ref(0);

const showSuggestModal = ref(false)


const currentSongId = computed(() => props.songDetails?.id || props.songDetails?.song?.id);

const currentVideoId = computed(() => {
  const song = props.songDetails?.song || props.songDetails;
  if (!song) return null;
  if (song.videoId) return song.videoId;
  return extractYoutubeId(song.youtubeUrl || song.videoUrl);
});

const isFav = computed(() => {
  if (currentSongId.value && favoritesStore.isFavorite(currentSongId.value)) return true;
  if (currentVideoId.value && favoritesStore.favoriteList) {
    return favoritesStore.favoriteList.some(s => s.videoId === currentVideoId.value);
  }
  return false;
});

const normalizedDetails = computed(() => normalizeSong(props.songDetails));

const uniqueSongKey = computed(() => {
  const details = props.songDetails;
  if (!details) return null;

  const dbId = details.id || details.song?.id;
  if (dbId) return dbId;

  return currentVideoId.value;
});

const displayArtist = computed(() => normalizedDetails.value?.artist || '');
const displayTitle = computed(() => normalizedDetails.value?.title || '');

const sortedLyricLines = computed(() => {
  let lines = props.songDetails?.lyricLines || props.songDetails?.song?.lyricLines;

  if (!lines || lines.length === 0) {
    lines = fetchedLyricLines.value;
  }

  return (lines && lines.length > 0)
      ? [...lines].sort((a, b) => a.timeStampStart - b.timeStampStart)
      : [];
});

const videoId = computed(() => currentVideoId.value);

const activeLineIndex = computed(() => {
  if (!sortedLyricLines.value.length) return -1;
  const index = sortedLyricLines.value.findIndex(line =>
      currentTime.value >= line.timeStampStart && currentTime.value < line.timeStampEnd
  );
  if (index === -1 && currentTime.value > 0) {
    return sortedLyricLines.value.findLastIndex(line => line.timeStampEnd <= currentTime.value);
  }
  return index;
});

function handleToggleFavorite() {
  if (props.songDetails) {
    favoritesStore.toggleFavorite(props.songDetails);
    emit('favorite-toggled');
  }
}

const loadLyrics = async (durationOverride = null) => {
  const song = props.songDetails;
  if (!song) {
    isLyricsLoading.value = false;
    return;
  }

  const targetKey = uniqueSongKey.value;

  const existingLines = song.lyricLines || song.song?.lyricLines;
  if (existingLines?.length > 0) {
    isLyricsLoading.value = false;
    return;
  }

  const rawLrc = song.lyrics || song.song?.lyrics;
  if (rawLrc?.includes('[')) {
    fetchedLyricLines.value = mapParsedToPlayerFormat(parseLRC(rawLrc));
    isLyricsLoading.value = false;
    return;
  }

  if (fetchedLyricLines.value.length === 0) {
    isLyricsLoading.value = true;
  }

  try {
    const normalized = normalizeSong(song);
    const { artist, title } = normalized;
    const searchDuration = durationOverride || normalized.duration;

    if (!searchDuration) {
      return;
    }

    let results = await fetchSyncedLyrics(artist, title, searchDuration);

    if (uniqueSongKey.value !== targetKey) {
      console.log(`[LoadLyrics] Odrzucono wyniki - zmiana piosenki (Target: ${targetKey}, Current: ${uniqueSongKey.value})`);
      return;
    }

    if (!Array.isArray(results)) {
      results = results ? [results] : [];
    }

    const sortedCandidates = results.sort((a, b) => {
      const aLen = (a.syncedLyrics || "").length;
      const bLen = (b.syncedLyrics || "").length;
      return bLen - aLen;
    });

    if (sortedCandidates.length > 0) {
      availableVersions.value = sortedCandidates;
      if (currentVersionIndex.value >= sortedCandidates.length) currentVersionIndex.value = 0;
      applyLyricsVersion(currentVersionIndex.value);
    }

  } catch (e) {
    console.error("Błąd LoadLyrics:", e);
    if (uniqueSongKey.value === targetKey) {
      availableVersions.value = [];
    }
  } finally {
    if (uniqueSongKey.value === targetKey) {
      isLyricsLoading.value = false;
    }
  }
};

const applyLyricsVersion = (index) => {
  if (!availableVersions.value[index]) return;
  const selected = availableVersions.value[index];

  const content = selected.syncedLyrics || selected.lyrics;
  if (content) {
    fetchedLyricLines.value = mapParsedToPlayerFormat(parseLRC(content));
  }
};

const tryNextVersion = () => {
  const next = currentVersionIndex.value + 1;
  const target = next < availableVersions.value.length ? next : 0;
  currentVersionIndex.value = target;
  applyLyricsVersion(target);
};

function mapParsedToPlayerFormat(parsedLines) {
  return parsedLines.map((line, index, arr) => ({
    text: line.text,
    timeStampStart: line.time,
    timeStampEnd: arr[index + 1] ? arr[index + 1].time : line.time + 5
  }));
}


const scrollToActiveLine = (behavior = 'smooth') => {
  if (activeLineRef.value && teleprompterRef.value) {
    activeLineRef.value.scrollIntoView({ behavior, block: 'center' });
  }
};

const startPlaybackTimer = () => {
  if (playbackInterval) clearInterval(playbackInterval);
  playbackInterval = setInterval(() => {
    if (player.value?.getCurrentTime) {
      currentTime.value = player.value.getCurrentTime();
      scrollToActiveLine();
    }
  }, 100);
};

const stopPlaybackTimer = () => {
  if (playbackInterval) {
    clearInterval(playbackInterval);
    playbackInterval = null;
  }
};

const loadYouTubeAPI = () => {
  return new Promise(resolve => {
    if (window.YT?.Player) return resolve();
    if (!document.querySelector('script[src="https://www.youtube.com/iframe_api"]')) {
      const tag = document.createElement('script');
      tag.src = 'https://www.youtube.com/iframe_api';
      document.head.appendChild(tag);
    }
    const check = setInterval(() => {
      if (window.YT?.Player) { clearInterval(check); resolve(); }
    }, 100);
  });
};

const initPlayer = async () => {
  if (player.value) { try { player.value.destroy(); } catch (e) {} player.value = null; }
  if (!videoId.value) { stopPlaybackTimer(); currentTime.value = 0; return; }

  await loadYouTubeAPI();
  await nextTick();
  if (!youtubeTarget.value) return;

  player.value = new window.YT.Player(youtubeTarget.value, {
    height: '100%', width: '100%', videoId: videoId.value,
    playerVars: { 'autoplay': 1, 'playsinline': 1, 'origin': window.location.origin },
    events: {
      'onReady': (event) => {
        scrollToActiveLine('auto');
        event.target.playVideo();
        startPlaybackTimer();

        const dur = event.target.getDuration();

        if (dur > 0 && fetchedLyricLines.value.length === 0) {
          loadLyrics(dur);
        }
      },
      'onStateChange': (e) => e.data === window.YT.PlayerState.PLAYING ? startPlaybackTimer() : stopPlaybackTimer()
    }
  });
};

const seekTo = (time) => player.value?.seekTo(time, true);


watch(videoId, async (newId) => {
  if (newId) { await nextTick(); initPlayer(); }
  else { stopPlaybackTimer(); currentTime.value = 0; if(player.value) player.value.destroy(); }
}, { immediate: true });

watch(uniqueSongKey, async (newKey, oldKey) => {
  if (newKey !== oldKey) {
    console.log(`[Watcher] Zmiana utworu (Key): ${oldKey} -> ${newKey}`);

    isLyricsLoading.value = true;
    fetchedLyricLines.value = [];
    availableVersions.value = [];
    currentVersionIndex.value = 0;

    if (!videoId.value) {
      stopPlaybackTimer();
      currentTime.value = 0;
    }
  }

  if (newKey) {
    const details = props.songDetails;
    const hasLocalLyrics = details?.lyrics || details?.song?.lyrics || (details?.lyricLines && details.lyricLines.length > 0);
    const hasKnownDuration = details?.duration || details?.song?.duration;

    if (hasLocalLyrics || hasKnownDuration) {
      await loadLyrics();
    } else {
      console.log('[Watcher] Brak czasu trwania/tekstu lokalnego. Czekam na YouTube...');
      isLyricsLoading.value = true;
    }
  }
}, { immediate: true });

onUnmounted(() => {
  stopPlaybackTimer();
  if (player.value) try { player.value.destroy(); } catch (e) {}
});
</script>

<style scoped>
.karaoke-player-container {
  background: rgba(30, 34, 42, 0.65);
  backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37);

  min-height: 600px;

  display: flex;
  flex-direction: column;
  justify-content: flex-start;

  position: relative;
  z-index: 1;
}

.header-info {
  text-align: center;
}

.header-info h3 {
  font-size: 2.2rem;
  margin: 0 0 0.7rem 0;
  background: linear-gradient(90deg, #fff, #aab2c2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 900;
  letter-spacing: -0.5px;
}

.meta-tags {
  display: flex;
  justify-content: center;
  gap: 15px;
  flex-wrap: wrap;
  margin-top: 6px;
}

.tag {
  background: rgba(255, 255, 255, 0.15);
  padding: 6px 15px;
  border-radius: 25px;
  font-size: 0.95rem;
  color: #e0e0e0;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.tag.artist {
  color: #8a7aff;
  background: rgba(138, 122, 255, 0.15);
}

.tag.category {
  color: #ff85a2;
  background: rgba(255, 133, 162, 0.15);
}

.video-player-wrapper {
  width: 100%;
  aspect-ratio: 16 / 9;
  border-radius: 15px;
  overflow: hidden;
  background: #000;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.6);
  border: 2px solid rgba(255, 255, 255, 0.08);
  position: relative;
}

.youtube-target {
  width: 100%;
  height: 100%;
}
.no-video-placeholder i {
  font-size: 4rem;
  color: #6C63FF;
  opacity: 0.7;
}

.loading i {
  font-size: 3rem;
  color: #8a7aff;
}

.loading p {
  color: #aab2c2;
  font-style: italic;
  font-size: 1.1rem;
}

.lyrics-display {
  background: #121214;
  border-radius: 15px;
  position: relative;
  overflow: hidden;
  height: 200px;
  box-shadow: inset 0 0 20px rgba(0,0,0,0.8);
  border: 1px solid rgba(255, 255, 255, 0.05);

  margin-top: 20px;
}

.teleprompter-container {
  height: 100%;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  scroll-behavior: smooth;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.teleprompter-container::-webkit-scrollbar {
  display: none;
}

.teleprompter-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100px 0;
  min-height: 100%;
}

.line {
  margin: 12px 0;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  cursor: pointer;
  text-align: center;
  padding: 0 25px;
  font-weight: 500;
  line-height: 1.4;
  white-space: pre-wrap;
  word-wrap: break-word;
  max-width: 90%;

  font-size: 1.4rem;
  color: #777;
  opacity: 0.3;
  transform: none;
}

.line:hover {
  color: #aaa;
  opacity: 0.5;
}

.line.active {
  color: #fff;
  opacity: 1;
  font-size: 2.2rem;
  text-shadow: 0 0 25px rgba(108, 99, 255, 0.7), 0 0 10px rgba(255, 255, 255, 0.3);
  font-weight: 900;
  letter-spacing: 0.5px;
  margin: 20px 0;
}

.line.past {
  color: #3a3a3a;
  opacity: 0.2;
  font-size: 1.2rem;
}

.line.future {
  color: #555;
  opacity: 0.4;
  font-size: 1.3rem;
}

.fallback-lyrics {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #ccc;
}

.fallback-lyrics::-webkit-scrollbar {
  display: none;
}

.fallback-lyrics pre {
  white-space: pre-wrap;
  color: #b0b0b0;
  font-family: 'Fira Code', monospace;
  text-align: center;
  line-height: 1.8;
  background: rgba(0, 0, 0, 0.2);
  padding: 15px;
  border-radius: 8px;
  max-height: 150px;
  overflow-y: auto;
}

.fallback-lyrics.empty {
  justify-content: center;
  color: #555;
  font-size: 1.3rem;
}

.fallback-lyrics.empty i {
  font-size: 4.5rem;
  margin-bottom: 20px;
  color: #6C63FF;
  opacity: 0.6;
}

.no-song-selected {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: rgba(30, 30, 30, 0.4);
  color: #aab2c2;
  font-size: 1.7rem;
  padding: 40px;
  height: 500px;
  border-radius: 20px;
  border: 2px dashed rgba(255, 255, 255, 0.1);
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.4);
}

.spin-animation {
  font-size: 5rem;
  margin-bottom: 25px;
  animation: spin 6s linear infinite;
  opacity: 0.4;
  color: #6C63FF;
}

.btn-favorite {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.8rem;
  padding: 0;
  color: #aab2c2;
  transition: color 0.3s, transform 0.2s;
}

.btn-favorite:hover {
  color: #fff;
  transform: scale(1.1);
}

.btn-favorite.is-favorite {
  color: #FF6584;
  animation: favorite-pop 0.3s ease-out;
}

.btn-favorite.is-favorite i {
  filter: drop-shadow(0 0 5px rgba(255, 101, 132, 0.7));
}
.btn-suggest-icon {
  background: none;
  border: none;
  cursor: pointer;

  font-size: 1.8rem;
  padding: 0;
  color: #aab2c2;

  transition: color 0.3s, transform 0.2s;
}

.btn-suggest-icon:hover {
  color: #8a7aff;
  transform: scale(1.1);
}

.btn-suggest-icon:active {
  transform: scale(0.95);
}



.version-control-wrapper {
  position: absolute;
  top: 15px;
  right: 15px;
  z-index: 10;

  display: block;
  margin: 0;
  padding: 0;
}

.btn-version {
  background: rgba(30, 30, 35, 0.85);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #aab2c2;
  border-radius: 20px;
  padding: 6px 14px;
  font-size: 0.85rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  backdrop-filter: blur(4px);
  font-family: inherit;
  box-shadow: 0 4px 10px rgba(0,0,0,0.3);
}

.btn-version:hover {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
  border-color: rgba(255, 255, 255, 0.25);
  box-shadow: 0 0 15px rgba(255, 255, 255, 0.05);
}

.btn-version:active {
  transform: translateY(0);
}

.icon-refresh {
  font-size: 1rem;
  transition: transform 0.5s ease;
  display: inline-flex;
  width: 1em;
  height: 1em;
}

.btn-version:hover .icon-refresh {
  transform-origin: center;
  transform: rotate(180deg);
  color: #8a7aff;
}

.version-counter {
  background: rgba(0, 0, 0, 0.3);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.75rem;
  opacity: 0.8;
}

.version-label {
  font-weight: 500;
}

@keyframes favorite-pop {
  0% { transform: scale(1); }
  50% { transform: scale(1.3); }
  100% { transform: scale(1.1); }
}

@keyframes spin {
  100% { transform: rotate(360deg); }
}
</style>
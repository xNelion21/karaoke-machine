<template>
  <div v-if="songDetails" class="karaoke-player-container">
    <div class="header-info">
      <h3>{{ songDetails.song?.title || songDetails.title }}</h3>
      <div class="meta-tags">
        <span v-if="songDetails.song?.authors || songDetails.authors" class="tag artist">
          <i class="bi bi-mic-fill"></i> {{ (songDetails.song?.authors || songDetails.authors || []).join(', ') }}
        </span>
        <span v-if="songDetails.song?.genre || songDetails.genre" class="tag genre">
          <i class="bi bi-music-note-beamed"></i> {{ songDetails.song?.genre || songDetails.genre }}
        </span>
      </div>
    </div>

    <div class="video-player-wrapper" :key="videoId">
      <div v-if="videoId" ref="youtubeTarget" class="youtube-target"></div>
      <div v-else class="no-video-placeholder">
        <i class="bi bi-camera-video-off"></i>
        <p>Brak wideo dla tego utworu.</p>
      </div>
    </div>

    <div class="lyrics-display">

      <div v-if="sortedLyricLines && sortedLyricLines.length > 0" class="teleprompter-container" ref="teleprompterRef">
        <div class="teleprompter-content">
          <p
              v-for="(line, index) in sortedLyricLines"
              :key="line.id || index"
              :class="{
                'line': true,
                'active': activeLineIndex === index,
                'past': index < activeLineIndex,
                'future': index > activeLineIndex && index > (activeLineIndex + linesToShowBeforeAndAfter) // Pokazujemy tylko kilka przyszłych
              }"
              @click="seekTo(line.timeStampStart)"
              :ref="el => { if (activeLineIndex === index) activeLineRef = el }"
          >
            {{ line.text }}
          </p>
        </div>
        <div class="gradient-overlay top"></div>
        <div class="gradient-overlay bottom"></div>
      </div>

      <div v-else-if="songDetails.song?.lyrics || songDetails.lyrics" class="fallback-lyrics">
        <p class="info-text">Brak synchronizacji czasowej.</p>
        <pre>{{ songDetails.song?.lyrics || songDetails.lyrics }}</pre>
      </div>

      <div v-else class="fallback-lyrics empty">
        <i class="bi bi-music-note-list"></i>
        <p>Brak tekstu dla tego utworu.</p>
      </div>

    </div>

  </div>
  <div v-else class="no-song-selected">
    <i class="bi bi-disc spin-animation"></i>
    <p>Wybierz piosenkę, aby rozpocząć karaoke!</p>
  </div>
</template>

<script setup>
import { ref, computed, watch, onUnmounted, nextTick } from 'vue';

const props = defineProps({
  songDetails: {
    type: Object,
    default: null,
  },
});

const linesToShowBeforeAndAfter = 1;

const player = ref(null);
const currentTime = ref(0);
const youtubeTarget = ref(null);
const teleprompterRef = ref(null);
const activeLineRef = ref(null);
let playbackInterval = null;
const sortedLyricLines = computed(() => {
  const lines = props.songDetails?.lyricLines || props.songDetails?.song?.lyricLines;
  if (lines) {
    return [...lines].sort((a, b) => a.timeStampStart - b.timeStampStart);
  }
  return [];
});

const videoId = computed(() => {
  const url = props.songDetails?.videoUrl ||
      props.songDetails?.youtubeUrl ||
      props.songDetails?.song?.videoUrl ||
      props.songDetails?.song?.youtubeUrl;

  if (!url) return null;
  const match = url.match(/(?:youtu\.be\/|youtube\.com\/(?:watch\?v=|embed\/|v\/|shorts\/))([\w-]{11})/);
  return match ? match[1] : null;
});

const activeLineIndex = computed(() => {
  const index = sortedLyricLines.value.findIndex(line =>
      currentTime.value >= line.timeStampStart && currentTime.value < line.timeStampEnd
  );
  if (index === -1 && currentTime.value > 0 && sortedLyricLines.value.length > 0) {
    return sortedLyricLines.value.findIndex(line => line.timeStampEnd > currentTime.value);
  }
  return index;
});

const scrollToActiveLine = (behavior = 'smooth') => {
  if (activeLineRef.value && teleprompterRef.value) {
    const container = teleprompterRef.value;
    const element = activeLineRef.value;

    element.scrollIntoView({
      behavior: behavior,
      block: 'center'
    });
  }
};

const startPlaybackTimer = () => {
  if (playbackInterval) clearInterval(playbackInterval);
  playbackInterval = setInterval(() => {
    if (player.value && typeof player.value.getCurrentTime === 'function') {
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
    if (window.YT && window.YT.Player) {
      resolve();
      return;
    }
    if (!document.querySelector('script[src="https://www.youtube.com/iframe_api"]')) {
      const tag = document.createElement('script');
      tag.src = 'https://www.youtube.com/iframe_api';
      document.head.appendChild(tag);
    }
    const checkInterval = setInterval(() => {
      if (window.YT && window.YT.Player) {
        clearInterval(checkInterval);
        resolve();
      }
    }, 100);
  });
};

const initPlayer = async () => {
  if (player.value) {
    try { player.value.destroy(); } catch (e) {}
    player.value = null;
  }

  if (!videoId.value) {
    stopPlaybackTimer();
    currentTime.value = 0;
    return;
  }

  await loadYouTubeAPI();
  await nextTick();

  if (!youtubeTarget.value) return;

  player.value = new window.YT.Player(youtubeTarget.value, {
    height: '100%',
    width: '100%',
    videoId: videoId.value,
    playerVars: {
      'autoplay': 1,
      'playsinline': 1,
      'origin': window.location.origin
    },
    events: {
      'onReady': (event) => {
        scrollToActiveLine('auto');
        event.target.playVideo();
        startPlaybackTimer();
      },
      'onStateChange': (event) => {
        if (event.data === window.YT.PlayerState.PLAYING) {
          startPlaybackTimer();
        } else {
          stopPlaybackTimer();
        }
      }
    }
  });
};

const isLineHighlighted = (line) => {
  return currentTime.value >= line.timeStampStart && currentTime.value < line.timeStampEnd;
};

const seekTo = (time) => {
  if (player.value && typeof player.value.seekTo === 'function') {
    player.value.seekTo(time, true);
  }
};

watch(videoId, async (newId) => {
  if (newId) {
    await nextTick();
    initPlayer();
  } else {
    stopPlaybackTimer();
    if (player.value) {
      try { player.value.destroy(); } catch (e) {}
      player.value = null;
    }
    currentTime.value = 0;
  }
}, { immediate: true });

watch(() => props.songDetails, async (newVal) => {
  if (newVal && !videoId.value) {
    stopPlaybackTimer();
    if (player.value) {
      try { player.value.destroy(); } catch (e) {}
      player.value = null;
    }
    currentTime.value = 0;
  }
}, { immediate: true });


onUnmounted(() => {
  stopPlaybackTimer();
  if (player.value) {
    try { player.value.destroy(); } catch (e) {}
  }
});
</script>

<style scoped>
@import url("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.3/font/bootstrap-icons.css");

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  font-family: 'Poppins', sans-serif;
  background-color: #1a1a2e;
  color: #e0e0e0;
}

.karaoke-player-container {
  background: linear-gradient(145deg, #1e1e24, #23232a);
  border-radius: 20px;
  padding: 2rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  color: #fff;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.05);
  max-width: 1000px;
  margin: 20px auto;
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

.tag.artist { color: #8a7aff; background: rgba(138, 122, 255, 0.15); }
.tag.genre { color: #ff85a2; background: rgba(255, 133, 162, 0.15); }

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

.no-video-placeholder {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: rgba(30,30,30,0.9);
  color: #aab2c2;
  font-style: italic;
  font-size: 1.3rem;
  gap: 15px;
}

.no-video-placeholder i {
  font-size: 4rem;
  color: #6C63FF;
  opacity: 0.7;
}

.lyrics-display {
  background: #121214;
  border-radius: 15px;
  position: relative;
  overflow: hidden;
  height: 200px;
  box-shadow: inset 0 0 20px rgba(0,0,0,0.8);
  border: 1px solid rgba(255, 255, 255, 0.05);
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
.teleprompter-container::-webkit-scrollbar { display: none; }

.teleprompter-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100px 0;
  min-height: 100%;
}

.line {
  font-size: 1.4rem;
  color: #777;
  margin: 12px 0;
  transition: all 0.2s ease-out;
  cursor: pointer;
  text-align: center;
  padding: 0 25px;
  font-weight: 500;
  opacity: 0.3;
  transform: scale(0.9);
  line-height: 1.4;
}

.line:hover {
  color: #aaa;
  opacity: 0.5;
}

.line.active {
  color: #fff;
  font-size: 2.2rem;
  opacity: 1;
  transform: scale(1.15);
  text-shadow: 0 0 25px rgba(108, 99, 255, 0.7), 0 0 10px rgba(255, 255, 255, 0.3);
  font-weight: 900;
  letter-spacing: 0.5px;
  transition: all 0.3s cubic-bezier(0.68, -0.55, 0.27, 1.55);
}

.line.past {
  color: #3a3a3a;
  opacity: 0.2;
  font-size: 1.2rem;
  transform: scale(0.85);
}
.line.future {
  color: #555;
  opacity: 0.4;
  font-size: 1.3rem;
  transform: scale(0.95);
}

.gradient-overlay {
  position: absolute;
  left: 0;
  right: 0;
  height: 80px;
  pointer-events: none;
  z-index: 2;
}
.gradient-overlay.top {
  top: 0;
  background: linear-gradient(to bottom, #121214 0%, transparent 100%);
}
.gradient-overlay.bottom {
  bottom: 0;
  background: linear-gradient(to top, #121214 0%, transparent 100%);
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

.fallback-lyrics::-webkit-scrollbar { display: none; }

.info-text { color: #FF6584; margin-bottom: 15px; font-style: italic; font-size: 1.1em; }
.fallback-lyrics pre {
  white-space: pre-wrap;
  color: #b0b0b0;
  font-family: 'Fira Code', monospace;
  text-align: center;
  line-height: 1.8;
  background: rgba(0,0,0,0.2);
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
.fallback-lyrics.empty i { font-size: 4.5rem; margin-bottom: 20px; color: #6C63FF; opacity: 0.6; }

.no-song-selected {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: rgba(30,30,30,0.4);
  color: #aab2c2;
  font-size: 1.7rem;
  padding: 40px;
  height: 500px;
  border-radius: 20px;
  border: 2px dashed rgba(255,255,255,0.1);
  box-shadow: 0 5px 20px rgba(0,0,0,0.4);
}
.spin-animation {
  font-size: 5rem;
  margin-bottom: 25px;
  animation: spin 6s linear infinite;
  opacity: 0.4;
  color: #6C63FF;
}

@keyframes spin { 100% { transform: rotate(360deg); } }
</style>
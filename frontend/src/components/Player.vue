<template>
  <div class="karaoke-layout">
    <div class="lyrics-wrapper">
      <h2 class="song-title">{{ song.title || 'Tytuł piosenki' }}</h2>
      <h4 class="song-artist">{{ song.artist || 'Artysta' }}</h4>
      <pre class="lyrics-box">{{ song.lyrics || 'Tekst piosenki pojawi się tutaj...' }}</pre>
    </div>

    <div class="video-wrapper">
      <video controls :src="song.videoUrl" class="video-player">
        Twoja przeglądarka nie obsługuje tego video.
      </video>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

const props = defineProps({
  song: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
.karaoke-layout {
  display: flex;
  flex-direction: row;
  gap: 2rem;
  justify-content: center;
  align-items: flex-start;
  margin-bottom: 50px;
}

.lyrics-wrapper {
  flex: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.song-title {
  font-size: 2rem;
  background: -webkit-linear-gradient(45deg, #6C63FF, #a9a5ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 0.5rem;
}

.song-artist {
  font-size: 1.25rem;
  color: #aab2c2;
  margin-bottom: 1rem;
}

.lyrics-box {
  width: 100%;
  max-width: 900px;
  max-height: 75vh;
  background-color: rgba(30,30,30,0.9);
  color: #fff;
  font-size: 2rem;
  line-height: 2.2rem;
  padding: 30px;
  border-radius: 15px;
  overflow-y: auto;
  white-space: pre-wrap;
  text-align: center;
}

.video-wrapper {
  flex: 1;
  display: flex;
  justify-content: center;
}

.video-player {
  width: 100%;
  max-width: 500px;
  border-radius: 15px;
  box-shadow: 0 8px 30px rgba(0,0,0,0.5);
}


@media (max-width: 992px) {
  .karaoke-layout {
    flex-direction: column;
  }
  .lyrics-box {
    font-size: 1.5rem;
    max-height: 50vh;
  }
  .video-player {
    max-width: 100%;
    margin-bottom: 20px;
  }
}
</style>

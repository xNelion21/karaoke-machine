<template>
  <div class="modal-overlay">
    <div class="modal-box">
      <h3>Dodaj piosenkę</h3>

      <input v-model="title" class="form-control mb-2" placeholder="Tytuł" />
      <input v-model="artist" class="form-control mb-2" placeholder="Artysta" />
      <input v-model="genre" class="form-control mb-2" placeholder="Gatunek" />
      <input v-model="youtubeUrl" class="form-control mb-2" placeholder="URL Youtube" />
      <textarea v-model="lyrics" class="form-control mb-3" placeholder="Tekst piosenki"></textarea>

      <div class="d-flex justify-content-end gap-2">
        <button class="btn btn-secondary" @click="$emit('close')">Anuluj</button>
        <button class="btn btn-primary" @click="submit">Zapisz</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const title = ref('')
const artist = ref('')
const genre = ref('')
const youtubeUrl = ref('')
const lyrics = ref('')

const auth = useAuthStore()

async function submit() {
  axios.defaults.headers.common['Authorization'] = `Bearer ${auth.token}`

  await axios.post('/songs', {
    title: title.value,
    artist: artist.value,
    genre: genre.value,
    lyrics: lyrics.value,
    youtubeUrl: youtubeUrl.value,
    authorIds: null,
    categoryIds: null
  })

  $emit('added')
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0,0,0,0.6);
  display: flex;
  justify-content: center;
  align-items: center;
}
.modal-box {
  background: #1f1f27;
  padding: 20px;
  width: 450px;
  border-radius: 8px;
  color: white;
}
</style>
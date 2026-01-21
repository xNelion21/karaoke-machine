<template>
  <div class="songs-page">
    <h1 class="admin-title">Piosenki</h1>

    <div v-if="showForm" class="form-overlay" @click.self="closeForm">
      <div class="song-form">
        <h3>{{ editingSong ? 'Edytuj piosenkę' : 'Dodaj piosenkę' }}</h3>

        <input v-model="form.title" class="form-control mb-2" placeholder="Tytuł">
        <input v-model="form.genre" class="form-control mb-2" placeholder="Gatunek">
        <input v-model="form.youtubeUrl" class="form-control mb-2" placeholder="Link YouTube">

        <textarea v-model="form.lyrics" class="form-control mb-3" placeholder="Tekst piosenki"></textarea>

        <select v-model="form.authorId" class="form-select mb-3">
          <option disabled value="">Wybierz autora</option>
          <option v-for="a in authors" :value="a.id" :key="a.id">
            {{ a.name }}
          </option>
        </select>

        <div class="d-flex gap-2">
          <button class="btn btn-success" @click="saveSong">Zapisz</button>
          <button class="btn btn-secondary" @click="closeForm">Anuluj</button>
        </div>
      </div>
    </div>

    <div class="controls-row">
      <input
          v-model="search"
          class="form-control search-input"
          placeholder="Wyszukaj piosenkę"
      />

      <select v-model="sortBy" class="form-select sort-select">
        <option value="id">ID</option>
        <option value="title">Tytuł</option>
        <option value="artist">Artysta</option>
        <option value="genre">Gatunek</option>
      </select>

      <select v-model="sortDir" class="form-select sort-select">
        <option value="asc">Rosnąco</option>
        <option value="desc">Malejąco</option>
      </select>
    </div>

    <SongsTable
        ref="tableRef"
        :search="search"
        :sort-by="sortBy"
        :sort-dir="sortDir"
        :page="page"
        @edit="openEdit"
        @delete="deleteSong"
        @page-change="page = $event"
    />

    <div class="bottom-actions">
      <button class="btn btn-primary mt-3" @click="openCreate">
        Dodaj piosenkę
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from "axios"
import { useAuthStore } from '@/stores/auth'
import SongsTable from '@/components/admin/SongsTable.vue'

const auth = useAuthStore()

const search = ref('')
const sortBy = ref('id')
const sortDir = ref('asc')
const page = ref(1)

const authors = ref([])

const showForm = ref(false)
const editingSong = ref(null)
const tableRef = ref(null)

const form = ref({
  title: '',
  category: '',
  lyrics: '',
  youtubeUrl: '',
  authorId: ''
})

async function loadAuthors() {
  axios.defaults.headers.common['Authorization'] = `Bearer ${auth.token}`
  const resp = await axios.get('/authors')
  authors.value = resp.data
}

function openCreate() {
  editingSong.value = null
  form.value = {
    title: '',
    genre: '',
    lyrics: '',
    youtubeUrl: '',
    authorId: ''
  }
  showForm.value = true
}

function openEdit(song) {
  editingSong.value = song.id
  form.value = {
    title: song.title,
    category: song.category,
    lyrics: song.lyrics,
    youtubeUrl: song.youtubeUrl,
    authorId: song.authorIds?.length ? song.authorIds[0] : ''
  }
  showForm.value = true
}

async function saveSong() {
  axios.defaults.headers.common['Authorization'] = `Bearer ${auth.token}`

  const payload = {
    title: form.value.title,
    category: form.value.category,
    lyrics: form.value.lyrics,
    youtubeUrl: form.value.youtubeUrl,
    authorsIds: [form.value.authorId],
    categoryIds: []
  }

  if (editingSong.value) {
    await axios.put(`/songs/${editingSong.value}`, payload)
  } else {
    await axios.post('/songs', payload)
  }

  showForm.value = false

  if (tableRef.value) {
    await tableRef.value.loadSongs()
  }
}

async function deleteSong(id) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${auth.token}`
  await axios.delete(`/songs/${id}`)

  if (tableRef.value) {
    await tableRef.value.loadSongs()
  }
}

function closeForm() {
  showForm.value = false
}

onMounted(loadAuthors)
</script>

<style scoped>
.songs-page {
  position: relative;
  color: white;
  padding: 20px;
}

.admin-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 24px;
}

.controls-row {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  align-items: center;
}

.search-input {
  flex: 1;
  min-width: 0;
}

.sort-select {
  width: 160px;
}

.bottom-actions {
  display: flex;
  justify-content: flex-end;
}

.form-overlay {
  position: fixed;
  inset: 0;
  backdrop-filter: blur(6px);
  background: rgba(0,0,0,0.85);
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding-top: 60px;
  z-index: 99999;
}

.song-form {
  background: rgba(20,20,20,0.95);
  padding: 20px;
  width: 380px;
  border-radius: 10px;
}
</style>


<template>
  <div class="songs-page">
    <h1 class="admin-title">Piosenki</h1>

    <AddSong
        v-if="showAddEmpty"
        @added="reloadTable"
        @close="showAddEmpty = false"
    />

    <AddSongYoutube
        v-if="showAddYoutube"
        @added="reloadTable"
        @close="showAddYoutube = false"
    />

    <div v-if="showForm" class="form-overlay" @click.self="closeForm">
      <div class="song-form">
        <h3>Edytuj piosenkę</h3>

        <input
            v-model="form.title"
            class="form-control mb-2"
            placeholder="Tytuł"
        />

        <input
            v-model="form.youtubeUrl"
            class="form-control mb-2"
            placeholder="Link YouTube"
        />

        <textarea
            v-model="form.lyrics"
            class="form-control mb-3"
            placeholder="Tekst piosenki"
        />

        <div class="dropdown mb-3">
          <button
              class="btn btn-outline-light dropdown-toggle w-100"
              type="button"
              data-bs-toggle="dropdown"
          >
            Kategorie
          </button>

          <ul class="dropdown-menu w-100">
            <li v-for="c in categories" :key="c.id" class="px-2">
              <label class="form-check">
                <input
                    type="checkbox"
                    class="form-check-input"
                    :value="c.name"
                    v-model="form.categories"
                />
                {{ c.name }}
              </label>
            </li>
          </ul>
        </div>

        <div class="d-flex gap-2">
          <button class="btn btn-success" @click="saveSong">
            Zapisz
          </button>
          <button class="btn btn-secondary" @click="closeForm">
            Anuluj
          </button>
        </div>
      </div>
    </div>

    <div class="controls-row">
      <input
          v-model="search"
          class="form-control search-input"
          placeholder="Wyszukaj piosenkę"
      />
    </div>

    <SongsTable
        ref="tableRef"
        :search="search"
        :page="page"
        @edit="openEdit"
        @delete="deleteSong"
        @page-change="page = $event"
    />

    <div class="bottom-actions">
      <button
          class="btn btn-outline-light me-2"
          @click="showAddEmpty = true"
      >
        Dodaj piosenkę
      </button>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

import SongsTable from '@/components/admin/SongsTable.vue'
import AddSong from '@/components/admin/AddSong.vue'

const auth = useAuthStore()

const search = ref('')
const page = ref(1)

const categories = ref([])

const showAddEmpty = ref(false)
const showAddYoutube = ref(false)
const showForm = ref(false)

const editingSong = ref(null)
const tableRef = ref(null)

const form = ref({
  title: '',
  lyrics: '',
  youtubeUrl: '',
  categories: []
})

onMounted(async () => {
  axios.defaults.headers.common.Authorization = `Bearer ${auth.token}`
  categories.value = (await axios.get('/categories')).data
})

function openEdit(song) {
  editingSong.value = song.id
  form.value = {
    title: song.title,
    lyrics: song.lyrics,
    youtubeUrl: song.youtubeUrl,
    categories: song.categories ?? []
  }
  showForm.value = true
}

async function saveSong() {
  await axios.put(`/songs/${editingSong.value}`, {
    title: form.value.title,
    lyrics: form.value.lyrics,
    youtubeUrl: form.value.youtubeUrl,
    categories: form.value.categories
  })

  showForm.value = false
  reloadTable()
}

async function deleteSong(id) {
  await axios.delete(`/songs/${id}`)
  reloadTable()
}

function reloadTable() {
  tableRef.value?.loadSongs()
}

function closeForm() {
  showForm.value = false
}
</script>

<style scoped>
.songs-page {
  color: white;
  padding: 20px;
}

.admin-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 24px;
}

.controls-row {
  margin-bottom: 20px;
}

.bottom-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.form-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.85);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 99999;
}

.song-form {
  background: rgba(20,20,20,0.95);
  padding: 20px;
  width: 380px;
  border-radius: 10px;
}
</style>

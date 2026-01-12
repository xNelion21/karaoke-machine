<template>
  <div class="authors-page">
    <h1 class="admin-title">Autorzy</h1>

    <div v-if="showForm" class="form-overlay" @click.self="closeForm">
      <div class="author-form">
        <h3>{{ editingAuthor ? 'Edytuj autora' : 'Dodaj autora' }}</h3>

        <input v-model="form.name" class="form-control mb-3" placeholder="Nazwa autora" />

        <div class="d-flex gap-2">
          <button class="btn btn-success" @click="saveAuthor">Zapisz</button>
          <button class="btn btn-secondary" @click="closeForm">Anuluj</button>
        </div>
      </div>
    </div>

    <div class="controls-row">
      <input
          v-model="search"
          class="form-control search-input"
          placeholder="Wyszukaj autora"
      />

      <select v-model="sortBy" class="form-select sort-select">
        <option value="id">ID</option>
        <option value="name">Nazwa</option>
      </select>

      <select v-model="sortDir" class="form-select sort-select">
        <option value="asc">Rosnąco</option>
        <option value="desc">Malejąco</option>
      </select>
    </div>

    <AuthorTable
        ref="tableRef"
        :search="search"
        :sort-by="sortBy"
        :sort-dir="sortDir"
        :page="page"
        @edit="openEdit"
        @delete="deleteAuthor"
        @page-change="page = $event"
    />

    <div class="bottom-actions">
      <button class="btn btn-primary mt-3" @click="openCreate">
        Dodaj autora
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import AuthorTable from '@/components/admin/AuthorTable.vue'

const auth = useAuthStore()

const search = ref('')
const sortBy = ref('id')
const sortDir = ref('asc')
const page = ref(1)

const showForm = ref(false)
const editingAuthor = ref(null)
const tableRef = ref(null)

const form = ref({ name: '' })

function openCreate() {
  editingAuthor.value = null
  form.value = { name: '' }
  showForm.value = true
}

function openEdit(author) {
  editingAuthor.value = author.id
  form.value = { ...author }
  showForm.value = true
}

async function saveAuthor() {
  axios.defaults.headers.common['Authorization'] = `Bearer ${auth.token}`

  if (editingAuthor.value)
    await axios.put(`/authors/${editingAuthor.value}`, form.value)
  else
    await axios.post('/authors', form.value)

  showForm.value = false
  if (tableRef.value) tableRef.value.loadAuthors()
}

async function deleteAuthor(id) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${auth.token}`
  await axios.delete(`/authors/${id}`)

  if (tableRef.value) tableRef.value.loadAuthors()
}

function closeForm() {
  showForm.value = false
}
</script>

<style scoped>
.authors-page {
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

.author-form {
  background: rgba(20,20,20,0.95);
  padding: 20px;
  width: 360px;
  border-radius: 10px;
}
</style>



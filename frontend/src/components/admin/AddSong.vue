<template>
  <div class="modal-overlay">
    <div class="modal-box">
      <h3>Dodaj nową piosenkę</h3>

      <input
          v-model="form.title"
          class="form-control mb-2"
          placeholder="Tytuł piosenki"
      />

      <input
          v-model="form.youtubeUrl"
          class="form-control mb-2"
          placeholder="Link YouTube (opcjonalnie)"
      />

      <textarea
          v-model="form.lyrics"
          class="form-control mb-3"
          placeholder="Tekst piosenki"
          rows="5"
      />

      <div class="dropdown mb-4">
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

      <div class="d-flex justify-content-end gap-2">
        <button class="btn btn-secondary" @click="emit('close')">
          Anuluj
        </button>

        <button
            class="btn btn-primary"
            :disabled="!form.title.trim()"
            @click="submit"
        >
          Dodaj
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const emit = defineEmits(['added', 'close'])
const auth = useAuthStore()

const categories = ref([])

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

async function submit() {
  await axios.post(
      '/songs',
      {
        title: form.value.title,
        lyrics: form.value.lyrics,
        youtubeUrl: form.value.youtubeUrl || null,
        categories: form.value.categories,
        authors: null
      },
      {
        headers: {
          Authorization: `Bearer ${auth.token}`
        }
      }
  )

  emit('added')
  emit('close')
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 99999;
}

.modal-box {
  background: #1f1f27;
  padding: 20px;
  width: 460px;
  border-radius: 8px;
  color: white;
}
</style>

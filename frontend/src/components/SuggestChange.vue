<template>
  <div class="modal-overlay">
    <div class="modal-box">
      <h3>Zaproponuj poprawkę</h3>

      <select v-model="category" class="form-select mb-2">
        <option value="GENERAL">Ogólna</option>
        <option value="TEXT">Tekst piosenki</option>
        <option value="TIMESTAMP">Znaczniki czasowe</option>
      </select>

      <textarea
          v-model="content"
          class="form-control mb-3"
          placeholder="Opisz proponowaną poprawkę"
          rows="6"
      ></textarea>

      <div class="d-flex justify-content-end gap-2">
        <button class="btn btn-secondary" @click="emit('close')">
          Anuluj
        </button>
        <button
            class="btn btn-primary"
            :disabled="!canSubmit"
            @click="submit"
        >
          Wyślij
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const props = defineProps({
  songId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['close', 'submitted'])

const category = ref('GENERAL')
const content = ref('')

const auth = useAuthStore()

const canSubmit = computed(() => content.value.trim().length > 0)

async function submit() {
  axios.defaults.headers.common['Authorization'] = `Bearer ${auth.token}`

  await axios.post('/songs/suggest', {
    songId: props.songId,
    proposedContent: `[${category.value}] ${content.value.trim()}`
  })

  emit('submitted')
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
  z-index: 2000;
}

.modal-box {
  background: #1f1f27;
  padding: 20px;
  width: 450px;
  border-radius: 8px;
  color: white;
}
</style>




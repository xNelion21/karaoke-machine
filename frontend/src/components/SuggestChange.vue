<template>
  <div class="modal-overlay">
    <div class="modal-box">
      <h3>{{ $t('change.suggest_change') }}</h3>

      <select v-model="category" class="form-select mb-2">
        <option value="GENERAL">{{$t('change.general')}}</option>
        <option value="TEXT">{{$t('change.text')}}</option>
        <option value="OTHER">{{$t('change.other')}}</option>
      </select>

      <textarea
          v-model="content"
          class="form-control mb-3"
          :placeholder="$t('change.suggestion')"
          rows="10"
      ></textarea>

      <div class="d-flex justify-content-end gap-2">
        <button class="btn btn-secondary" @click="emit('close')">
          {{ $t('change.cancel') }}
        </button>
        <button
            class="btn btn-primary"
            :disabled="!canSubmit"
            @click="submit"
        >
          {{ $t('change.send') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import axios from 'axios'

const props = defineProps({
  songId: {
    type: [Number, null],
    default: null
  },
  youtubeData: {
    type: Object,
    default: null
  },

  initialLyrics: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['close', 'submitted'])

const category = ref('GENERAL')
const content = ref('')

const canSubmit = computed(() => content.value.trim().length > 0)

watch(category, (newVal) => {
  if (newVal === 'TEXT') {
    content.value = props.initialLyrics || ''
  } else {
    content.value = ''
  }
}, { immediate: true })

async function submit() {

  const payload = {
    songId: props.songId,
    youtubeSongData: props.songId ? null : props.youtubeData,
    proposedContent: `[${category.value}]\n${content.value.trim()}`
  }

  if (category.value === 'TEXT') {
    payload.proposedLyrics = content.value.trim();
  }

  await axios.post('/songs/suggest', payload)

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
  padding: 20px;
}

.modal-box {
  background: #1f1f27;
  padding: 25px;

  width: 800px;
  max-width: 100%;

  max-height: 90vh;
  display: flex;
  flex-direction: column;

  border-radius: 12px;
  color: white;
  box-shadow: 0 10px 40px rgba(0,0,0,0.5);
}

textarea.form-control {
  min-height: 400px;
  resize: vertical;
  font-family: monospace;
  font-size: 0.95rem;
}
</style>
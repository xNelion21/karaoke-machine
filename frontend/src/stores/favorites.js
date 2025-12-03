
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useFavoritesStore = defineStore('favorites', () => {
    const favoriteIds = ref(JSON.parse(localStorage.getItem('karaoke_favorites')) || [])

    const isFavorite = computed(() => (songId) => {
        return favoriteIds.value.includes(songId)
    })

    function toggleFavorite(songId) {
        const index = favoriteIds.value.indexOf(songId)

        if (index > -1) {
            favoriteIds.value.splice(index, 1)
        } else {
            favoriteIds.value.push(songId)
        }

        localStorage.setItem('karaoke_favorites', JSON.stringify(favoriteIds.value))
    }

    function resetState() {
        favoriteIds.value = [];
    }

    return { favoriteIds, isFavorite, toggleFavorite, resetState }
})
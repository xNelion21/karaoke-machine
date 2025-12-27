import { defineStore } from 'pinia'
import { computed } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth.js'

export const useFavoritesStore = defineStore('favorites', () => {
    const authStore = useAuthStore()

    const favoriteIds = computed(() => {
        return authStore.user?.likedSongIds || []
    })

    const isFavorite = computed(() => (songId) => {
        return favoriteIds.value.includes(songId)
    })

    async function toggleFavorite(songId) {
        if (!authStore.isAuthenticated || !authStore.user) return

        if (!authStore.user.likedSongIds) {
            authStore.user.likedSongIds = []
        }

        const index = authStore.user.likedSongIds.indexOf(songId)
        const isAdding = index === -1

        if (isAdding) {
            authStore.user.likedSongIds.push(songId)
        } else {
            authStore.user.likedSongIds.splice(index, 1)
        }

        try {
            if (isAdding) {
                await axios.post(`/users/me/likes/${songId}`)
            } else {
                await axios.delete(`/users/me/likes/${songId}`)
            }
        } catch (error) {
            console.error("Błąd zapisu, cofanie zmian:", error)
            if (isAdding) {
                const removeIndex = authStore.user.likedSongIds.indexOf(songId)
                if (removeIndex > -1) authStore.user.likedSongIds.splice(removeIndex, 1)
            } else {
                authStore.user.likedSongIds.push(songId)
            }
        }
    }

    function resetState() {}

    return { favoriteIds, isFavorite, toggleFavorite, resetState }
})
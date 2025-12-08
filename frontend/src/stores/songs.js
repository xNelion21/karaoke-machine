import { defineStore } from 'pinia'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth.js'
import { useFavoritesStore } from '@/stores/favorites.js'

export const useSongsStore = defineStore('songs', {
    state: () => ({
        allSongs: [],
        currentSong: null,

        relatedSongs: [],
        isRelatedLoading: false,

        searchQuery: '',
        isLoading: false,
        error: null
    }),

    getters: {
        filteredSongs: (state) => {
            const query = state.searchQuery.toLowerCase().trim()
            if (!query) return []
            return state.allSongs.filter(song =>
                song.title.toLowerCase().includes(query)
            )
        },
        hasResults: (state) => state.filteredSongs.length > 0,

        favoriteSongsList: (state) => {
            const favoritesStore = useFavoritesStore()
            return state.allSongs.filter(song => favoritesStore.isFavorite(song.id))
        }
    },

    actions: {
        async fetchSongs() {
            const authStore = useAuthStore()
            if (!authStore.isAuthenticated) return

            this.isLoading = true;
            try {
                const response = await axios.get('/songs', {
                    headers: { Authorization: `Bearer ${authStore.token}` }
                })
                this.allSongs = response.data
            } catch (e) {
                console.error("Błąd pobierania listy piosenek:", e)
            } finally {
                this.isLoading = false;
            }
        },

        async fetchSongDetails(id) {
            const authStore = useAuthStore()
            this.currentSong = null;

            try {
                const response = await axios.get(`/songs/${id}`, {
                    headers: { Authorization: `Bearer ${authStore.token}` }
                })
                this.currentSong = response.data;
            } catch (err) {
                console.error("Błąd pobierania szczegółów:", err);
            }
        },

        fetchRelatedSongs(category, currentSongId) {
            this.relatedSongs = []
            this.isRelatedLoading = true

            setTimeout(() => {
                try {
                    const results = this.allSongs.filter(song => {
                        const songCategories = song.categories || song.song?.categories || [];
                        const hasCategory = songCategories.includes(category);

                        const isNotCurrent = song.id !== currentSongId;

                        return hasCategory && isNotCurrent;
                    });

                    this.relatedSongs = results
                        .sort(() => 0.5 - Math.random())
                        .slice(0, 4);

                } catch (error) {
                    console.error("Błąd filtrowania powiązanych:", error);
                } finally {
                    this.isRelatedLoading = false;
                }
            }, 300);
        },


        setSearchQuery(query) { this.searchQuery = query },
        clearSearch() { this.searchQuery = '' },
        resetState() {this.allSongs = []; this.relatedSongs = []; this.currentSong = null; },
    }
})
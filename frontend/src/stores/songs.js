import { defineStore } from 'pinia';
import axios from 'axios';
import { useAuthStore } from '@/stores/auth.js';
import { useFavoritesStore } from '@/stores/favorites.js';
import { normalizeSong } from '@/utils/songUtils';

export const useSongsStore = defineStore('songs', {
    state: () => ({
        allSongs: [],
        searchResults: [],
        currentSong: null,
        relatedSongs: [],
        isRelatedLoading: false,
        searchQuery: '',
        isLoading: false,
        isSearching: false,
        error: null
    }),

    getters: {
        filteredSongs: (state) => {
            return state.searchResults.map(song => normalizeSong(song));
        },

        hasResults: (state) => state.searchResults.length > 0,

        favoriteSongsList: (state) => {
            const favoritesStore = useFavoritesStore();
            return state.allSongs
                .map(s => normalizeSong(s))
                .filter(song => favoritesStore.isFavorite(song));
        },
        relatedSongsNormalized: (state) => {
            return state.relatedSongs
                .map(song => normalizeSong(song))
                .filter(song => song !== null);
        },
    },

    actions: {
        async fetchSongs() {
            const authStore = useAuthStore();
            if (!authStore.isAuthenticated) return;

            this.isLoading = true;
            try {
                const response = await axios.get('/songs');
                this.allSongs = response.data;
            } catch (e) {
                console.error("Błąd pobierania listy piosenek:", e);
            } finally {
                this.isLoading = false;
            }
        },

        async fetchSongDetails(song) {
            if (song.id) {
                try {
                    const response = await axios.get(`/songs/${song.id}`);
                    this.currentSong = normalizeSong(response.data);
                } catch (e) {
                    console.error("Błąd pobierania szczegółów:", e);
                }
            } else {
                this.currentSong = song;
            }
        },

        async setSearchQuery(query) {
            this.searchQuery = query;
            if (!query) {
                this.searchResults = [];
                return;
            }

            this.isSearching = true;
            this.isLoading = true;

            try {
                const lowerQuery = query.toLowerCase();
                const localResults = this.allSongs.filter(song => {
                    const title = song.song?.title || song.title || '';
                    const artist = Array.isArray(song.song?.authors)
                        ? song.song.authors.join(' ')
                        : (song.song?.artist || song.artist || '');

                    return title.toLowerCase().includes(lowerQuery) ||
                        String(artist).toLowerCase().includes(lowerQuery);
                });

                let onlineResults = [];
                try {
                    const response = await axios.get('/songs/search-online', { params: { query } });
                    onlineResults = response.data || [];
                } catch (e) {
                    console.warn("Szukanie online nieudane:", e.message);
                }

                this.searchResults = [...localResults, ...onlineResults];

            } catch (e) {
                console.error("Błąd wyszukiwania:", e);
            } finally {
                this.isSearching = false;
                this.isLoading = false;
            }
        },

        clearSearch() {
            this.searchQuery = '';
            this.searchResults = [];
        },

        async fetchRelatedSongs(artistName) {
            this.isRelatedLoading = true;
            try {
                const response = await axios.get('/songs/search-online', { params: { query: artistName } });
                this.relatedSongs = Array.isArray(response.data) ? response.data : [];
            } catch (e) {
                console.error(e);
            } finally {
                this.isRelatedLoading = false;
            }
        },

        resetState() {
            this.allSongs = [];
            this.searchResults = [];
            this.relatedSongs = [];
            this.currentSong = null;
            this.searchQuery = '';
        },
    }
});
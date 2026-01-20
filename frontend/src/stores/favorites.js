import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth.js'
import { normalizeSong } from "@/utils/songUtils.js";

export const useFavoritesStore = defineStore('favorites', () => {
    const authStore = useAuthStore()
    const favoritesList = ref([])

    const favoriteIds = computed(() => {
        return authStore.user?.likedSongIds || []
    })

    const isFavorite = computed(() => (songOrId) => {
        if (!songOrId) return false;

        const song = typeof songOrId === 'object' ? normalizeSong(songOrId) : null;

        if (song?.id || (typeof songOrId !== 'object')) {
            const idToCheck = song?.id || songOrId;
            if (favoriteIds.value.some(id => String(id) === String(idToCheck))) {
                return true;
            }
        }

        if (song?.videoId) {
            return favoritesList.value.some(f => f.videoId === song.videoId);
        }

        return false;
    })

    async function fetchFavorites() {
        if (!authStore.isAuthenticated) return;

        try {
            const response = await axios.get('/songs/my-liked-songs');
            const rawData = response.data;
            favoritesList.value = rawData.map(song => normalizeSong(song));
        } catch (e) {
            console.error("Błąd pobierania ulubionych", e)
        }
    }

    async function toggleFavorite(rawSong) {
        if (!authStore.isAuthenticated || !authStore.user) {
            alert("Musisz być zalogowany!");
            return;
        }
        if (!rawSong) return;

        let song = normalizeSong(rawSong);

        if (!song.id && song.videoId) {
            const existingFav = favoritesList.value.find(f => f.videoId === song.videoId);
            if (existingFav && existingFav.id) {
                song.id = existingFav.id;
            }
        }

        const dbId = song.id;
        const ytId = song.videoId;

        if (!authStore.user.likedSongIds) {
            authStore.user.likedSongIds = [];
        }

        const isFav = isFavorite.value(song);
        const isAdding = !isFav;

        if (isAdding) {
            favoritesList.value.unshift(song);
            if (dbId) {
                authStore.user.likedSongIds.push(dbId);
            }
        } else {
            if (dbId) {
                const index = authStore.user.likedSongIds.findIndex(id => String(id) === String(dbId));
                if (index > -1) authStore.user.likedSongIds.splice(index, 1);
            }

            favoritesList.value = favoritesList.value.filter(s => {
                if (dbId && s.id && String(s.id) === String(dbId)) return false;
                if (ytId && s.videoId && s.videoId === ytId) return false;
                return true;
            });
        }

        try {
            if (isAdding) {
                if (!dbId && ytId) {
                    const youtubeDto = {
                        videoId: ytId,
                        title: song.title,
                        thumbnailUrl: song.thumbnailUrl,
                        artist: song.artist,
                    };

                    const response = await axios.post('/songs/like', youtubeDto);

                    if (response.data?.id) {
                        const newId = response.data.id;

                        const idx = favoritesList.value.findIndex(f => f.videoId === ytId);
                        if (idx !== -1) {
                            favoritesList.value[idx].id = newId;
                        }

                        authStore.user.likedSongIds.push(newId);
                    }
                }
                else if (dbId) {
                    await axios.post(`/users/me/likes/${dbId}`);
                }
            } else {
                if (dbId) {
                    await axios.delete(`/users/me/likes/${dbId}`);
                } else {
                    console.warn("Próba usunięcia piosenki bez ID (błąd synchronizacji stanu).");
                }
            }
        } catch (error) {
            console.error("Błąd API toggleFavorite:", error);
            if (isAdding) {
                favoritesList.value = favoritesList.value.filter(s => s.videoId !== ytId);
                if (dbId) {
                    const idx = authStore.user.likedSongIds.indexOf(dbId);
                    if (idx > -1) authStore.user.likedSongIds.splice(idx, 1);
                }
            } else {
                favoritesList.value.unshift(song)
                if (dbId) authStore.user.likedSongIds.push(dbId);
            }
            alert("Wystąpił błąd podczas aktualizacji ulubionych.");
        }
    }

    function resetState() {
        favoritesList.value = [];
    }

    return {
        favoritesList,
        favoriteIds,
        isFavorite,
        fetchFavorites,
        toggleFavorite,
        resetState
    }
})
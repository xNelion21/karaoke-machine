import { defineStore } from 'pinia'
import axios from 'axios'
import {useSongsStore} from "@/stores/songs.js";
import {useFavoritesStore} from "@/stores/favorites.js";

axios.defaults.baseURL = 'http://localhost:8080/api';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: null,
        token: localStorage.getItem('token') || sessionStorage.getItem('token') || null,
        errorMessage: null,
        isLoading: false
    }),

    getters: {
        isAuthenticated: (state) => !!state.token,
        username: (state) => state.user?.username || null
    },

    actions: {
        async login(credentials, rememberMe) {
            this.isLoading = true;
            this.errorMessage = null;

            try {
                const response = await axios.post('/auth/login', credentials);
                const token = response.data.accessToken;

                this.token = token;
                if (rememberMe) {
                    localStorage.setItem('token', token);
                } else {
                    sessionStorage.setItem('token', token);
                }

                axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

                await this.fetchUser();

                if (this.user.locked) {
                    this.errorMessage = "Twoje konto zostało zablokowane.";
                    this.logout();
                    return false;
                }

                return true;

            } catch (error) {
                this.errorMessage = error.response?.data?.message || 'Nieprawidłowa nazwa użytkownika lub hasło.';
                return false;

            } finally {
                this.isLoading = false;
            }
        }
        ,

        async register(credentials) {
            this.isLoading = true;
            this.errorMessage = null;
            try {
                const response = await axios.post('/auth/register', credentials);

                const token = response.data.accessToken;
                this.token = token;
                sessionStorage.setItem('token', token);
                axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
                await this.fetchUser();
                return true;
            } catch (error) {
                this.errorMessage = error.response?.data?.message || 'Wystąpił błąd podczas rejestracji. Sprawdź poprawność danych.';
                return false;
            } finally {
                this.isLoading = false;
            }
        },

        async loginWithFacebook(fbUserData) {
            this.isLoading = true;
            try {
                const payload = {
                    facebookId: fbUserData.id,
                    email: fbUserData.email,
                    name: fbUserData.name,
                    pictureUrl: fbUserData.picture?.data?.url || ''
                };

                const response = await axios.post('/auth/facebook', payload);
                const token = response.data.accessToken;

                this.token = token;
                localStorage.setItem('token', token);
                axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

                await this.fetchUser();
                return true;
            } catch (error) {
                console.error("Błąd backendu:", error);
                return false;
            } finally {
                this.isLoading = false;
            }
        },

        async fetchUser() {
            if (!this.token) return;

            try {
                axios.defaults.headers.common['Authorization'] = `Bearer ${this.token}`;
                const response = await axios.get('/users/me');
                this.user = response.data;
            } catch (error) {
                console.error('Błąd pobierania użytkownika:', error);
                this.logout();
            }
        },

        logout() {
            this.user = null;
            this.token = null;
            localStorage.removeItem('token');
            sessionStorage.removeItem('token');
            delete axios.defaults.headers.common['Authorization'];

            const songsStore = useSongsStore();
            songsStore.resetState();

            const favoritesStore = useFavoritesStore();
            favoritesStore.resetState();
        }
    }
})
const BASE_URL = 'https://lrclib.net/api';

export const fetchSyncedLyrics = async (artist, title, duration = null) => {
    try {
        const url = new URL(`${BASE_URL}/search`);
        url.searchParams.append('track_name', title);
        url.searchParams.append('artist_name', artist);

        const response = await fetch(url.toString(), {
            method: 'GET'
        });

        if (!response.ok) {
            console.warn(`[LRCLIB] Błąd HTTP: ${response.status}`);
            return null;
        }

        const results = await response.json();

        if (!Array.isArray(results) || results.length === 0) {
            console.warn("[LRCLIB] Brak wyników (pusta tablica).");
            return null;
        }

        const candidates = results.filter(item => item.syncedLyrics);

        if (candidates.length === 0) {
            console.warn("[LRCLIB] Znaleziono utwór, ale brak zsynchronizowanych napisów.");
            return null;
        }
        if (duration) {
            candidates.sort((a, b) => {
                const diffA = Math.abs(a.duration - duration);
                const diffB = Math.abs(b.duration - duration);
                return diffA - diffB;
            });

            return candidates[0].syncedLyrics;
        }

        return candidates[0].syncedLyrics;

    } catch (error) {
        console.error("[LRCLIB] Błąd krytyczny:", error);
        return null;
    }
};
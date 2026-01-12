const BASE_URL = 'https://lrclib.net/api';

export const fetchSyncedLyrics = async (artist, title, duration = null) => {
    try {
        const targetDuration = (duration && duration > 0) ? parseFloat(duration) : null;

        const url = new URL(`${BASE_URL}/search`);
        url.searchParams.append('track_name', title);
        url.searchParams.append('artist_name', artist);

        const response = await fetch(url.toString(), { method: 'GET' });

        if (!response.ok) {
            console.warn(`[LRCLIB] Błąd HTTP: ${response.status}`)
            return null;
        }

        const results = await response.json();

        const candidates = results.filter(item => item.syncedLyrics);

        if (targetDuration) {
            candidates.forEach(c => {
                c.diff = Math.abs(c.duration - targetDuration);
            });

            candidates.sort((a, b) => a.diff - b.diff);

            const bestMatch = candidates[0];

            if (bestMatch.diff > 15) {
            }

            return candidates.slice(0, 5);
        }

        else {
            return candidates[0].syncedLyrics;
        }

    } catch (error) {
        console.error("[LRCLIB] Błąd krytyczny:", error);
        return null;
    }
};
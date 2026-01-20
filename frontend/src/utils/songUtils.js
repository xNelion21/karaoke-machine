export const extractYoutubeId = (urlOrId) => {
    if (!urlOrId) return null;
    if (urlOrId.length === 11) return urlOrId;
    const match = urlOrId.match(/(?:youtu\.be\/|youtube\.com\/(?:watch\?v=|embed\/|v\/|shorts\/))([\w-]{11})/);
    return match ? match[1] : null;
};

export const cleanTitle = (rawTitle) => {
    if (!rawTitle) return '';
    return rawTitle
        .replace(/\s*[([].*?(karaoke|instrumental|version|lyrics|off vocal|no guide|cover|official|video).*?[)\]]/gi, '')
        .replace(/official video/gi, '')
        .replace(/hq/gi, '')
        .trim();
};

const resolveArtist = (data) => {
    if (data.authors) {
        if (Array.isArray(data.authors)) {
            return data.authors.map(a => (typeof a === 'object' ? a.name : a)).join(', ');
        }
        return data.authors;
    }

    if (data.artist) {
        if (typeof data.artist === 'object') {
            return data.artist.name || "Nieznany wykonawca";
        }
        return data.artist;
    }

    return null;
};

export const normalizeSong = (input) => {
    if (!input) return null;

    const core = input.song || input;
    const videoId = core.videoId || extractYoutubeId(core.youtubeUrl || core.videoUrl);

    let artistName = resolveArtist(core);
    let title = core.title;

    if (!artistName && title && title.includes(' - ')) {
        const parts = title.split(' - ');
        artistName = parts[0].trim();
        title = parts.slice(1).join(' - ').trim();
    }

    const finalTitle = cleanTitle(title);

    return {
        id: core.id || input.id,
        title: finalTitle || 'Nieznany tytuł',
        artist: artistName || 'Nieznany wykonawca',
        videoId: videoId,
        youtubeUrl: core.youtubeUrl || (videoId ? `https://www.youtube.com/watch?v=${videoId}` : null),
        coverUrl: core.coverUrl || (videoId ? `https://img.youtube.com/vi/${videoId}/mqdefault.jpg` : null),
    };
};
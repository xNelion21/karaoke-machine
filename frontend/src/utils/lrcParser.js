export function parseLRC(lrcString) {
    if (!lrcString) return [];

    const lines = lrcString.split('\n');
    const result = [];

    const timeReg = /\[(\d{2}):(\d{2})\.(\d{2,3})\]/;

    lines.forEach(line => {
        const match = timeReg.exec(line);
        if (match) {
            const min = parseInt(match[1], 10);
            const sec = parseInt(match[2], 10);
            const msRaw = match[3];

            const ms = parseInt(msRaw, 10);
            const msInSeconds = msRaw.length === 3 ? ms / 1000 : ms / 100;

            const time = min * 60 + sec + msInSeconds;
            const text = line.replace(timeReg, '').trim();

            if (text) {
                result.push({ time, text });
            }
        }
    });

    return result;
}
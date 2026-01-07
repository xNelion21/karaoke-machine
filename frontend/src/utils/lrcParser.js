export function parseLRC(lrcString) {
    if (!lrcString) return [];

    const lines = lrcString.split('\n');
    const result = [];

    const timeReg = /\[(\d{2}):(\d{2})\.(\d{2,3})\]/;

    lines.forEach(line => {
        const match = timeReg.exec(line);
        if (match) {
            const min = parseInt(match[1]);
            const sec = parseInt(match[2]);
            const ms = parseInt(match[3]);

            const time = min * 60 + sec + (ms / 100);
            const text = line.replace(timeReg, '').trim();

            if (text) {
                result.push({ time, text });
            }
        }
    });

    return result;
}
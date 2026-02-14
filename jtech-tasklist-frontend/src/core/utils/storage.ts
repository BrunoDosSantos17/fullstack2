export const sget = <T>(k: string, d: T): T => JSON.parse(localStorage.getItem(k) || 'null') ?? d
export const sset = (k: string, v: unknown) => localStorage.setItem(k, JSON.stringify(v))
export const suuid = () => crypto.randomUUID()
export const nowIso = () => new Date().toISOString()

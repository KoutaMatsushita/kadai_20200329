import ky from "ky";

export const fetcher = <T>(url: string) => ky(url).json<T>()
import axios from "axios";

const FALLBACK_API_BASE_URL = "http://localhost:8080";

export const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL as string | undefined | null)
  ? (import.meta.env.VITE_API_BASE_URL as string)
  : FALLBACK_API_BASE_URL;

export const DEFAULT_HEADERS = {
  Accept: "application/json",
} as const;

export const httpClient = axios.create({
  baseURL: API_BASE_URL.replace(/\/$/, ""),
  headers: DEFAULT_HEADERS,
});

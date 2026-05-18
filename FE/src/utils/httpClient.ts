import axios, { AxiosHeaders } from "axios";

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

httpClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (!token) return config;

  const currentHeaders = config.headers;
  if (currentHeaders && typeof (currentHeaders as AxiosHeaders).set === "function") {
    const axiosHeaders = currentHeaders as AxiosHeaders;
    if (!axiosHeaders.has("Authorization")) {
      axiosHeaders.set("Authorization", `Bearer ${token}`);
    }
  } else {
    const plain = (currentHeaders ?? {}) as any;
    if (!plain.Authorization && !plain.authorization) {
      plain.Authorization = `Bearer ${token}`;
    }
    config.headers = plain;
  }
  return config;
});

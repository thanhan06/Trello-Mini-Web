import axios from "axios";
import { httpClient } from "../utils/httpClient";

export type User = {
  id: string;
  name: string;
  email: string;
  role: string;
  dob: string;
};

export type RegisterPayload = {
  name: string;
  email: string;
  password: string;
  dob: string;
};

export type LoginPayload = {
  email: string;
  password: string;
};

export type LoginResponse = {
  accessToken: string;
  refreshToken: string;
  authenticated: boolean;
};

export type GoogleLoginPayload = {
  code: string;
};

export type ApiResponse<T> = {
  timestamp: string;
  status: number;
  message: string;
  path: string;
  data: T;
};

export async function register(payload: RegisterPayload): Promise<User> {
  try {
    const res = await httpClient.post<ApiResponse<User>>("/users/register", payload);
    return res.data.data;
  } catch (err) {
    if (axios.isAxiosError(err)) {
      const data = err.response?.data as any;
      if (data?.message) throw new Error(data.message);
      if (typeof data === "string" && data.trim().length > 0) {
        throw new Error(data);
      }
      throw new Error(err.message);
    }

    throw err;
  }
}

export async function login(payload: LoginPayload): Promise<LoginResponse> {
  try {
    const res = await httpClient.post<ApiResponse<LoginResponse>>("/auth/login", payload);
    return res.data.data;
  } catch (err) {
    if (axios.isAxiosError(err)) {
      const data = err.response?.data as any;
      if (data?.message) throw new Error(data.message);
      if (typeof data === "string" && data.trim().length > 0) {
        throw new Error(data);
      }
      throw new Error(err.message);
    }

    throw err;
  }
}

export async function loginWithGoogle(payload: GoogleLoginPayload): Promise<LoginResponse> {
  try {
    const res = await httpClient.post<ApiResponse<LoginResponse>>("/auth/google", payload);
    return res.data.data;
  } catch (err) {
    if (axios.isAxiosError(err)) {
      const data = err.response?.data as any;
      if (data?.message) throw new Error(data.message);
      if (typeof data === "string" && data.trim().length > 0) {
        throw new Error(data);
      }
      throw new Error(err.message);
    }

    throw err;
  }
}

export async function getUsers(): Promise<User[]> {
  try {
    const res = await httpClient.get<ApiResponse<User[]>>("/users");
    return res.data.data;
  } catch (err) {
    if (axios.isAxiosError(err)) {
      const data = err.response?.data;
      if (typeof data === "string" && data.trim().length > 0) {
        throw new Error(data);
      }
      throw new Error(err.message);
    }

    throw err;
  }
}

export async function getMyInfo(): Promise<User> {
  try {
    const res = await httpClient.get<ApiResponse<User>>("/users/me");
    return res.data.data;
  } catch (err) {
    if (axios.isAxiosError(err)) {
      const data = err.response?.data as any;
      if (data?.message) throw new Error(data.message);
      if (typeof data === "string" && data.trim().length > 0) {
        throw new Error(data);
      }
      throw new Error(err.message);
    }

    throw err;
  }
}



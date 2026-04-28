import axios from "axios";
import { httpClient } from "../utils/httpClient";

export type Category = {
  id: number;
  name: string;
};

export async function getCategories(): Promise<Category[]> {
  try {
    const res = await httpClient.get<Category[]>("/categories");
    return res.data;
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

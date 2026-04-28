import { useEffect, useState } from "react";
import { getCategories, type Category } from "../services/CategoryService";

export default function CategoriesPage() {
  const [data, setData] = useState<Category[]>([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    let cancelled = false;
    async function load() {
      setLoading(true);
      setError("");
      try {
        const categories = await getCategories();
        if (!cancelled) {
          setData(categories);
        }
      } catch (e) {
        if (!cancelled) {
          setError(e instanceof Error ? e.message : "Something went wrong");
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <div>
      <h1>Categories</h1>

      {loading && <p>Loading...</p>}
      {error && !loading && <p style={{ color: "red" }}>{error}</p>}

      {!loading && !error && (
        <ul>
          {data.map((c) => (
            <li key={c.id}>{c.name}</li>
          ))}
        </ul>
      )}
    </div>
  );
}
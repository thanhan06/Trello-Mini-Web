import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { getMyInfo, type User } from '../services/AuthService';

export default function HomePage() {
  const navigate = useNavigate();

  const token = useMemo(() => localStorage.getItem('token') ?? '', []);

  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!token) return;

    let cancelled = false;
    setLoading(true);
    setError(null);

    getMyInfo()
      .then((u) => {
        if (cancelled) return;
        setUser(u);
      })
      .catch((e: any) => {
        if (cancelled) return;
        setError(e?.message ?? 'Không thể tải thông tin người dùng');
      })
      .finally(() => {
        if (cancelled) return;
        setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [token]);

  return (
    <div className="p-6">
      <h1 className="text-2xl font-semibold text-gray-900">Home</h1>

      {!token ? (
        <div className="mt-4 bg-white p-4 rounded-md shadow">
          <p className="text-gray-700">Bạn chưa đăng nhập hoặc không đọc được thông tin người dùng.</p>
          <button
            type="button"
            onClick={() => navigate('/login')}
            className="mt-3 inline-flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition duration-150"
          >
            Đi tới đăng nhập
          </button>
        </div>
      ) : loading ? (
        <div className="mt-4 bg-white p-4 rounded-md shadow">
          <p className="text-gray-700">Đang tải thông tin người dùng...</p>
        </div>
      ) : error ? (
        <div className="mt-4 bg-white p-4 rounded-md shadow">
          <p className="text-gray-700">Không thể tải thông tin người dùng.</p>
          <p className="mt-1 text-sm text-red-600">{error}</p>
          <button
            type="button"
            onClick={() => navigate('/login')}
            className="mt-3 inline-flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition duration-150"
          >
            Đi tới đăng nhập
          </button>
        </div>
      ) : (
        <div className="mt-4 bg-white p-4 rounded-md shadow">
          <p className="text-gray-700">
            Xin chào{user?.name ? `, ${user.name}` : ''}!
          </p>

          <div className="mt-3 space-y-1 text-sm text-gray-800">
            {user?.email && (
              <div>
                <span className="font-medium">Email:</span> {user.email}
              </div>
            )}
            {user?.role && (
              <div>
                <span className="font-medium">Role:</span> {user.role}
              </div>
            )}
            {user?.dob && (
              <div>
                <span className="font-medium">DOB:</span> {user.dob}
              </div>
            )}
            {user?.id && (
              <div>
                <span className="font-medium">User ID:</span> {user.id}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login, loginWithGoogle } from '../services/AuthService';
import { useToast } from '../components/Toast';

export default function LoginPage() {
  const navigate = useNavigate();
  const { showToast } = useToast();

  const [googleLoading, setGoogleLoading] = useState(false);

  const [loginData, setLoginData] = useState({
    email: '',
    password: '',
    rememberMe: false
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setLoginData({
      ...loginData,
      [name]: type === 'checkbox' ? checked : value,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const payload = {
        email: loginData.email,
        password: loginData.password
      };
      
      const res = await login(payload);
      
      if (res.authenticated) {
        // Lưu token vào localStorage (hoặc cookies tuỳ nhu cầu dự án)
        localStorage.setItem('token', res.accessToken);
        localStorage.setItem('refreshToken', res.refreshToken);
        
        showToast('Đăng nhập thành công!', 'success');
        navigate('/'); // Điều hướng về trang chủ
      } else {
        showToast('Đăng nhập thất bại, vui lòng kiểm tra lại thông tin.', 'error');
      }
    } catch (error: any) {
      const errorMsg = error?.response?.data?.message || error?.message || 'Lỗi không xác định';
      showToast('Đăng nhập thất bại: ' + errorMsg, 'error');
    }
  };

  const handleGoogleLogin = () => {
    const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
    if (!clientId) {
      showToast('Thiếu cấu hình VITE_GOOGLE_CLIENT_ID ở FE', 'error');
      return;
    }

    const initCodeClient = window.google?.accounts?.oauth2?.initCodeClient;
    if (!initCodeClient) {
      showToast('Google SDK chưa sẵn sàng. Hãy thử refresh trang.', 'error');
      return;
    }

    setGoogleLoading(true);

    const client = initCodeClient({
      client_id: clientId,
      scope: 'email profile openid',
      ux_mode: 'popup',
      callback: async (response) => {
        try {
          if ('error' in response) {
            showToast(`Google login lỗi: ${response.error}`, 'error');
            return;
          }

          const res = await loginWithGoogle({ code: response.code });
          if (res.authenticated) {
            localStorage.setItem('token', res.accessToken);
            localStorage.setItem('refreshToken', res.refreshToken);
            showToast('Đăng nhập Google thành công!', 'success');
            navigate('/');
            return;
          }

          showToast('Đăng nhập Google thất bại.', 'error');
        } catch (e: any) {
          const errorMsg = e?.message || 'Lỗi không xác định';
          showToast('Đăng nhập Google thất bại: ' + errorMsg, 'error');
        } finally {
          setGoogleLoading(false);
        }
      },
    });

    client.requestCode();
  };

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
          Chào mừng quay trở lại
        </h2>
        <p className="mt-2 text-center text-sm text-gray-600">
          Hoặc{' '}
          <a href="/register" className="font-medium text-indigo-600 hover:text-indigo-500">
            tạo tài khoản mới nếu chưa có
          </a>
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
          <form className="space-y-6" onSubmit={handleSubmit}>
            {/* Email */}
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                Địa chỉ Email
              </label>
              <div className="mt-1">
                <input
                  id="email"
                  name="email"
                  type="email"
                  required
                  value={loginData.email}
                  onChange={handleChange}
                  placeholder="name@company.com"
                  className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
              </div>
            </div>

            {/* Mật khẩu */}
            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700">
                Mật khẩu
              </label>
              <div className="mt-1">
                <input
                  id="password"
                  name="password"
                  type="password"
                  required
                  value={loginData.password}
                  onChange={handleChange}
                  className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
              </div>
            </div>

            <div className="flex items-center justify-between">
              <div className="flex items-center">
                <input
                  id="rememberMe"
                  name="rememberMe"
                  type="checkbox"
                  checked={loginData.rememberMe}
                  onChange={handleChange}
                  className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded cursor-pointer"
                />
                <label htmlFor="rememberMe" className="ml-2 block text-sm text-gray-900 cursor-pointer">
                  Ghi nhớ tôi
                </label>
              </div>

              <div className="text-sm">
                <a href="#" className="font-medium text-indigo-600 hover:text-indigo-500">
                  Quên mật khẩu?
                </a>
              </div>
            </div>

            <div>
              <button
                type="submit"
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition duration-150 transform active:scale-95"
              >
                Đăng nhập
              </button>
            </div>
          </form>

          {/* Social Login */}
          <div className="mt-6">
            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-gray-300"></div>
              </div>
              <div className="relative flex justify-center text-sm">
                <span className="px-2 bg-white text-gray-500">Hoặc đăng nhập với</span>
              </div>
            </div>

            <div className="mt-6 grid grid-cols-2 gap-3">
              <button
                type="button"
                onClick={handleGoogleLogin}
                disabled={googleLoading}
                className="w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-md shadow-sm bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 transition duration-150 disabled:opacity-60"
              >
                <span>Google</span>
              </button>
              <button className="w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-md shadow-sm bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 transition duration-150">
                <span>Github</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
import { useState } from 'react';
import { register, type RegisterPayload } from '../services/AuthService';
import { useNavigate } from 'react-router-dom';
import { useToast } from '../components/Toast';

type RegisterForm = {
  name: string;
  email: string;
  dob: string;
  password: string;
};

type RegisterErrors = Partial<Record<keyof RegisterForm, string>>;

const isValidEmail = (email: string) => {
  // close enough to javax.validation @Email behavior for client-side
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim());
};

const calculateAge = (dob: Date, today: Date) => {
  let age = today.getFullYear() - dob.getFullYear();
  const monthDiff = today.getMonth() - dob.getMonth();
  const dayDiff = today.getDate() - dob.getDate();
  if (monthDiff < 0 || (monthDiff === 0 && dayDiff < 0)) age -= 1;
  return age;
};

const validateRegisterForm = (data: RegisterForm): RegisterErrors => {
  const errors: RegisterErrors = {};

  if (!data.name.trim()) {
    errors.name = 'Vui lòng nhập họ và tên';
  }

  if (!data.email.trim() || !isValidEmail(data.email)) {
    errors.email = 'Email should be valid';
  }

  const pwd = data.password;
  if (pwd.length < 8 || pwd.length > 20) {
    errors.password = 'INVALID_PASSWORD';
  }

  if (!data.dob) {
    errors.dob = 'INVALID_DOB';
  } else {
    const dobDate = new Date(data.dob);
    if (Number.isNaN(dobDate.getTime())) {
      errors.dob = 'INVALID_DOB';
    } else {
      const age = calculateAge(dobDate, new Date());
      if (age < 18) {
        errors.dob = 'INVALID_DOB';
      }
    }
  }

  return errors;
};

export default function RegisterPage() {
  const navigate = useNavigate();
  const { showToast } = useToast();

  const [registerData, setRegisterData] = useState<RegisterForm>({
    name: '',
    email: '',
    dob: '',
    password: '',
  });

  const [errors, setErrors] = useState<RegisterErrors>({});

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setRegisterData({
      ...registerData,
      [name]: value,
    });

    if (errors[name as keyof RegisterForm]) {
      setErrors((prev) => ({ ...prev, [name]: undefined }));
    }
  };
  
  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const nextErrors = validateRegisterForm(registerData);
    if (Object.keys(nextErrors).length > 0) {
      setErrors(nextErrors);
      const firstError = Object.values(nextErrors).find(Boolean);
      showToast(firstError ?? 'Vui lòng kiểm tra lại thông tin đăng ký', 'error');
      return;
    }

    const payload: RegisterPayload = {
      name: registerData.name,
      email: registerData.email,
      dob: registerData.dob,
      password: registerData.password,
    };

    try {
      const user = await register(payload);
      showToast('Đăng ký thành công! Chào mừng ' + user.name, 'success');
      navigate('/login');
    } catch (error: any) {
      const errorMsg = error.response?.data?.message || error.message || 'Lỗi không xác định';
      showToast('Đăng ký thất bại: ' + errorMsg, 'error');
    }
  };


  return (
    <div className="min-h-screen bg-gray-100 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
          Tạo tài khoản mới
        </h2>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
          <form className="space-y-6" onSubmit={handleSubmit}>
            {/* Họ và Tên */}
            <div>
              <label htmlFor="name" className="block text-sm font-medium text-gray-700">Họ và tên</label>
              <div className="mt-1">
                <input
                  id="name"
                  name="name"
                  type="text"
                  required
                  value={registerData.name}
                  onChange={handleChange}
                  className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
              </div>
              {errors.name && (
                <p className="mt-1 text-sm text-red-600">{errors.name}</p>
              )}
            </div>

            {/* Email */}
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700">Địa chỉ Email</label>
              <div className="mt-1">
                <input
                  id="email"
                  name="email"
                  type="email"
                  required
                  value={registerData.email}
                  onChange={handleChange}
                  className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
              </div>
              {errors.email && (
                <p className="mt-1 text-sm text-red-600">{errors.email}</p>
              )}
            </div>

            {/* Ngày sinh (DOB) */}
            <div>
              <label htmlFor="dob" className="block text-sm font-medium text-gray-700">Ngày sinh</label>
              <div className="mt-1">
                <input
                  id="dob"
                  name="dob"
                  type="date"
                  required
                  value={registerData.dob}
                  onChange={handleChange}
                  className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
              </div>
              {errors.dob && (
                <p className="mt-1 text-sm text-red-600">{errors.dob}</p>
              )}
            </div>

            {/* Mật khẩu */}
            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700">Mật khẩu</label>
              <div className="mt-1">
                <input
                  id="password"
                  name="password"
                  type="password"
                  required
                  value={registerData.password}
                  onChange={handleChange}
                  className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
              </div>
              {errors.password && (
                <p className="mt-1 text-sm text-red-600">{errors.password}</p>
              )}
            </div>

            <div>
              <button
                type="submit"
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition duration-150"
              >
                Đăng ký ngay
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
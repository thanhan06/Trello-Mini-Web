async function loginRequest(email, password) {
	const res = await fetch('/auth/login', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({ email, password })
	});

	const text = await res.text();
	let payload;
	try {
		payload = text ? JSON.parse(text) : null;
	} catch {
		payload = null;
	}

	if (!res.ok) {
		const message = payload?.message || 'Đăng nhập thất bại';
		throw new Error(message);
	}

	return payload;
}

function setError(message) {
	const el = document.getElementById('errorMessage');
	if (el) el.textContent = message || '';
}

document.addEventListener('DOMContentLoaded', () => {
	const loginBtn = document.getElementById('loginBtn');
	const resetBtn = document.getElementById('resetBtn');
	const emailInput = document.getElementById('email');
	const passwordInput = document.getElementById('password');

	if (!loginBtn || !resetBtn || !emailInput || !passwordInput) return;

	resetBtn.addEventListener('click', () => {
		emailInput.value = '';
		passwordInput.value = '';
		setError('');
	});

	loginBtn.addEventListener('click', async () => {
		setError('');
		const email = String(emailInput.value || '').trim();
		const password = String(passwordInput.value || '');

		if (!email || !password) {
			setError('Vui lòng nhập đầy đủ thông tin.');
			return;
		}

		try {
			const apiResponse = await loginRequest(email, password);
			const data = apiResponse?.data;

			if (data?.accessToken) localStorage.setItem('accessToken', data.accessToken);
			if (data?.refreshToken) localStorage.setItem('refreshToken', data.refreshToken);

			setError('Đăng nhập thành công. Đang chuyển hướng...');
			setTimeout(() => {
				window.location.href = '/productlist';
			}, 1000);
		} catch (err) {
			setError(err instanceof Error ? err.message : 'Đăng nhập thất bại');
		}
	});
});


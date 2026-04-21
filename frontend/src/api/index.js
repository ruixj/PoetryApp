import axios from 'axios';
import { useAuthStore } from '../store/authStore';

const api = axios.create({
  baseURL: '/api',
  timeout: 15000,
});

// 请求拦截：自动附加 Token
api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token;
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// 响应拦截：统一错误处理
api.interceptors.response.use(
  (res) => res.data,
  (err) => {
    const data = err.response?.data;
    const code = data?.code;
    if (code === 4004) {
      useAuthStore.getState().logout();
      window.location.href = '/login';
    }
    const message = data?.message || '网络错误，请稍后重试';
    return Promise.reject({ code, message });
  }
);

// ── Auth ──────────────────────────────────────────────────
export const authApi = {
  sendSms:       (phone) => api.post('/auth/sms/send', null, { params: { phone } }),
  register:      (data)  => api.post('/auth/register', data),
  loginPassword: (data)  => api.post('/auth/login/password', data),
  loginSms:      (data)  => api.post('/auth/login/sms', data),
  logout:        ()      => api.post('/auth/logout'),
};

// ── User ──────────────────────────────────────────────────
export const userApi = {
  getProfile:   ()       => api.get('/user/profile'),
  updateProfile: (data)  => api.put('/user/profile', data),
  uploadAvatar:  (form)  => api.post('/user/avatar', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
};

// ── Poetry ────────────────────────────────────────────────
export const poetryApi = {
  listTextbooks:    ()              => api.get('/poetry/textbooks'),
  listGrades:       (textbookId)    => api.get(`/poetry/textbooks/${textbookId}/grades`),
  listUnits:        (gradeId)       => api.get(`/poetry/textbooks/grades/${gradeId}/units`),
  getPoem:          (poemId)        => api.get(`/poetry/poems/${poemId}`),
  getPoemsByUnit:   (unitId)        => api.get(`/poetry/units/${unitId}/poems`),
  getPoemsByCategory: (type, value) => api.get('/poetry/poems/category', { params: { type, value } }),
  getCategoryValues:  (type)        => api.get('/poetry/categories/values', { params: { type } }),
  addUnitToLibrary:   (unitId)      => api.post(`/poetry/library/unit/${unitId}`),
  addPoemToLibrary:   (poemId)      => api.post(`/poetry/library/poem/${poemId}`),
  getLibrary:         ()            => api.get('/poetry/library'),
  updateStage:   (poemId, stage)    => api.post(`/poetry/progress/${poemId}/stage`, null, { params: { stage } }),
  getProgress:   (poemId)           => api.get(`/poetry/progress/${poemId}`),
  getCompleted:  ()                 => api.get('/poetry/progress/completed'),
  uploadRecording: (poemId, form)   => api.post(`/poetry/progress/${poemId}/recording`, form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
};

// ── Game ──────────────────────────────────────────────────
export const gameApi = {
  submit:         (data)                   => api.post('/game/submit', data),
  getSubmissions: (categoryType, categoryValue) =>
    api.get('/game/submissions', { params: { categoryType, categoryValue } }),
};

// ── Shop ──────────────────────────────────────────────────
export const shopApi = {
  listItems:    (page = 0, size = 20) => api.get('/shop/items', { params: { page, size } }),
  addToCart:    (itemId, quantity = 1) => api.post('/shop/cart', null, { params: { itemId, quantity } }),
  getCart:      ()                     => api.get('/shop/cart'),
  removeFromCart: (itemId)             => api.delete(`/shop/cart/${itemId}`),
  placeOrder:   (data)                 => api.post('/shop/orders', data),
  getOrders:    ()                     => api.get('/shop/orders'),
};

// ── Admin ─────────────────────────────────────────────────
export const adminApi = {
  listUsers:       (page = 0, size = 20) => api.get('/admin/users', { params: { page, size } }),
  listTextbooks:   (page = 0, size = 100) => api.get('/admin/textbooks', { params: { page, size } }),
  listPoems:       (page = 0, size = 20)  => api.get('/admin/poems', { params: { page, size } }),
  createPoem:      (form) => api.post('/admin/poems', form, { headers: { 'Content-Type': 'multipart/form-data' } }),
  deletePoem:      (id)   => api.delete(`/admin/poems/${id}`),
  listShopItems:   (page = 0, size = 50) => api.get('/admin/shop/items', { params: { page, size } }),
  createShopItem:  (form) => api.post('/admin/shop/items', form, { headers: { 'Content-Type': 'multipart/form-data' } }),
  onShelfItem:     (id)   => api.put(`/admin/shop/items/${id}/status`, null, { params: { status: 'ON_SHELF' } }),
  offShelfItem:    (id)   => api.put(`/admin/shop/items/${id}/status`, null, { params: { status: 'OFF_SHELF' } }),
  listOrders:      (page = 0, size = 50) => api.get('/admin/shop/orders', { params: { page, size } }),
  updateOrderStatus: (id, status) => api.put(`/admin/shop/orders/${id}/status`, null, { params: { status } }),
};

export default api;

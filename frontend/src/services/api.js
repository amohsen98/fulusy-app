const API_BASE = import.meta.env.VITE_API_URL || '';

function getToken() {
  return localStorage.getItem('fulusy_token');
}

async function request(path, options = {}) {
  const token = getToken();
  const headers = { 'Content-Type': 'application/json', ...options.headers };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });

  if (res.status === 401) {
    localStorage.removeItem('fulusy_token');
    window.location.href = '/login';
    throw new Error('Unauthorized');
  }

  if (!res.ok) {
    const err = await res.json().catch(() => ({ error: 'Request failed' }));
    throw new Error(err.error || 'Request failed');
  }

  if (res.status === 204) return null;
  return res.json();
}

export const api = {
  // Auth
  register: (data) => request('/api/auth/register', { method: 'POST', body: JSON.stringify(data) }),
  login: (data) => request('/api/auth/login', { method: 'POST', body: JSON.stringify(data) }),
  me: () => request('/api/me'),

  // Dashboard
  dashboard: () => request('/api/dashboard'),

  // Expenses
  createExpense: (data) => request('/api/expenses', { method: 'POST', body: JSON.stringify(data) }),
  listExpenses: (year, month) => {
    const params = year && month ? `?year=${year}&month=${month}` : '';
    return request(`/api/expenses${params}`);
  },
  deleteExpense: (id) => request(`/api/expenses/${id}`, { method: 'DELETE' }),

  // Income
  createIncome: (data) => request('/api/incomes', { method: 'POST', body: JSON.stringify(data) }),
  listIncomes: () => request('/api/incomes'),
  deleteIncome: (id) => request(`/api/incomes/${id}`, { method: 'DELETE' }),

  // Savings
  createSavings: (data) => request('/api/savings', { method: 'POST', body: JSON.stringify(data) }),
  listSavings: () => request('/api/savings'),
  totalSaved: () => request('/api/savings/total'),

  // Budgets
  setBudget: (data) => request('/api/budgets', { method: 'POST', body: JSON.stringify(data) }),
  listBudgets: () => request('/api/budgets'),

  // Goals
  createGoal: (data) => request('/api/goals', { method: 'POST', body: JSON.stringify(data) }),
  listGoals: (status) => request(`/api/goals${status ? `?status=${status}` : ''}`),
  getGoal: (id) => request(`/api/goals/${id}`),
  contributeGoal: (id, data) => request(`/api/goals/${id}/contribute`, { method: 'POST', body: JSON.stringify(data) }),
  updateGoalStatus: (id, status) => request(`/api/goals/${id}/status`, { method: 'PATCH', body: JSON.stringify({ status }) }),

  // Chat
  chat: (message) => request('/api/chat', { method: 'POST', body: JSON.stringify({ message }) }),
  chatHistory: (limit = 50) => request(`/api/chat/history?limit=${limit}`),
};

import { createContext, useContext, useState, useEffect } from 'react';
import { api } from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('fulusy_token');
    if (token) {
      api.me()
        .then(setUser)
        .catch(() => localStorage.removeItem('fulusy_token'))
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, []);

  const login = async (data) => {
    const res = await api.login(data);
    localStorage.setItem('fulusy_token', res.token);
    setUser({ id: res.userId, email: res.email, name: res.name, language: res.language });
    return res;
  };

  const register = async (data) => {
    const res = await api.register(data);
    localStorage.setItem('fulusy_token', res.token);
    setUser({ id: res.userId, email: res.email, name: res.name, language: res.language });
    return res;
  };

  const logout = () => {
    localStorage.removeItem('fulusy_token');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);

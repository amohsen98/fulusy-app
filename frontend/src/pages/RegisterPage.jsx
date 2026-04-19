import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';

export default function RegisterPage() {
  const { t } = useTranslation();
  const { register } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({
    email: '', password: '', name: '',
    startingBalance: '', incomeMode: 'fixed',
    fixedIncomeAmount: '', fixedIncomeDay: '1',
  });

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const payload = {
        email: form.email,
        password: form.password,
        name: form.name,
        startingBalance: parseFloat(form.startingBalance) || 0,
        incomeMode: form.incomeMode,
        language: 'ar',
      };
      if (form.incomeMode !== 'variable') {
        payload.fixedIncomeAmount = parseFloat(form.fixedIncomeAmount) || 0;
        payload.fixedIncomeDay = parseInt(form.fixedIncomeDay) || 1;
      }
      await register(payload);
      navigate('/');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-6 bg-gradient-to-br from-fulusy-800 to-fulusy-950">
      <div className="w-full max-w-sm">
        <div className="text-center mb-8">
          <h1 className="font-display text-4xl text-white">{t('app.name')}</h1>
          <p className="text-fulusy-300 mt-2">{t('app.tagline')}</p>
        </div>

        <div className="card">
          <h2 className="font-semibold text-xl mb-6">{t('auth.register')}</h2>
          {error && <div className="bg-red-50 text-danger-500 text-sm rounded-xl p-3 mb-4">{error}</div>}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="label">{t('auth.name')}</label>
              <input className="input" required value={form.name} onChange={set('name')} />
            </div>
            <div>
              <label className="label">{t('auth.email')}</label>
              <input type="email" className="input" required dir="ltr" value={form.email} onChange={set('email')} />
            </div>
            <div>
              <label className="label">{t('auth.password')}</label>
              <input type="password" className="input" required dir="ltr" minLength={8}
                value={form.password} onChange={set('password')} />
            </div>
            <div>
              <label className="label">{t('auth.startingBalance')} ({t('common.le')})</label>
              <input type="number" className="input" dir="ltr" required min="0" step="0.01"
                value={form.startingBalance} onChange={set('startingBalance')} placeholder="4000" />
            </div>
            <div>
              <label className="label">{t('auth.incomeMode')}</label>
              <select className="input" value={form.incomeMode} onChange={set('incomeMode')}>
                <option value="fixed">{t('auth.fixed')}</option>
                <option value="variable">{t('auth.variable')}</option>
                <option value="hybrid">{t('auth.hybrid')}</option>
              </select>
            </div>
            {form.incomeMode !== 'variable' && (
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="label">{t('auth.fixedAmount')}</label>
                  <input type="number" className="input" dir="ltr" min="0"
                    value={form.fixedIncomeAmount} onChange={set('fixedIncomeAmount')} placeholder="12000" />
                </div>
                <div>
                  <label className="label">{t('auth.fixedDay')}</label>
                  <input type="number" className="input" dir="ltr" min="1" max="31"
                    value={form.fixedIncomeDay} onChange={set('fixedIncomeDay')} />
                </div>
              </div>
            )}
            <button type="submit" disabled={loading} className="btn-primary w-full text-lg py-3">
              {loading ? t('common.loading') : t('auth.submit')}
            </button>
          </form>

          <p className="text-center text-sm text-surface-500 mt-4">
            {t('auth.hasAccount')}{' '}
            <Link to="/login" className="text-fulusy-600 font-medium hover:underline">
              {t('auth.loginBtn')}
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}

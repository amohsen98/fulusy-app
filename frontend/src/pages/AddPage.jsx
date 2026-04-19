import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { api } from '../services/api';
import { Wallet, Receipt } from 'lucide-react';

const CATEGORIES = ['essentials', 'transport', 'luxuries', 'shopping', 'other'];
const SOURCES = ['salary', 'freelance', 'gift', 'gam3eya_payout', 'bonus', 'other'];

export default function AddPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [tab, setTab] = useState(searchParams.get('tab') === 'income' ? 'income' : 'expense');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  const today = new Date().toISOString().split('T')[0];

  const [expense, setExpense] = useState({ amount: '', categoryId: 'luxuries', note: '', expenseDate: today, isRecurring: false });
  const [income, setIncome] = useState({ amount: '', source: 'salary', note: '', incomeDate: today });

  const handleExpense = async (e) => {
    e.preventDefault();
    setLoading(true); setError(''); setSuccess('');
    try {
      await api.createExpense({
        ...expense,
        amount: parseFloat(expense.amount),
        expenseDate: expense.expenseDate,
      });
      setSuccess('✓');
      setExpense({ ...expense, amount: '', note: '' });
      setTimeout(() => setSuccess(''), 1500);
    } catch (err) { setError(err.message); }
    finally { setLoading(false); }
  };

  const handleIncome = async (e) => {
    e.preventDefault();
    setLoading(true); setError(''); setSuccess('');
    try {
      await api.createIncome({
        ...income,
        amount: parseFloat(income.amount),
        incomeDate: income.incomeDate,
      });
      setSuccess('💰');
      setIncome({ ...income, amount: '', note: '' });
      setTimeout(() => setSuccess(''), 1500);
    } catch (err) { setError(err.message); }
    finally { setLoading(false); }
  };

  return (
    <div className="pb-20">
      {/* Tab switch */}
      <div className="flex bg-surface-100 rounded-2xl p-1 mb-6">
        <button
          onClick={() => setTab('expense')}
          className={`flex-1 py-3 rounded-xl font-medium text-sm transition-all flex items-center justify-center gap-2 ${
            tab === 'expense' ? 'bg-white shadow-sm text-surface-900' : 'text-surface-500'
          }`}
        >
          <Receipt className="w-4 h-4" /> {t('expense.title')}
        </button>
        <button
          onClick={() => setTab('income')}
          className={`flex-1 py-3 rounded-xl font-medium text-sm transition-all flex items-center justify-center gap-2 ${
            tab === 'income' ? 'bg-white shadow-sm text-fulusy-700' : 'text-surface-500'
          }`}
        >
          <Wallet className="w-4 h-4" /> {t('income.title')}
        </button>
      </div>

      {error && <div className="bg-red-50 text-danger-500 text-sm rounded-xl p-3 mb-4">{error}</div>}
      {success && (
        <div className={`text-center text-5xl mb-4 animate-count-up ${tab === 'income' ? 'animate-pulse-green' : ''}`}>
          {success}
        </div>
      )}

      {/* Expense form */}
      {tab === 'expense' && (
        <form onSubmit={handleExpense} className="card space-y-4">
          <div>
            <label className="label">{t('expense.amount')} ({t('common.le')})</label>
            <input type="number" className="input text-2xl font-semibold text-center" dir="ltr"
              required min="0.01" step="0.01" placeholder="0"
              value={expense.amount} onChange={e => setExpense({...expense, amount: e.target.value})} />
          </div>

          <div>
            <label className="label">{t('expense.category')}</label>
            <div className="grid grid-cols-3 gap-2">
              {CATEGORIES.map((cat) => (
                <button
                  key={cat} type="button"
                  onClick={() => setExpense({...expense, categoryId: cat})}
                  className={`py-2.5 px-3 rounded-xl text-sm font-medium transition-all border-2 ${
                    expense.categoryId === cat
                      ? 'border-fulusy-500 bg-fulusy-50 text-fulusy-700'
                      : 'border-surface-100 text-surface-600 hover:border-surface-200'
                  }`}
                >
                  {t(`categories.${cat}`)}
                </button>
              ))}
            </div>
          </div>

          <div>
            <label className="label">{t('expense.note')}</label>
            <input className="input" value={expense.note}
              onChange={e => setExpense({...expense, note: e.target.value})} />
          </div>

          <div>
            <label className="label">{t('expense.date')}</label>
            <input type="date" className="input" dir="ltr"
              value={expense.expenseDate} onChange={e => setExpense({...expense, expenseDate: e.target.value})} />
          </div>

          <button type="submit" disabled={loading} className="btn-primary w-full py-3 text-lg">
            {loading ? t('common.loading') : t('expense.save')}
          </button>
        </form>
      )}

      {/* Income form */}
      {tab === 'income' && (
        <form onSubmit={handleIncome} className="card space-y-4">
          <div>
            <label className="label">{t('income.amount')} ({t('common.le')})</label>
            <input type="number" className="input text-2xl font-semibold text-center text-fulusy-700" dir="ltr"
              required min="0.01" step="0.01" placeholder="0"
              value={income.amount} onChange={e => setIncome({...income, amount: e.target.value})} />
          </div>

          <div>
            <label className="label">{t('income.source')}</label>
            <div className="grid grid-cols-3 gap-2">
              {SOURCES.map((src) => (
                <button
                  key={src} type="button"
                  onClick={() => setIncome({...income, source: src})}
                  className={`py-2.5 px-3 rounded-xl text-sm font-medium transition-all border-2 ${
                    income.source === src
                      ? 'border-fulusy-500 bg-fulusy-50 text-fulusy-700'
                      : 'border-surface-100 text-surface-600 hover:border-surface-200'
                  }`}
                >
                  {t(`sources.${src}`)}
                </button>
              ))}
            </div>
          </div>

          <div>
            <label className="label">{t('income.note')}</label>
            <input className="input" value={income.note}
              onChange={e => setIncome({...income, note: e.target.value})} />
          </div>

          <div>
            <label className="label">{t('income.date')}</label>
            <input type="date" className="input" dir="ltr"
              value={income.incomeDate} onChange={e => setIncome({...income, incomeDate: e.target.value})} />
          </div>

          <button type="submit" disabled={loading} className="btn-add-money w-full py-3 text-lg">
            {loading ? t('common.loading') : t('income.save')}
          </button>
        </form>
      )}
    </div>
  );
}

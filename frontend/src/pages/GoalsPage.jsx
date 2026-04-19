import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { api } from '../services/api';
import { Target, Plus, TrendingUp, Clock } from 'lucide-react';

export default function GoalsPage() {
  const { t } = useTranslation();
  const [goals, setGoals] = useState([]);
  const [showCreate, setShowCreate] = useState(false);
  const [contributeId, setContributeId] = useState(null);
  const [loading, setLoading] = useState(true);

  const load = () => {
    api.listGoals().then(setGoals).catch(console.error).finally(() => setLoading(false));
  };
  useEffect(load, []);

  const formatMoney = (n) => Number(n || 0).toLocaleString('en-US', { maximumFractionDigits: 0 });

  return (
    <div className="pb-20 space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="font-display text-2xl">{t('goals.title')}</h2>
        <button onClick={() => setShowCreate(true)} className="btn-primary flex items-center gap-1.5 text-sm">
          <Plus className="w-4 h-4" /> {t('goals.create')}
        </button>
      </div>

      {showCreate && <CreateGoalForm onClose={() => setShowCreate(false)} onCreated={() => { setShowCreate(false); load(); }} />}

      {loading && <div className="text-center py-8 text-surface-400">{t('common.loading')}</div>}

      {goals.map((g) => {
        const pct = Math.min(Number(g.progressPct) || 0, 100);
        const isActive = g.status === 'active';
        return (
          <div key={g.id} className={`card ${!isActive ? 'opacity-60' : ''}`}>
            <div className="flex items-start justify-between mb-3">
              <div className="flex items-center gap-2">
                <span className="text-xl">{g.icon || '🎯'}</span>
                <div>
                  <h3 className="font-semibold">{g.name}</h3>
                  <span className={`text-xs px-2 py-0.5 rounded-full ${
                    g.status === 'achieved' ? 'bg-fulusy-100 text-fulusy-700' :
                    g.status === 'failed' ? 'bg-red-100 text-danger-500' :
                    'bg-surface-100 text-surface-600'
                  }`}>
                    {g.status === 'achieved' ? t('goals.achieved') : g.status === 'failed' ? t('goals.failed') : `${g.daysRemaining} ${t('goals.daysLeft')}`}
                  </span>
                </div>
              </div>
              <span className="font-display text-xl text-fulusy-700">{pct.toFixed(0)}%</span>
            </div>

            {/* Progress bar */}
            <div className="health-bar h-3 mb-2">
              <div
                className={`health-bar-fill ${g.status === 'achieved' ? 'bg-fulusy-500' : 'bg-gradient-to-r from-fulusy-400 to-fulusy-600'}`}
                style={{ width: `${pct}%` }}
              />
            </div>

            <div className="flex justify-between text-sm text-surface-500 mb-3">
              <span>{formatMoney(g.currentAmount)} / {formatMoney(g.targetAmount)} {t('common.le')}</span>
              <span className="flex items-center gap-1">
                <Clock className="w-3.5 h-3.5" />
                {formatMoney(g.requiredMonthlySavings)} {t('common.le')}/{t('goals.perMonth')}
              </span>
            </div>

            {isActive && (
              contributeId === g.id
                ? <ContributeForm goalId={g.id} onDone={() => { setContributeId(null); load(); }} />
                : <button onClick={() => setContributeId(g.id)} className="btn-primary w-full text-sm flex items-center justify-center gap-1.5">
                    <TrendingUp className="w-4 h-4" /> {t('goals.contribute')}
                  </button>
            )}
          </div>
        );
      })}

      {!loading && goals.length === 0 && (
        <div className="text-center py-12">
          <Target className="w-12 h-12 text-surface-300 mx-auto mb-3" />
          <p className="text-surface-400">{t('goals.create')}</p>
        </div>
      )}
    </div>
  );
}

function CreateGoalForm({ onClose, onCreated }) {
  const { t } = useTranslation();
  const [form, setForm] = useState({ name: '', targetAmount: '', deadline: '', priority: 'medium', icon: '🎯' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await api.createGoal({ ...form, targetAmount: parseFloat(form.targetAmount), startingAmount: 0 });
      onCreated();
    } catch (err) { setError(err.message); }
    finally { setLoading(false); }
  };

  return (
    <form onSubmit={submit} className="card space-y-3 border-2 border-fulusy-200 animate-slide-up">
      {error && <div className="text-danger-500 text-sm">{error}</div>}
      <input className="input" placeholder={t('goals.name')} required value={form.name} onChange={set('name')} />
      <input type="number" className="input" dir="ltr" placeholder={t('goals.target')} required min="1"
        value={form.targetAmount} onChange={set('targetAmount')} />
      <input type="date" className="input" dir="ltr" required value={form.deadline} onChange={set('deadline')} />
      <select className="input" value={form.priority} onChange={set('priority')}>
        <option value="low">{t('goals.low')}</option>
        <option value="medium">{t('goals.medium')}</option>
        <option value="high">{t('goals.high')}</option>
      </select>
      <div className="flex gap-2">
        <button type="submit" disabled={loading} className="btn-primary flex-1">
          {loading ? '...' : t('common.save')}
        </button>
        <button type="button" onClick={onClose} className="btn-secondary">{t('common.cancel')}</button>
      </div>
    </form>
  );
}

function ContributeForm({ goalId, onDone }) {
  const { t } = useTranslation();
  const [amount, setAmount] = useState('');
  const [loading, setLoading] = useState(false);
  const today = new Date().toISOString().split('T')[0];

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await api.contributeGoal(goalId, { amount: parseFloat(amount), contributionDate: today });
      onDone();
    } catch (err) { console.error(err); }
    finally { setLoading(false); }
  };

  return (
    <form onSubmit={submit} className="flex gap-2 animate-slide-up">
      <input type="number" className="input flex-1" dir="ltr" placeholder={t('income.amount')}
        required min="0.01" step="0.01" value={amount} onChange={e => setAmount(e.target.value)} />
      <button type="submit" disabled={loading} className="btn-primary">{loading ? '...' : '✓'}</button>
    </form>
  );
}

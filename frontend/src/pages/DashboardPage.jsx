import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import BalanceHUD from '../components/BalanceHUD';
import GoalsProgressBar from '../components/GoalsProgressBar';
import CategoryBars from '../components/CategoryBars';
import { PieChart, Pie, Cell, ResponsiveContainer } from 'recharts';

const CATEGORY_COLORS_HEX = {
  essentials: '#3b82f6',
  transport: '#f59e0b',
  luxuries: '#ec4899',
  shopping: '#8b5cf6',
  other: '#6b7280',
};

export default function DashboardPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.dashboard()
      .then(setDashboard)
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="w-8 h-8 border-3 border-fulusy-500 border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  if (!dashboard) return null;

  // Pie chart data
  const pieData = Object.entries(dashboard.spendingByCategory || {})
    .filter(([, v]) => Number(v) > 0)
    .map(([k, v]) => ({ name: t(`categories.${k}`), value: Number(v), color: CATEGORY_COLORS_HEX[k] || '#6b7280' }));

  return (
    <div className="pb-20 space-y-4">
      {/* Balance HUD */}
      <BalanceHUD dashboard={dashboard} />

      {/* Action buttons */}
      <div className="grid grid-cols-2 gap-3">
        <button onClick={() => navigate('/add')} className="btn-secondary py-4 text-center font-semibold">
          📝 {t('dashboard.addExpense')}
        </button>
        <button onClick={() => navigate('/add?tab=income')} className="btn-add-money text-center">
          💰 {t('dashboard.addMoney')}
        </button>
      </div>

      {/* Goals progress bar */}
      <GoalsProgressBar dashboard={dashboard} />

      {/* Spending pie chart */}
      {pieData.length > 0 && (
        <div className="card">
          <h3 className="font-semibold text-surface-800 mb-3">{t('dashboard.thisMonth')}</h3>
          <div className="flex items-center gap-4">
            <ResponsiveContainer width={120} height={120}>
              <PieChart>
                <Pie
                  data={pieData}
                  cx="50%"
                  cy="50%"
                  innerRadius={35}
                  outerRadius={55}
                  dataKey="value"
                  stroke="none"
                >
                  {pieData.map((entry, i) => (
                    <Cell key={i} fill={entry.color} />
                  ))}
                </Pie>
              </PieChart>
            </ResponsiveContainer>
            <div className="flex-1 space-y-1.5">
              {pieData.map((entry) => (
                <div key={entry.name} className="flex items-center justify-between text-sm">
                  <div className="flex items-center gap-2">
                    <div className="w-2.5 h-2.5 rounded-full" style={{ backgroundColor: entry.color }} />
                    <span className="text-surface-600">{entry.name}</span>
                  </div>
                  <span className="font-medium">{entry.value.toLocaleString()} {t('common.le')}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Budget health bars */}
      <CategoryBars
        budgets={dashboard.budgets}
        spendingByCategory={dashboard.spendingByCategory}
      />
    </div>
  );
}

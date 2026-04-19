import { useTranslation } from 'react-i18next';

const CATEGORY_COLORS = {
  essentials: 'from-blue-400 to-blue-600',
  transport: 'from-amber-400 to-amber-600',
  luxuries: 'from-pink-400 to-pink-600',
  shopping: 'from-violet-400 to-violet-600',
  other: 'from-gray-400 to-gray-600',
};

export default function CategoryBars({ budgets, spendingByCategory }) {
  const { t } = useTranslation();

  if (!budgets || budgets.length === 0) return null;

  const formatMoney = (n) => Number(n || 0).toLocaleString('en-US', { maximumFractionDigits: 0 });

  return (
    <div className="card">
      <h3 className="font-semibold text-surface-800 mb-4">{t('budget.spent')}</h3>
      <div className="space-y-3">
        {budgets.map((b) => {
          const pct = Math.min(Number(b.pct) || 0, 100);
          const overBudget = pct >= 100;
          const nearLimit = pct >= 80 && !overBudget;
          const gradient = CATEGORY_COLORS[b.categoryId] || CATEGORY_COLORS.other;

          return (
            <div key={b.categoryId}>
              <div className="flex justify-between text-sm mb-1">
                <span className="font-medium">{t(`categories.${b.categoryId}`)}</span>
                <span className={`${overBudget ? 'text-danger-500 font-semibold' : 'text-surface-500'}`}>
                  {formatMoney(b.spent)} / {formatMoney(b.limit)} {t('common.le')}
                </span>
              </div>
              <div className="health-bar">
                <div
                  className={`health-bar-fill bg-gradient-to-r ${overBudget ? 'from-danger-400 to-danger-600' : nearLimit ? 'from-warning-400 to-warning-500' : gradient} ${overBudget ? 'animate-pulse-red' : ''}`}
                  style={{ width: `${Math.min(pct, 100)}%` }}
                />
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

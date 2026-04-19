import { useTranslation } from 'react-i18next';
import { Target } from 'lucide-react';

export default function GoalsProgressBar({ dashboard }) {
  const { t } = useTranslation();
  if (!dashboard || dashboard.activeGoalsCount === 0) return null;

  const { goalsProgressPct, goalsCurrentTotal, goalsTargetTotal, activeGoalsCount } = dashboard;
  const pct = Math.min(Number(goalsProgressPct) || 0, 100);

  const formatMoney = (n) => Number(n || 0).toLocaleString('en-US', { maximumFractionDigits: 0 });

  return (
    <div className="card">
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2">
          <Target className="w-5 h-5 text-fulusy-600" />
          <h3 className="font-semibold text-surface-800">{t('dashboard.goalsProgress')}</h3>
        </div>
        <span className="text-sm text-surface-500">
          {activeGoalsCount} {activeGoalsCount === 1 ? 'goal' : 'goals'}
        </span>
      </div>

      {/* Progress bar */}
      <div className="health-bar h-4 bg-surface-100">
        <div
          className="health-bar-fill bg-gradient-to-r from-fulusy-400 to-fulusy-600"
          style={{ width: `${pct}%` }}
        />
      </div>

      <div className="flex justify-between mt-2 text-sm">
        <span className="text-surface-500">
          {formatMoney(goalsCurrentTotal)} / {formatMoney(goalsTargetTotal)} {t('common.le')}
        </span>
        <span className="font-semibold text-fulusy-700">{pct.toFixed(1)}%</span>
      </div>
    </div>
  );
}

import { useTranslation } from 'react-i18next';
import { TrendingDown, TrendingUp, Flame } from 'lucide-react';

export default function BalanceHUD({ dashboard }) {
  const { t } = useTranslation();
  if (!dashboard) return null;

  const { currentBalance, monthSpent, monthIncome, monthSaved, currentStreak } = dashboard;

  // Determine health status based on balance relative to starting
  const ratio = dashboard.startingBalance > 0
    ? currentBalance / dashboard.startingBalance
    : 1;
  const status = ratio > 0.5 ? 'healthy' : ratio > 0.2 ? 'warning' : 'danger';

  const formatMoney = (n) => {
    const num = Number(n) || 0;
    return num.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  };

  return (
    <div className={`balance-hud texture-noise ${status}`}>
      {/* Balance amount */}
      <div className="relative z-10">
        <p className="text-white/60 text-sm font-medium">{t('dashboard.balance')}</p>
        <p className="text-white text-4xl font-display mt-1 animate-count-up tracking-tight">
          {formatMoney(currentBalance)} <span className="text-lg text-white/50">{t('common.le')}</span>
        </p>

        {/* Streak badge */}
        {currentStreak > 0 && (
          <div className="absolute top-0 end-0 flex items-center gap-1 bg-white/10 rounded-full px-3 py-1">
            <Flame className="w-4 h-4 text-orange-400" />
            <span className="text-white/80 text-sm font-medium">{currentStreak}</span>
          </div>
        )}
      </div>

      {/* Monthly stats row */}
      <div className="relative z-10 grid grid-cols-3 gap-3 mt-5">
        <StatPill
          label={t('dashboard.spent')}
          value={formatMoney(monthSpent)}
          icon={<TrendingDown className="w-3.5 h-3.5" />}
          color="text-red-300"
        />
        <StatPill
          label={t('dashboard.income')}
          value={formatMoney(monthIncome)}
          icon={<TrendingUp className="w-3.5 h-3.5" />}
          color="text-emerald-300"
        />
        <StatPill
          label={t('dashboard.saved')}
          value={formatMoney(monthSaved)}
          icon={<TrendingUp className="w-3.5 h-3.5" />}
          color="text-blue-300"
        />
      </div>
    </div>
  );
}

function StatPill({ label, value, icon, color }) {
  return (
    <div className="bg-white/10 rounded-xl px-3 py-2.5 text-center">
      <div className={`flex items-center justify-center gap-1 ${color} mb-0.5`}>
        {icon}
        <span className="text-xs">{label}</span>
      </div>
      <p className="text-white font-semibold text-sm">{value}</p>
    </div>
  );
}

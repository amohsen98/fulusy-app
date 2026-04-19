import { useTranslation } from 'react-i18next';
import { useLocation, useNavigate } from 'react-router-dom';
import { Home, PlusCircle, Target, MessageCircle, Settings } from 'lucide-react';

const NAV_ITEMS = [
  { key: 'home', path: '/', icon: Home },
  { key: 'goals', path: '/goals', icon: Target },
  { key: 'add', path: '/add', icon: PlusCircle, accent: true },
  { key: 'chat', path: '/chat', icon: MessageCircle },
  { key: 'settings', path: '/settings', icon: Settings },
];

export default function BottomNav() {
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = useNavigate();

  return (
    <nav className="fixed bottom-0 start-0 end-0 bg-white border-t border-surface-100 z-50 safe-area-pb">
      <div className="max-w-lg mx-auto flex items-center justify-around h-16">
        {NAV_ITEMS.map(({ key, path, icon: Icon, accent }) => {
          const active = location.pathname === path;
          return (
            <button
              key={key}
              onClick={() => navigate(path)}
              className={`flex flex-col items-center gap-0.5 px-3 py-1 transition-all ${
                accent
                  ? 'relative -top-3'
                  : ''
              }`}
            >
              {accent ? (
                <div className={`w-12 h-12 rounded-full flex items-center justify-center shadow-lg ${
                  active ? 'bg-fulusy-600' : 'bg-fulusy-500'
                }`}>
                  <Icon className="w-6 h-6 text-white" />
                </div>
              ) : (
                <>
                  <Icon className={`w-5 h-5 ${active ? 'text-fulusy-600' : 'text-surface-400'}`} />
                  <span className={`text-[10px] ${active ? 'text-fulusy-600 font-semibold' : 'text-surface-400'}`}>
                    {t(`nav.${key}`)}
                  </span>
                </>
              )}
            </button>
          );
        })}
      </div>
    </nav>
  );
}

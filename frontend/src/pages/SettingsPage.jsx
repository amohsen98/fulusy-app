import { useTranslation } from 'react-i18next';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { Globe, LogOut, Volume2, Bell, Shield } from 'lucide-react';

export default function SettingsPage() {
  const { t, i18n } = useTranslation();
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const toggleLanguage = () => {
    const newLang = i18n.language === 'ar' ? 'en' : 'ar';
    i18n.changeLanguage(newLang);
    document.documentElement.dir = newLang === 'ar' ? 'rtl' : 'ltr';
    document.documentElement.lang = newLang;
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="pb-20 space-y-4">
      <h2 className="font-display text-2xl">{t('nav.settings')}</h2>

      {/* User info */}
      <div className="card">
        <div className="flex items-center gap-4">
          <div className="w-14 h-14 rounded-full bg-gradient-to-br from-fulusy-400 to-fulusy-700 flex items-center justify-center text-white font-display text-xl">
            {user?.name?.charAt(0) || '?'}
          </div>
          <div>
            <p className="font-semibold text-lg">{user?.name}</p>
            <p className="text-sm text-surface-400">{user?.email}</p>
          </div>
        </div>
      </div>

      {/* Language */}
      <div className="card">
        <button onClick={toggleLanguage} className="flex items-center justify-between w-full">
          <div className="flex items-center gap-3">
            <Globe className="w-5 h-5 text-fulusy-600" />
            <span className="font-medium">
              {i18n.language === 'ar' ? 'اللغة / Language' : 'Language / اللغة'}
            </span>
          </div>
          <span className="bg-surface-100 px-3 py-1 rounded-full text-sm font-medium">
            {i18n.language === 'ar' ? 'عربي 🇪🇬' : 'English 🇬🇧'}
          </span>
        </button>
      </div>

      {/* Notification settings placeholder */}
      <div className="card">
        <div className="space-y-4">
          <SettingRow icon={<Bell className="w-5 h-5 text-fulusy-600" />} label="Daily reminder" sublabel="9:00 PM" />
          <SettingRow icon={<Volume2 className="w-5 h-5 text-fulusy-600" />} label="Sounds" sublabel="Opt-in" />
          <SettingRow icon={<Shield className="w-5 h-5 text-fulusy-600" />} label="Penalty intensity" sublabel="Standard" />
        </div>
      </div>

      {/* App info */}
      <div className="card text-center">
        <p className="font-display text-xl text-fulusy-700 mb-1">فلوسي</p>
        <p className="text-xs text-surface-400">v0.1.0 • Made with ☕ in Egypt</p>
      </div>

      {/* Logout */}
      <button onClick={handleLogout} className="btn-danger w-full flex items-center justify-center gap-2">
        <LogOut className="w-5 h-5" /> {i18n.language === 'ar' ? 'تسجيل خروج' : 'Log out'}
      </button>
    </div>
  );
}

function SettingRow({ icon, label, sublabel }) {
  return (
    <div className="flex items-center justify-between">
      <div className="flex items-center gap-3">
        {icon}
        <span className="font-medium text-sm">{label}</span>
      </div>
      <span className="text-sm text-surface-400">{sublabel}</span>
    </div>
  );
}

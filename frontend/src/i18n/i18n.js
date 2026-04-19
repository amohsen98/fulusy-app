import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

const resources = {
  ar: {
    translation: {
      app: { name: 'فلوسي', tagline: 'تتبع فلوسك زي اللعبة' },
      nav: { home: 'الرئيسية', add: 'أضف', goals: 'الأهداف', chat: 'المساعد', settings: 'الإعدادات' },
      auth: {
        login: 'تسجيل دخول', register: 'حساب جديد', email: 'البريد الإلكتروني',
        password: 'كلمة المرور', name: 'اسمك', startingBalance: 'رصيدك الحالي',
        incomeMode: 'نوع الدخل', fixed: 'ثابت', variable: 'متغير', hybrid: 'مختلط',
        fixedAmount: 'مبلغ الراتب', fixedDay: 'يوم الراتب', submit: 'يلا نبدأ',
        loginBtn: 'دخول', noAccount: 'مفيش حساب؟', hasAccount: 'عندك حساب؟',
      },
      dashboard: {
        balance: 'رصيدك', spent: 'صرفت', income: 'دخل', saved: 'وفرت',
        thisMonth: 'الشهر ده', topCategory: 'أكتر حاجة صرفت عليها',
        goalsProgress: 'تقدم الأهداف', streak: 'أيام متتالية',
        addExpense: 'سجل مصروف', addMoney: '+ فلوس جديدة',
      },
      categories: {
        essentials: 'الأساسيات', transport: 'مواصلات', luxuries: 'رفاهيات',
        shopping: 'مشتريات', other: 'أخرى',
      },
      sources: {
        salary: 'مرتب', freelance: 'فريلانس', gift: 'هدية',
        gam3eya_payout: 'جمعية', bonus: 'بونص', other: 'أخرى',
      },
      expense: {
        title: 'سجل مصروف', amount: 'المبلغ', category: 'التصنيف',
        note: 'ملاحظة (اختياري)', date: 'التاريخ', save: 'سجل',
        recurring: 'متكرر شهريًا',
      },
      income: {
        title: '+ فلوس جديدة', amount: 'المبلغ', source: 'المصدر',
        note: 'ملاحظة (اختياري)', date: 'التاريخ', save: 'أضف',
      },
      goals: {
        title: 'الأهداف', create: 'هدف جديد', name: 'اسم الهدف',
        target: 'المبلغ المطلوب', deadline: 'الموعد النهائي',
        priority: 'الأولوية', low: 'منخفضة', medium: 'متوسطة', high: 'عالية',
        progress: 'التقدم', remaining: 'باقي', perMonth: 'المطلوب شهريًا',
        contribute: 'ساهم', achieved: 'تم!', failed: 'فات الموعد',
        daysLeft: 'يوم باقي',
      },
      chat: {
        title: 'المساعد المالي', placeholder: 'اسأل عن مصاريفك...',
        send: 'أرسل', locked: 'المساعد مقفل بسبب أهداف فايتة',
      },
      budget: { limit: 'الحد الأقصى', of: 'من', spent: 'مصروف' },
      common: { le: 'LE', save: 'حفظ', cancel: 'إلغاء', delete: 'حذف', loading: 'جاري التحميل...' },
    },
  },
  en: {
    translation: {
      app: { name: 'Fulusy', tagline: 'Track your money like a game' },
      nav: { home: 'Home', add: 'Add', goals: 'Goals', chat: 'Assistant', settings: 'Settings' },
      auth: {
        login: 'Log In', register: 'Register', email: 'Email',
        password: 'Password', name: 'Your name', startingBalance: 'Current balance',
        incomeMode: 'Income type', fixed: 'Fixed', variable: 'Variable', hybrid: 'Hybrid',
        fixedAmount: 'Salary amount', fixedDay: 'Salary day', submit: "Let's go!",
        loginBtn: 'Log In', noAccount: "Don't have an account?", hasAccount: 'Already have an account?',
      },
      dashboard: {
        balance: 'Balance', spent: 'Spent', income: 'Income', saved: 'Saved',
        thisMonth: 'This month', topCategory: 'Top spending category',
        goalsProgress: 'Goals progress', streak: 'Day streak',
        addExpense: 'Log expense', addMoney: '+ Add Money',
      },
      categories: {
        essentials: 'Essentials', transport: 'Transport', luxuries: 'Luxuries',
        shopping: 'Shopping', other: 'Other',
      },
      sources: {
        salary: 'Salary', freelance: 'Freelance', gift: 'Gift',
        gam3eya_payout: "Gam3eya payout", bonus: 'Bonus', other: 'Other',
      },
      expense: {
        title: 'Log Expense', amount: 'Amount', category: 'Category',
        note: 'Note (optional)', date: 'Date', save: 'Save',
        recurring: 'Recurring monthly',
      },
      income: {
        title: '+ Add Money', amount: 'Amount', source: 'Source',
        note: 'Note (optional)', date: 'Date', save: 'Add',
      },
      goals: {
        title: 'Goals', create: 'New Goal', name: 'Goal name',
        target: 'Target amount', deadline: 'Deadline',
        priority: 'Priority', low: 'Low', medium: 'Medium', high: 'High',
        progress: 'Progress', remaining: 'Remaining', perMonth: 'Required/month',
        contribute: 'Contribute', achieved: 'Done!', failed: 'Missed',
        daysLeft: 'days left',
      },
      chat: {
        title: 'Financial Assistant', placeholder: 'Ask about your spending...',
        send: 'Send', locked: 'Assistant locked due to missed goals',
      },
      budget: { limit: 'Limit', of: 'of', spent: 'spent' },
      common: { le: 'LE', save: 'Save', cancel: 'Cancel', delete: 'Delete', loading: 'Loading...' },
    },
  },
};

i18n.use(initReactI18next).init({
  resources,
  lng: 'ar',
  fallbackLng: 'en',
  interpolation: { escapeValue: false },
});

export default i18n;

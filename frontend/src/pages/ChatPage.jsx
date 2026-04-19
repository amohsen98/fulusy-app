import { useState, useEffect, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { api } from '../services/api';
import { Send, Bot, User, Lock } from 'lucide-react';

export default function ChatPage() {
  const { t } = useTranslation();
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [locked, setLocked] = useState(false);
  const [historyLoaded, setHistoryLoaded] = useState(false);
  const bottomRef = useRef(null);

  useEffect(() => {
    api.chatHistory(30)
      .then((history) => {
        setMessages(history.reverse());
        setHistoryLoaded(true);
      })
      .catch(console.error);

    api.dashboard().then((d) => {
      setLocked(d.chatbotLocked);
    }).catch(() => {});
  }, []);

  useEffect(() => {
    if (bottomRef.current) {
      bottomRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [messages]);

  const handleSend = async (e) => {
    e.preventDefault();
    if (!input.trim() || loading || locked) return;

    const userMsg = input.trim();
    setInput('');
    setMessages((prev) => [...prev, { role: 'user', content: userMsg, createdAt: new Date().toISOString() }]);
    setLoading(true);

    try {
      const res = await api.chat(userMsg);
      setMessages((prev) => [...prev, { role: 'assistant', content: res.reply, createdAt: res.timestamp }]);
    } catch (err) {
      setMessages((prev) => [...prev, {
        role: 'assistant',
        content: err.message || 'Something went wrong',
        createdAt: new Date().toISOString(),
        error: true,
      }]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="pb-20 flex flex-col h-[calc(100vh-8rem)]">
      {/* Header */}
      <div className="flex items-center gap-3 mb-4">
        <div className="w-10 h-10 rounded-full bg-gradient-to-br from-fulusy-400 to-fulusy-700 flex items-center justify-center">
          <Bot className="w-5 h-5 text-white" />
        </div>
        <div>
          <h2 className="font-semibold">{t('chat.title')}</h2>
          <p className="text-xs text-surface-400">Powered by Gemini</p>
        </div>
      </div>

      {/* Locked banner */}
      {locked && (
        <div className="bg-red-50 border border-red-200 rounded-xl p-4 mb-4 flex items-center gap-3">
          <Lock className="w-5 h-5 text-danger-500 flex-shrink-0" />
          <p className="text-sm text-danger-600">{t('chat.locked')}</p>
        </div>
      )}

      {/* Messages */}
      <div className="flex-1 overflow-y-auto space-y-3 px-1">
        {!historyLoaded && (
          <div className="text-center py-8 text-surface-400">{t('common.loading')}</div>
        )}

        {historyLoaded && messages.length === 0 && (
          <div className="text-center py-12">
            <Bot className="w-12 h-12 text-surface-300 mx-auto mb-3" />
            <p className="text-surface-400 text-sm">
              {t('chat.placeholder')}
            </p>
            <div className="mt-4 space-y-2">
              {['ما أكثر حاجة صرفت عليها الشهر ده؟', 'How can I save more?', 'إيه الفئة اللي ممكن أقلل منها؟'].map((q) => (
                <button
                  key={q}
                  onClick={() => setInput(q)}
                  className="block mx-auto bg-surface-100 hover:bg-surface-200 text-surface-700 text-sm px-4 py-2 rounded-xl transition-colors"
                >
                  {q}
                </button>
              ))}
            </div>
          </div>
        )}

        {messages.map((msg, i) => (
          <div key={i} className={`flex gap-2 animate-fade-in ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
            {msg.role === 'assistant' && (
              <div className="w-7 h-7 rounded-full bg-fulusy-100 flex items-center justify-center flex-shrink-0 mt-1">
                <Bot className="w-4 h-4 text-fulusy-600" />
              </div>
            )}
            <div
              className={`max-w-[80%] rounded-2xl px-4 py-2.5 text-sm leading-relaxed ${
                msg.role === 'user'
                  ? 'bg-fulusy-600 text-white rounded-br-md'
                  : msg.error
                    ? 'bg-red-50 text-danger-600 rounded-bl-md'
                    : 'bg-surface-100 text-surface-800 rounded-bl-md'
              }`}
              style={{ whiteSpace: 'pre-wrap' }}
            >
              {msg.content}
            </div>
            {msg.role === 'user' && (
              <div className="w-7 h-7 rounded-full bg-surface-200 flex items-center justify-center flex-shrink-0 mt-1">
                <User className="w-4 h-4 text-surface-600" />
              </div>
            )}
          </div>
        ))}

        {loading && (
          <div className="flex gap-2 items-center animate-fade-in">
            <div className="w-7 h-7 rounded-full bg-fulusy-100 flex items-center justify-center">
              <Bot className="w-4 h-4 text-fulusy-600" />
            </div>
            <div className="bg-surface-100 rounded-2xl rounded-bl-md px-4 py-3">
              <div className="flex gap-1.5">
                <div className="w-2 h-2 bg-surface-400 rounded-full animate-bounce" style={{ animationDelay: '0ms' }} />
                <div className="w-2 h-2 bg-surface-400 rounded-full animate-bounce" style={{ animationDelay: '150ms' }} />
                <div className="w-2 h-2 bg-surface-400 rounded-full animate-bounce" style={{ animationDelay: '300ms' }} />
              </div>
            </div>
          </div>
        )}

        <div ref={bottomRef} />
      </div>

      {/* Input */}
      <form onSubmit={handleSend} className="mt-3 flex gap-2">
        <input
          className="input flex-1"
          placeholder={t('chat.placeholder')}
          value={input}
          onChange={(e) => setInput(e.target.value)}
          disabled={locked || loading}
        />
        <button
          type="submit"
          disabled={!input.trim() || loading || locked}
          className="btn-primary px-4"
        >
          <Send className="w-5 h-5" />
        </button>
      </form>
    </div>
  );
}

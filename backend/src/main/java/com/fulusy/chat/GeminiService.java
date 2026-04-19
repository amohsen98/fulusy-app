package com.fulusy.chat;

import com.fulusy.dashboard.DashboardResponse;
import com.fulusy.dashboard.DashboardService;
import com.fulusy.expense.Expense;
import com.fulusy.goal.Goal;
import com.fulusy.user.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class GeminiService {

    private static final Logger LOG = Logger.getLogger(GeminiService.class);

    @ConfigProperty(name = "gemini.api.key", defaultValue = "")
    String apiKey;

    @ConfigProperty(name = "gemini.api.base-url")
    String baseUrl;

    @ConfigProperty(name = "gemini.model")
    String model;

    @Inject DashboardService dashboardService;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Transactional
    public ChatResponse chat(Long userId, String userMessage) {
        // Detect language
        String language = isArabic(userMessage) ? "ar" : "en";

        // Build financial context
        String context = buildContext(userId, language);

        // Build Gemini request
        String prompt = context + "\n\nUser question: " + userMessage;

        String systemInstruction;
        if ("ar".equals(language)) {
            systemInstruction = "أنت مساعد مالي شخصي لتطبيق فلوسي. "
                    + "أجب بالعربية المصرية بشكل مختصر ومفيد. "
                    + "اعتمد على البيانات المالية المقدمة فقط. "
                    + "لا تخترع أرقام. إذا لم تعرف الإجابة قل 'مش عارف أجاوب على ده من البيانات اللي عندي'.";
        } else {
            systemInstruction = "You are a personal finance assistant for the Fulusy app. "
                    + "Answer concisely based only on the financial data provided. "
                    + "Never invent numbers. If you can't answer, say so.";
        }

        String reply;
        try {
            reply = callGemini(systemInstruction, prompt);
        } catch (Exception e) {
            LOG.error("Gemini API error", e);
            reply = "ar".equals(language)
                    ? "حصل مشكلة في الاتصال بالذكاء الاصطناعي. حاول تاني."
                    : "Failed to connect to AI service. Please try again.";
        }

        // Persist both messages
        persistMessage(userId, "user", userMessage, language);
        persistMessage(userId, "assistant", reply, language);

        return new ChatResponse(reply, language, LocalDateTime.now());
    }

    private String buildContext(Long userId, String language) {
        DashboardResponse d = dashboardService.getDashboard(userId);
        User user = User.findById(userId);

        // Recent expenses (last 10)
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
        List<Expense> recent = Expense.findByUserInMonth(userId, monthStart, monthEnd);
        String recentExpenses = recent.stream().limit(10)
                .map(e -> e.expenseDate + ": " + e.amount + " LE (" + e.categoryId + ")"
                        + (e.note != null ? " - " + e.note : ""))
                .collect(Collectors.joining("\n"));

        // Active goals
        List<Goal> goals = Goal.findActiveByUser(userId);
        String goalsSummary = goals.stream()
                .map(g -> g.name + ": " + g.currentAmount + "/" + g.targetAmount + " LE (deadline: " + g.deadline + ")")
                .collect(Collectors.joining("\n"));

        // Spending breakdown
        String categoryBreakdown = d.spendingByCategory().entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(e -> e.getKey() + ": " + e.getValue() + " LE")
                .collect(Collectors.joining(", "));

        StringBuilder sb = new StringBuilder();
        sb.append("=== Financial Data for ").append(user.name).append(" ===\n");
        sb.append("Currency: LE (Egyptian Pound)\n");
        sb.append("Current balance: ").append(d.currentBalance()).append(" LE\n");
        sb.append("This month spent: ").append(d.monthSpent()).append(" LE\n");
        sb.append("This month income: ").append(d.monthIncome()).append(" LE\n");
        sb.append("This month saved: ").append(d.monthSaved()).append(" LE\n");
        sb.append("Spending by category: ").append(categoryBreakdown).append("\n");
        sb.append("Top spending category: ").append(d.topCategory() != null ? d.topCategory() : "none").append("\n");
        sb.append("Streak: ").append(d.currentStreak()).append(" days\n");
        sb.append("\nRecent expenses this month:\n").append(recentExpenses.isEmpty() ? "none yet" : recentExpenses);
        sb.append("\n\nActive goals:\n").append(goalsSummary.isEmpty() ? "none" : goalsSummary);

        return sb.toString();
    }

    private String callGemini(String systemInstruction, String userPrompt) throws Exception {
        if (apiKey == null || apiKey.isBlank()) {
            return "AI chatbot not configured. Set GEMINI_API_KEY environment variable.";
        }

        String url = baseUrl + "/v1beta/models/" + model + ":generateContent?key=" + apiKey;

        // Escape JSON strings
        String escapedSystem = escapeJson(systemInstruction);
        String escapedPrompt = escapeJson(userPrompt);

        String requestBody = """
                {
                  "system_instruction": {
                    "parts": [{"text": "%s"}]
                  },
                  "contents": [
                    {
                      "role": "user",
                      "parts": [{"text": "%s"}]
                    }
                  ],
                  "generationConfig": {
                    "maxOutputTokens": 1024,
                    "temperature": 0.3
                  }
                }
                """.formatted(escapedSystem, escapedPrompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            LOG.error("Gemini returned " + response.statusCode() + ": " + response.body());
            throw new RuntimeException("Gemini API returned status " + response.statusCode());
        }

        // Parse response - extract text from candidates[0].content.parts[0].text
        String body = response.body();
        return extractTextFromGeminiResponse(body);
    }

    private String extractTextFromGeminiResponse(String json) {
        // Simple extraction without a JSON library dependency
        // Looking for: "text": "..." inside candidates[0].content.parts[0]
        int textIdx = json.indexOf("\"text\"");
        if (textIdx == -1) return "No response from AI.";

        // Find the value after "text":
        int colonIdx = json.indexOf(":", textIdx);
        int startQuote = json.indexOf("\"", colonIdx + 1);
        if (startQuote == -1) return "No response from AI.";

        // Find end quote, handling escaped quotes
        StringBuilder sb = new StringBuilder();
        int i = startQuote + 1;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                if (next == '"') { sb.append('"'); i += 2; continue; }
                if (next == 'n') { sb.append('\n'); i += 2; continue; }
                if (next == 't') { sb.append('\t'); i += 2; continue; }
                if (next == '\\') { sb.append('\\'); i += 2; continue; }
                sb.append(c);
                i++;
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private boolean isArabic(String text) {
        return text.codePoints().anyMatch(cp ->
                Character.UnicodeBlock.of(cp) == Character.UnicodeBlock.ARABIC ||
                Character.UnicodeBlock.of(cp) == Character.UnicodeBlock.ARABIC_SUPPLEMENT);
    }

    private void persistMessage(Long userId, String role, String content, String language) {
        ChatMessage msg = new ChatMessage();
        msg.userId = userId;
        msg.role = role;
        msg.content = content;
        msg.language = language;
        msg.persist();
    }
}

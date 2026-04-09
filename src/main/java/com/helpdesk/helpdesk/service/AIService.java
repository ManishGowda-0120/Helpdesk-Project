package com.helpdesk.helpdesk.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.groq.com")
            .build();

    // ─── System Prompt (makes AI behave like IT support) ─────────────────────
    private static final String SYSTEM_PROMPT =
            "You are an expert IT support assistant. " +
                    "You help employees with technical issues clearly and concisely. " +
                    "Always follow the exact output format requested.";

    // ─── 1. Generate Response ─────────────────────────────────────────────────
    public String generateResponse(String issue) {
        String prompt =
                "An employee reported this IT issue: \"" + issue + "\"\n" +
                        "Write a helpful IT support response in 2-3 sentences. " +
                        "Give practical steps to resolve it. Do not add any extra text.";
        return callGroq(prompt, 150);
    }

    // ─── 2. Suggest Category ─────────────────────────────────────────────────
    public String suggestCategory(String issue) {
        String prompt =
                "Classify this IT issue into exactly ONE category.\n" +
                        "Choose only from: Hardware, Software, Network, Access\n" +
                        "IT Issue: \"" + issue + "\"\n" +
                        "Rules:\n" +
                        "- Hardware = physical device problems (keyboard, monitor, printer, laptop)\n" +
                        "- Software = app crashes, installation, OS issues\n" +
                        "- Network = internet, VPN, WiFi, connectivity\n" +
                        "- Access = login, password, permissions\n" +
                        "Reply with only the single category word. Nothing else.";
        return callGroq(prompt, 5);
    }

    // ─── 3. Suggest Priority ─────────────────────────────────────────────────
    public String suggestPriority(String issue) {
        String prompt =
                "Classify the priority of this IT issue into exactly ONE word.\n" +
                        "Choose only from: HIGH, MEDIUM, LOW\n" +
                        "IT Issue: \"" + issue + "\"\n" +
                        "Rules:\n" +
                        "- HIGH = blocking work completely (can't login, server down, data loss)\n" +
                        "- MEDIUM = affecting work but workaround exists (slow system, one app broken)\n" +
                        "- LOW = minor inconvenience (cosmetic issue, non-urgent request)\n" +
                        "Reply with only the single priority word. Nothing else.";
        return callGroq(prompt, 5);
    }

    // ─── 4. Chatbot (no DB save) ──────────────────────────────────────────────
    public String chat(String userMessage) {
        String prompt =
                "An employee asked: \"" + userMessage + "\"\n" +
                        "Give a clear, helpful IT support answer. " +
                        "If it is a greeting or non-IT question, respond politely and ask how you can help with IT.";
        return callGroq(prompt, 200);
    }

    // ─── Common Groq API Caller ───────────────────────────────────────────────
    private String callGroq(String userPrompt, int maxTokens) {
        try {
            if (apiKey == null || apiKey.isBlank()) {
                return "Error: GROQ_API_KEY not set in environment variables.";
            }

            Map<String, Object> requestBody = Map.of(
                    "model", "llama-3.3-70b-versatile",
                    "max_tokens", maxTokens,
                    "messages", List.of(
                            // System message makes AI behave consistently
                            Map.of("role", "system", "content", SYSTEM_PROMPT),
                            Map.of("role", "user",   "content", userPrompt)
                    )
            );

            Map response = webClient.post()
                    .uri("/openai/v1/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            Map choice = (Map) ((List) response.get("choices")).get(0);
            Map message = (Map) choice.get("message");
            return message.get("content").toString().trim();

        } catch (WebClientResponseException e) {
            System.err.println("❌ HTTP Status : " + e.getStatusCode());
            System.err.println("❌ Error Body  : " + e.getResponseBodyAsString());
            String err = "AI error: " + e.getResponseBodyAsString();
            return err.length() > 200 ? err.substring(0, 200) : err;

        } catch (Exception e) {
            System.err.println("❌ General Error: " + e.getMessage());
            String err = "AI error: " + e.getMessage();
            return err.length() > 200 ? err.substring(0, 200) : err;
        }
    }
    public Map<String, Object> chatWithTools(String userMessage, List<Map<String, Object>> tools) {

        try {
            Map<String, Object> requestBody = Map.of(
                    "model", "llama-3.3-70b-versatile",
                    "messages", List.of(
                            Map.of("role", "system", "content",
                                    "You are an IT helpdesk assistant.\n" +
                                            "If user reports an issue, call create_ticket.\n" +
                                            "If user asks about tickets, call appropriate tool.\n" +
                                            "Do NOT respond with plain text if a tool can be used."
                            ),
                            Map.of("role", "user", "content", userMessage)
                    ),
                    "tools", tools,
                    "tool_choice", "auto"   // 🔥 IMPORTANT
            );

            Map response = webClient.post()
                    .uri("/openai/v1/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return response;

        } catch (Exception e) {
            throw new RuntimeException("AI Tool call failed: " + e.getMessage());
        }
    }
    public Map<String, Object> extractToolCall(Map response) {

        Map choice = (Map) ((List) response.get("choices")).get(0);
        Map message = (Map) choice.get("message");

        // 🔥 If AI wants to call a tool
        if (message.containsKey("tool_calls")) {

            List toolCalls = (List) message.get("tool_calls");
            Map toolCall = (Map) toolCalls.get(0);
            Map function = (Map) toolCall.get("function");

            String name = function.get("name").toString();
            String arguments = function.get("arguments").toString();

            return Map.of(
                    "isToolCall", true,
                    "toolName", name,
                    "arguments", arguments
            );
        }

        // Normal text fallback
        return Map.of(
                "isToolCall", false,
                "message", message.get("content").toString()
        );
    }
}
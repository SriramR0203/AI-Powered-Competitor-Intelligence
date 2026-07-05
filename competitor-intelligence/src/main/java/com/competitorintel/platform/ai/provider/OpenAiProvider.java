package com.competitorintel.platform.ai.provider;

import com.competitorintel.platform.ai.AiProvider;
import com.competitorintel.platform.ai.AiRequest;
import com.competitorintel.platform.ai.AiResponse;
import com.competitorintel.platform.config.AppProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiProvider implements AiProvider {

    private static final String NAME = "openai";
    private final AppProperties appProperties;
    private final ObjectMapper  objectMapper;

    @Override public String  getProviderName() { return NAME; }
    @Override public boolean isAvailable() {
        return StringUtils.hasText(appProperties.getAi().getOpenai().getApiKey());
    }

    @Override
    public AiResponse complete(AiRequest req) {
        if (!isAvailable()) return AiResponse.failure(NAME, "OpenAI API key not configured");
        AppProperties.Ai.OpenAi cfg = appProperties.getAi().getOpenai();
        try {
            ObjectNode body = objectMapper.createObjectNode();
            body.put("model", cfg.getModel());
            body.put("temperature", req.getTemperature());
            body.put("max_tokens", req.getMaxTokens());

            ArrayNode messages = body.putArray("messages");
            if (StringUtils.hasText(req.getSystemPrompt())) {
                messages.addObject().put("role", "system").put("content", req.getSystemPrompt());
            }
            messages.addObject().put("role", "user").put("content", req.getUserPrompt());

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(cfg.getTimeoutSeconds())).build();
            HttpRequest httpReq = HttpRequest.newBuilder()
                    .uri(URI.create(cfg.getBaseUrl() + "/chat/completions"))
                    .header("Authorization", "Bearer " + cfg.getApiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .timeout(Duration.ofSeconds(cfg.getTimeoutSeconds()))
                    .build();

            HttpResponse<String> resp = client.send(httpReq, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                log.error("OpenAI error status={}", resp.statusCode());
                return AiResponse.failure(NAME, "OpenAI returned status " + resp.statusCode());
            }
            JsonNode root    = objectMapper.readTree(resp.body());
            String   content = root.path("choices").path(0).path("message").path("content").asText();
            return AiResponse.builder()
                    .content(content).provider(NAME).model(cfg.getModel())
                    .inputTokens(root.path("usage").path("prompt_tokens").asInt())
                    .outputTokens(root.path("usage").path("completion_tokens").asInt())
                    .success(true).build();
        } catch (Exception e) {
            log.error("OpenAI call failed: {}", e.getMessage());
            return AiResponse.failure(NAME, e.getMessage());
        }
    }
}

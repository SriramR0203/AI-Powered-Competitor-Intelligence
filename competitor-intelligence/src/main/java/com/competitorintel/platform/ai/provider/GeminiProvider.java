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
public class GeminiProvider implements AiProvider {

    private static final String NAME = "gemini";
    private final AppProperties appProperties;
    private final ObjectMapper  objectMapper;

    @Override public String  getProviderName() { return NAME; }
    @Override public boolean isAvailable() {
        return StringUtils.hasText(appProperties.getAi().getGemini().getApiKey());
    }

    @Override
    public AiResponse complete(AiRequest req) {
        if (!isAvailable()) return AiResponse.failure(NAME, "Gemini API key not configured");
        AppProperties.Ai.Gemini cfg = appProperties.getAi().getGemini();
        try {
            String prompt = StringUtils.hasText(req.getSystemPrompt())
                    ? req.getSystemPrompt() + "\n\n" + req.getUserPrompt()
                    : req.getUserPrompt();

            ObjectNode body = objectMapper.createObjectNode();
            ArrayNode  contents = body.putArray("contents");
            ArrayNode  parts    = contents.addObject().put("role", "user").putArray("parts");
            parts.addObject().put("text", prompt);
            ObjectNode genCfg = body.putObject("generationConfig");
            genCfg.put("temperature", req.getTemperature());
            genCfg.put("maxOutputTokens", req.getMaxTokens());

            String url = cfg.getBaseUrl() + "/models/" + cfg.getModel()
                    + ":generateContent?key=" + cfg.getApiKey();
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(cfg.getTimeoutSeconds())).build();
            HttpRequest httpReq = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .timeout(Duration.ofSeconds(cfg.getTimeoutSeconds()))
                    .build();

            HttpResponse<String> resp = client.send(httpReq, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                log.error("Gemini error status={}", resp.statusCode());
                return AiResponse.failure(NAME, "Gemini returned status " + resp.statusCode());
            }
            JsonNode root    = objectMapper.readTree(resp.body());
            String   content = root.path("candidates").path(0)
                    .path("content").path("parts").path(0).path("text").asText();
            return AiResponse.builder()
                    .content(content).provider(NAME).model(cfg.getModel())
                    .inputTokens(root.path("usageMetadata").path("promptTokenCount").asInt())
                    .outputTokens(root.path("usageMetadata").path("candidatesTokenCount").asInt())
                    .success(true).build();
        } catch (Exception e) {
            log.error("Gemini call failed: {}", e.getMessage());
            return AiResponse.failure(NAME, e.getMessage());
        }
    }
}

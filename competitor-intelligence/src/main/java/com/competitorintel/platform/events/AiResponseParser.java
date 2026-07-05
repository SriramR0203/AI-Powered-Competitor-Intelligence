package com.competitorintel.platform.events;

import com.competitorintel.platform.domain.enums.IntelligenceCategory;
import com.competitorintel.platform.domain.enums.SentimentType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the raw AI text response into a structured EnrichmentResult.
 * Resilient: falls back to defaults when parsing fails.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiResponseParser {

    private static final Pattern JSON_FENCE  = Pattern.compile("```json\\s*(.*?)\\s*```", Pattern.DOTALL);
    private static final Pattern JSON_OBJECT = Pattern.compile("\\{.*}", Pattern.DOTALL);

    private final ObjectMapper objectMapper;

    public EnrichmentResult parse(String raw) {
        if (!StringUtils.hasText(raw)) return defaultResult(raw);
        try {
            String json = extractJson(raw);
            if (json != null) return parseJson(json, raw);
        } catch (Exception e) {
            log.warn("Failed to parse AI response as JSON: {}", e.getMessage());
        }
        return defaultResult(raw);
    }

    private String extractJson(String text) {
        Matcher fence = JSON_FENCE.matcher(text);
        if (fence.find()) return fence.group(1);
        Matcher inline = JSON_OBJECT.matcher(text);
        if (inline.find()) return inline.group();
        return null;
    }

    private EnrichmentResult parseJson(String json, String raw) throws Exception {
        JsonNode node = objectMapper.readTree(json);

        String summary    = text(node, "summary", "aiSummary");
        String catStr     = text(node, "category");
        String sentStr    = text(node, "sentiment");
        String insights   = text(node, "keyInsights", "insights");
        Double sentScore  = dbl(node, "sentimentScore");
        Double relScore   = dbl(node, "relevanceScore");
        Double impScore   = dbl(node, "importanceScore");

        IntelligenceCategory category = parseEnum(IntelligenceCategory.class, catStr,
                IntelligenceCategory.UNCLASSIFIED);
        SentimentType sentiment = parseEnum(SentimentType.class, sentStr, SentimentType.NEUTRAL);

        List<String> tags = new ArrayList<>();
        JsonNode tagsNode = node.get("tags");
        if (tagsNode != null && tagsNode.isArray()) {
            for (JsonNode t : tagsNode) {
                if (t.isTextual() && StringUtils.hasText(t.asText()))
                    tags.add(t.asText().toLowerCase().trim());
            }
        }

        return EnrichmentResult.builder()
                .aiSummary(summary).category(category).sentiment(sentiment)
                .sentimentScore(sentScore).relevanceScore(relScore).importanceScore(impScore)
                .keyInsights(insights).tags(tags).parsed(true).rawResponse(raw).build();
    }

    private EnrichmentResult defaultResult(String raw) {
        String summary = (raw != null && raw.length() > 500) ? raw.substring(0, 500) : raw;
        return EnrichmentResult.builder()
                .aiSummary(summary)
                .category(IntelligenceCategory.UNCLASSIFIED)
                .sentiment(SentimentType.NEUTRAL)
                .sentimentScore(0.0).relevanceScore(0.5).importanceScore(0.5)
                .tags(List.of()).parsed(false).rawResponse(raw).build();
    }

    private String text(JsonNode n, String... keys) {
        for (String k : keys) {
            JsonNode v = n.get(k);
            if (v != null && v.isTextual() && StringUtils.hasText(v.asText())) return v.asText();
        }
        return null;
    }

    private Double dbl(JsonNode n, String key) {
        JsonNode v = n.get(key);
        return (v != null && v.isNumber()) ? v.asDouble() : null;
    }

    private <E extends Enum<E>> E parseEnum(Class<E> cls, String val, E def) {
        if (!StringUtils.hasText(val)) return def;
        try {
            return Enum.valueOf(cls, val.toUpperCase().replace(" ", "_").replace("-", "_"));
        } catch (IllegalArgumentException e) { return def; }
    }
}

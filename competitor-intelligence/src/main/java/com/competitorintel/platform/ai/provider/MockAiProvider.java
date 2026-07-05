package com.competitorintel.platform.ai.provider;

import com.competitorintel.platform.ai.AiProvider;
import com.competitorintel.platform.ai.AiRequest;
import com.competitorintel.platform.ai.AiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Mock AI provider — always available, used in dev/test when no real API keys are configured.
 * Returns a valid JSON body that satisfies the enrichment parser.
 */
@Slf4j
@Component
public class MockAiProvider implements AiProvider {

    private static final String PROVIDER_NAME = "mock";

    @Override public String  getProviderName() { return PROVIDER_NAME; }
    @Override public boolean isAvailable()      { return true; }

    @Override
    public AiResponse complete(AiRequest request) {
        log.debug("MockAiProvider returning simulated AI response");
        return AiResponse.builder()
                .content(mockJson())
                .provider(PROVIDER_NAME)
                .model("mock-1.0")
                .inputTokens(100)
                .outputTokens(150)
                .success(true)
                .build();
    }

    private String mockJson() {
        return """
                {
                  "summary": "Mock AI-generated summary of the competitor intelligence event.",
                  "category": "GENERAL_NEWS",
                  "sentiment": "NEUTRAL",
                  "sentimentScore": 0.0,
                  "relevanceScore": 0.65,
                  "importanceScore": 0.55,
                  "keyInsights": "Mock insight 1. Mock insight 2.",
                  "tags": ["product", "technology"]
                }
                """;
    }
}

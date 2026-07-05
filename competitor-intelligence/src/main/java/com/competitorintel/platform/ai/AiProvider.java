package com.competitorintel.platform.ai;

/**
 * Strategy interface for all AI provider implementations.
 */
public interface AiProvider {
    /** Unique lower-case identifier: "openai", "gemini", "mock". */
    String getProviderName();
    /** Returns true when the provider is configured and ready (API key present). */
    boolean isAvailable();
    /** Send a completion request and return a normalised response. */
    AiResponse complete(AiRequest request);
}

package com.competitorintel.platform.ai;

import com.competitorintel.platform.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Facade over all AI providers.
 * Resolves the configured provider; falls back through openai → gemini → mock.
 */
@Slf4j
@Service
public class AiService {

    private final AppProperties         appProperties;
    private final Map<String, AiProvider> providers;

    public AiService(AppProperties appProperties, List<AiProvider> providerList) {
        this.appProperties = appProperties;
        this.providers = providerList.stream()
                .collect(Collectors.toMap(AiProvider::getProviderName, Function.identity()));
        log.info("AI providers registered: {}", this.providers.keySet());
    }

    public AiResponse complete(AiRequest request) {
        AiProvider p = resolve();
        log.debug("Using AI provider: {}", p.getProviderName());
        return p.complete(request);
    }

    public String getActiveProviderName() {
        return resolve().getProviderName();
    }

    public List<String> getAvailableProviders() {
        return providers.values().stream()
                .filter(AiProvider::isAvailable)
                .map(AiProvider::getProviderName)
                .collect(Collectors.toList());
    }

    public boolean switchProvider(String name) {
        AiProvider p = providers.get(name);
        if (p != null && p.isAvailable()) {
            appProperties.getAi().setProvider(name);
            log.info("Switched AI provider to: {}", name);
            return true;
        }
        log.warn("Cannot switch to provider '{}': not found or unavailable", name);
        return false;
    }

    private AiProvider resolve() {
        String configured = appProperties.getAi().getProvider();
        if (StringUtils.hasText(configured)) {
            AiProvider p = providers.get(configured.toLowerCase());
            if (p != null && p.isAvailable()) return p;
            if (p != null) log.warn("Provider '{}' configured but unavailable — falling back", configured);
            else           log.warn("Unknown provider '{}' — falling back", configured);
        }
        for (String name : List.of("openai", "gemini", "mock")) {
            AiProvider p = providers.get(name);
            if (p != null && p.isAvailable()) return p;
        }
        throw new IllegalStateException("No AI provider available");
    }
}

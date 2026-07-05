package com.competitorintel.platform.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiResponse {
    private String  content;
    private String  provider;
    private String  model;
    private int     inputTokens;
    private int     outputTokens;
    private boolean success;
    private String  errorMessage;

    public static AiResponse failure(String provider, String message) {
        return AiResponse.builder()
                .provider(provider)
                .success(false)
                .errorMessage(message)
                .build();
    }
}

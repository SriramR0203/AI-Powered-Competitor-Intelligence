package com.competitorintel.platform.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRequest {
    private String systemPrompt;
    private String userPrompt;
    @Builder.Default
    private double temperature = 0.3;
    @Builder.Default
    private int    maxTokens   = 1500;
}

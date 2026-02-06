package com.solveria.ai.infrastructure.llm;

import com.solveria.ai.application.dto.ChatResultDto;
import com.solveria.ai.application.port.out.LlmChatPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FallbackLlmChatPortConfig {

    @Bean
    @ConditionalOnMissingBean(LlmChatPort.class)
    public LlmChatPort fallbackLlmChatPort() {
        return prompt -> new ChatResultDto(
                "[DEV-STUB] LLM no configurado. Respuesta simulada.",
                0,
                0
        );
    }
}

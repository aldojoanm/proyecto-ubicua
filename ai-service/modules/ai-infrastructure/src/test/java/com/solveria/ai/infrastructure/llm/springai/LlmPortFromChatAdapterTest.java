package com.solveria.ai.infrastructure.llm.springai;

import com.solveria.ai.application.dto.ChatResultDto;
import com.solveria.ai.application.port.out.LlmChatPort;
import com.solveria.ai.domain.model.Completion;
import com.solveria.ai.domain.model.Prompt;
import org.springframework.beans.factory.ObjectProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LlmPortFromChatAdapterTest {

    @Mock
    private LlmChatPort llmChat;

    @Test
    void complete_delegatesToLlmChatAndMapsToCompletion() {
        when(llmChat.chat("hello")).thenReturn(new ChatResultDto("hi", 1, 2));
        ObjectProvider<LlmChatPort> provider = new ObjectProvider<>() {
            @Override
            public LlmChatPort getObject(Object... args) {
                return llmChat;
            }

            @Override
            public LlmChatPort getObject() {
                return llmChat;
            }

            @Override
            public LlmChatPort getIfAvailable() {
                return llmChat;
            }

            @Override
            public LlmChatPort getIfUnique() {
                return llmChat;
            }

            @Override
            public java.util.stream.Stream<LlmChatPort> stream() {
                return java.util.stream.Stream.of(llmChat);
            }

            @Override
            public java.util.stream.Stream<LlmChatPort> orderedStream() {
                return java.util.stream.Stream.of(llmChat);
            }
        };
        var adapter = new LlmPortFromChatAdapter(provider);
        var prompt = Prompt.of("hello");

        Completion c = adapter.complete(prompt);

        assertEquals("hi", c.content());
        assertEquals("openai", c.model());
        assertEquals(3, c.tokensUsed());
        verify(llmChat).chat("hello");
    }
}

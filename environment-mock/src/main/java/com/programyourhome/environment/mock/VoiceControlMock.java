package com.programyourhome.environment.mock;

import java.util.Arrays;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.programyourhome.voice.VoiceControl;
import com.programyourhome.voice.model.PyhLanguage;

@Configuration
public class VoiceControlMock extends PyhMock {

    @Bean
    public VoiceControl createVoiceControlMock() {
        final VoiceControl voiceControlMock = this.createMock(VoiceControl.class);
        final PyhLanguage language = Mockito.mock(PyhLanguage.class);
        Mockito.when(language.getId()).thenReturn(1);
        Mockito.when(language.getLocale()).thenReturn("mo-ck");
        Mockito.when(language.getName()).thenReturn("Mock language");
        Mockito.when(language.getConfirmations()).thenReturn(Arrays.asList("Confirmation mock 1", "Confirmation mock 2"));
        Mockito.when(language.getNegations()).thenReturn(Arrays.asList("Negation mock 1", "Negation mock 2"));
        Mockito.when(voiceControlMock.getSupportedLanguages()).thenReturn(Arrays.asList(language));
        return voiceControlMock;
    }

}

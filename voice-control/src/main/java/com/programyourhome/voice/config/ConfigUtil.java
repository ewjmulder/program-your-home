package com.programyourhome.voice.config;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConfigUtil {

    public static Collection<String> getConfirmations(final VoiceControlConfig config, final String locale) {
        return getLanguage(config, locale).get().getConfirmations().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    public static Collection<String> getNegations(final VoiceControlConfig config, final String locale) {
        return getLanguage(config, locale).get().getNegations().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    public static Optional<Language> getLanguage(final VoiceControlConfig config, final String locale) {
        return config.getLanguages().stream()
                .filter(language -> language.getLocale().toLowerCase().equals(locale.toLowerCase()))
                .findFirst();
    }

}

package com.programyourhome.voice.config;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.programyourhome.common.config.ConfigLoader;
import com.programyourhome.common.config.ConfigurationException;
import com.programyourhome.voice.VoiceControl;

@Component
public class VoiceControlConfigLoader extends ConfigLoader<VoiceControlConfig> {

    private static final String CONFIG_BASE_PATH = "/com/programyourhome/config/voice-control/";
    private static final String XSD_BASE_PATH = CONFIG_BASE_PATH + "xsd/";
    private static final String XML_BASE_PATH = CONFIG_BASE_PATH + "xml/";
    private static final String XSD_FILENAME = "voice-control.xsd";
    private static final String XML_FILENAME = "voice-control.xml";

    @Inject
    private VoiceControl voiceControl;

    @Override
    protected Class<VoiceControlConfig> getConfigType() {
        return VoiceControlConfig.class;
    }

    @Override
    protected String getPathToXsd() {
        return XSD_BASE_PATH + XSD_FILENAME;
    }

    @Override
    protected String getPathToXml() {
        return XML_BASE_PATH + XML_FILENAME;
    }

    @Override
    protected void validateConfig(final VoiceControlConfig voiceControlConfig) throws ConfigurationException {
        // TODO: List of validations:
        /**
         * <pre>
         * - Internal XML:
         *   - Unique id's
         *   - Unique language names (PYH common utils thingy, since has overlap with server stuff (is ok, see as apache commons alternative))
         *   - Existing Java locales
         *   - confirmations/negations can contain only 1 word (per entry)!
         * </pre>
         */
    }
}

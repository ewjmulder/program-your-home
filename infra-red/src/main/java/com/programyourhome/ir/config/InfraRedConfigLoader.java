package com.programyourhome.ir.config;

import org.springframework.stereotype.Component;

import com.programyourhome.common.config.ConfigLoader;
import com.programyourhome.common.config.ConfigurationException;
import com.programyourhome.ir.InfraRed;

@Component
public class InfraRedConfigLoader extends ConfigLoader<InfraRedConfig> {

    private static final String CONFIG_BASE_PATH = "/com/programyourhome/config/infra-red/";
    private static final String XSD_BASE_PATH = CONFIG_BASE_PATH + "xsd/";
    private static final String XML_BASE_PATH = CONFIG_BASE_PATH + "xml/";
    private static final String XSD_FILENAME = "infra-red.xsd";
    private static final String XML_FILENAME = "infra-red.xml";

    // @Autowired
    private InfraRed infraRed;

    @Override
    protected Class<InfraRedConfig> getConfigType() {
        return InfraRedConfig.class;
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
    protected void validateConfig(final InfraRedConfig infraRedConfig) throws ConfigurationException {
        // TODO: IR specific validation - probably match with WinLIRC stuff
        // List of validations:
        /**
         * <pre>
         * - Internal XML:
         *   - Unique device names (PYH common utils thingy, since has overlap with server stuff (is ok, see as apache commons alternative))
         *   - Unique key names per device
         *   - Prototypes vs actual key type mapping
         *   - Unique input names for all keys of type input
         * - Consistency check with winlirc data
         *   - Remote name mapping
         *   - (winlirc) Key name mapping
         * </pre>
         */
    }
}

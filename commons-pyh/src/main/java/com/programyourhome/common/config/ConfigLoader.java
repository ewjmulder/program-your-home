package com.programyourhome.common.config;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

//TODO: have abstract methods for getting base paths and xsd and xml file names instead of defining those in every subclass.
public abstract class ConfigLoader<ConfigType> {

    @Inject
    private ConfigXsdSchema configXsdSchema;

    /**
     * Load the server configuration. Also performs all possible consistency checks on the contents of the
     * configuration.
     *
     * @throws ConfigurationException when the configuration contains an XSD error or does not comply to some custom validation rule
     * @return the config JAXB object
     */
    public ConfigType loadConfig() throws ConfigurationException {
        try {
            final JAXBContext context = JAXBContext.newInstance(this.getConfigType());
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(this.configXsdSchema.loadSchema(this.getPathToXsd()));
            @SuppressWarnings("unchecked")
            final ConfigType config = (ConfigType) unmarshaller.unmarshal(ConfigLoader.class.getResourceAsStream(this.getPathToXml()));
            this.validateConfig(config);
            return config;
        } catch (final JAXBException e) {
            throw new ConfigurationException("JAXBException occured during loading of config file: '" + this.getPathToXml() + "'.", e);
        }
    }

    protected abstract Class<ConfigType> getConfigType();

    protected abstract String getPathToXsd();

    protected abstract String getPathToXml();

    // TODO: have a 'formal' list of consistency checks somewhere? (ugh, requirements, really...? :)
    // --> how about separate small classes / lambda objects that can validate a certain piece of information? like predicates
    // TODO: unit tests for these rules!!
    /**
     * Performs consistency checks on the configuration. These include all checks that cannot be done in the XSD,
     * because they have conditional rules or depend on external resources.
     *
     * @param config the config to validate
     * @throws ConfigurationException when the configuration contains an XSD error or does not comply to some custom validation rule
     */
    protected abstract void validateConfig(final ConfigType config) throws ConfigurationException;

}

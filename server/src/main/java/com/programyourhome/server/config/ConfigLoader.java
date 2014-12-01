package com.programyourhome.server.config;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.programyourhome.config.Activity;
import com.programyourhome.config.ConfigXsdSchema;
import com.programyourhome.config.ServerConfig;
import com.programyourhome.hue.PhilipsHue;
import com.programyourhome.ir.InfraRed;

@Component
public class ConfigLoader {

    private static final String CONFIG_BASE_PATH = "/com/programyourhome/config/";
    private static final String XML_BASE_PATH = CONFIG_BASE_PATH + "xml/";
    private static final String ICON_BASE_PATH = CONFIG_BASE_PATH + "icons/";
    private static final String XML_FILENAME = "program-your-home-config-server.xml";

    @Autowired
    private PhilipsHue philipsHue;

    // @Autowired
    private InfraRed infraRed;

    /**
     * Load the server configuration. Also performs all possible consistency checks on the contents of the
     * configuration.
     *
     * @return
     */
    public ServerConfig loadConfig() {
        try {
            final JAXBContext context = JAXBContext.newInstance(ServerConfig.class);
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(ConfigXsdSchema.getSchema());
            final ServerConfig serverConfig = (ServerConfig) unmarshaller.unmarshal(ConfigLoader.class.getResourceAsStream(XML_BASE_PATH + XML_FILENAME));
            this.validateConfig(serverConfig);
            return serverConfig;
        } catch (final JAXBException e) {
            throw new ConfigurationException("JAXBException occured during loading config.", e);
        }
    }

    /**
     * Performs consistency checks on the server configuration. These include all checks that cannot be done in the XSD,
     * because they
     * have conditional rules or depend on external resources.
     *
     * @param serverConfig
     */
    // TODO: have a 'formal' list of consistency checks somewhere? (ugh, requirements, really...? :)
    // TODO: unit tests for these rules!!
    private void validateConfig(final ServerConfig serverConfig) {
        this.validateMain(serverConfig);
    }

    private void validateMain(final ServerConfig serverConfig) {
        final List<Activity> activities = serverConfig.getActivities();

        final long activityCount = activities.size();
        final long uniqueNamesCount = activities.stream().map(Activity::getName).distinct().count();
        if (activityCount != uniqueNamesCount) {
            throw new ConfigurationException("Activities should have a unique name.");
        }

        for (final Activity activity : activities) {
            if (!StringUtils.isEmpty(activity.getIcon()) && ConfigLoader.class.getResourceAsStream(ICON_BASE_PATH + activity.getIcon()) == null) {
                throw new ConfigurationException("Icon file: '" + activity.getIcon() + "' for activity: '" + activity.getName() + "' does not exist.");
            }
        }
    }
}

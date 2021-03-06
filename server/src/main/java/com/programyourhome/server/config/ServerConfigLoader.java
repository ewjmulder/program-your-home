package com.programyourhome.server.config;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.programyourhome.common.config.ConfigLoader;
import com.programyourhome.common.config.ConfigurationException;
import com.programyourhome.hue.PhilipsHue;
import com.programyourhome.ir.InfraRed;
import com.programyourhome.server.config.model.Activity;
import com.programyourhome.server.config.model.ServerConfig;

@Component
public class ServerConfigLoader extends ConfigLoader<ServerConfig> {

    private static final String CONFIG_BASE_PATH = "/com/programyourhome/config/server/";
    private static final String XSD_BASE_PATH = CONFIG_BASE_PATH + "xsd/";
    private static final String XML_BASE_PATH = CONFIG_BASE_PATH + "xml/";
    private static final String ICON_BASE_PATH = CONFIG_BASE_PATH + "icons/";
    private static final String XSD_FILENAME = "program-your-home-config-server.xsd";
    private static final String XML_FILENAME = "program-your-home-config-server.xml";

    @Inject
    private PhilipsHue philipsHue;

    @Inject
    private InfraRed infraRed;

    @Override
    protected Class<ServerConfig> getConfigType() {
        return ServerConfig.class;
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
    protected void validateConfig(final ServerConfig serverConfig) throws ConfigurationException {
        this.validateMain(serverConfig);
        // TODO: module specific validation.
        // Philips Hue:
        // - check light id's against existing lights
        // - check used light settings against light type
        // IR:
        // - check device id's against existing devices
        // - check used device input's against existing device inputs
        // - check if keys from activity devices are actually devices that should be turned on
    }

    private void validateMain(final ServerConfig serverConfig) {
        final List<Activity> activities = serverConfig.getActivitiesConfig().getActivities();

        final long activityCount = activities.size();

        // TODO: more generic doubles check
        final long uniqueIdsCount = activities.stream().map(Activity::getId).distinct().count();
        if (activityCount != uniqueIdsCount) {
            throw new ConfigurationException("Activities should have a unique id.");
        }

        final long uniqueNamesCount = activities.stream().map(Activity::getName).distinct().count();
        if (activityCount != uniqueNamesCount) {
            throw new ConfigurationException("Activities should have a unique name.");
        }

        for (final Activity activity : activities) {
            if (!StringUtils.isEmpty(activity.getIcon()) && ServerConfigLoader.class.getResourceAsStream(ICON_BASE_PATH + activity.getIcon()) == null) {
                throw new ConfigurationException("Icon file: '" + activity.getIcon() + "' for activity: '" + activity.getName() + "' does not exist.");
            }
        }
    }
}

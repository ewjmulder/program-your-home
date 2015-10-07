package com.programyourhome.common.config;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public abstract class ConfigHolder<ConfigType> {

    @Inject
    private ConfigLoader<ConfigType> configLoader;
    private ConfigType config;

    @PostConstruct
    private void init() {
        this.config = this.configLoader.loadConfig();
    }

    // TODO: caching done, also dynamic loading of underlying xml when changed? (what to do on error? -> not replace)
    public ConfigType getConfig() {
        return this.config;
    }

}

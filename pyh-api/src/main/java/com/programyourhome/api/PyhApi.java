package com.programyourhome.api;

import java.util.Collection;

/**
 * Super interface for all Program Your Home Module API classes.
 */
public interface PyhApi {

    /**
     * The name of this API module.
     *
     * @return the name
     */
    public String getName();

    /**
     * A collection of Class objects, representing the interface definitions that make up the model of this API module.
     * This collection should include all interfaces that should be implemented in an implementing module project.
     *
     * @return
     */
    public Collection<Class<?>> getModelInterfaces();

}

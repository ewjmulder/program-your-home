package com.programyourhome.server.household.model;

import com.programyourhome.common.model.PyhImpl;
import com.programyourhome.server.config.model.State;

public class PyhStateImpl extends PyhImpl implements PyhState {

    private final int id;
    private final String name;
    private final String description;

    public PyhStateImpl(final State state) {
        this.id = state.getId();
        this.name = state.getName();
        this.description = state.getDescription();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

}

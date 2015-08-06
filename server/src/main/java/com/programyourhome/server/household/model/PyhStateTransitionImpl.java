package com.programyourhome.server.household.model;

import com.programyourhome.common.model.PyhImpl;
import com.programyourhome.server.config.model.StateTransition;

public class PyhStateTransitionImpl extends PyhImpl implements PyhStateTransition {

    private final int from;
    private final int to;
    private final String name;
    private final String description;

    public PyhStateTransitionImpl(final StateTransition stateTransition) {
        this.from = stateTransition.getFrom();
        this.to = stateTransition.getTo();
        this.name = stateTransition.getName();
        this.description = stateTransition.getDescription();
    }

    @Override
    public int getFrom() {
        return this.from;
    }

    @Override
    public int getTo() {
        return this.to;
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

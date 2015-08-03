package com.programyourhome.server.household.model;

import com.programyourhome.common.model.PyhImpl;
import com.programyourhome.server.config.model.Person;
import com.programyourhome.server.config.model.State;

public class PyhPersonImpl extends PyhImpl implements PyhPerson {

    private final int id;
    private final PersonType type;
    private final String firstName;
    private final String lastName;
    private final PyhState state;

    public PyhPersonImpl(final Person person, final PersonType personType, final State state) {
        this.id = person.getId();
        this.type = personType;
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.state = new PyhStateImpl(state);
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public PersonType getType() {
        return this.type;
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public PyhState getState() {
        return this.state;
    }

}

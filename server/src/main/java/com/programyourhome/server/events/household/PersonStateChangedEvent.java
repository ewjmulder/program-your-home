package com.programyourhome.server.events.household;

import com.programyourhome.server.events.ValueChangedEvent;
import com.programyourhome.server.household.model.PyhPerson;

public class PersonStateChangedEvent extends ValueChangedEvent<PyhPerson> {

    private static final long serialVersionUID = 1L;

    public PersonStateChangedEvent(final PyhPerson oldValue, final PyhPerson newValue) {
        super(oldValue, newValue);
    }

    @Override
    public String getTopic() {
        return "/topic/household/state/people/" + this.getNewValue().getId();
    }

}

package com.programyourhome.server.states;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.programyourhome.server.config.model.Person;
import com.programyourhome.server.config.model.State;

@Component
public class PeopleStateManager {

    private final Map<Person, State> personStates;

    public PeopleStateManager() {
        this.personStates = new HashMap<>();
    }

}

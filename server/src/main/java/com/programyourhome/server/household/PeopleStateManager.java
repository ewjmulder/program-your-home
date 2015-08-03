package com.programyourhome.server.household;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.programyourhome.server.config.ServerConfigHolder;
import com.programyourhome.server.config.model.Person;
import com.programyourhome.server.config.model.State;

@Component
public class PeopleStateManager {

    @Autowired
    private ServerConfigHolder configHolder;

    @Autowired
    private PeopleManager peopleManager;

    private final Map<Person, State> personStates;

    public PeopleStateManager() {
        this.personStates = new HashMap<>();
    }

    @PostConstruct
    // TODO: could be replaced with / enriched with functionality to get the latest known state from some kind of log.
    // This is quite essential when rebooting every night, so the people states stay intact
    // https://geteventstore.com/downloads/ !!
    public void init() {
        this.peopleManager.getResidents().forEach(resident -> this.personStates.put(resident, this.getDefaultResidentState()));
        this.peopleManager.getGuests().forEach(guest -> this.personStates.put(guest, this.getDefaultGuestState()));
    }

    public State getState(final Person person) {
        return this.personStates.get(person);
    }

    public Optional<State> getResidentState(final int id) {
        return this.getPersonState(this.configHolder.getConfig().getStateConfig().getResident().getStates(), id);
    }

    public Optional<State> getGuestState(final int id) {
        return this.getPersonState(this.configHolder.getConfig().getStateConfig().getGuest().getStates(), id);
    }

    private Optional<State> getPersonState(final List<State> people, final int id) {
        return people.stream()
                .filter(state -> state.getId() == id)
                .findFirst();
    }

    public State getDefaultResidentState() {
        return this.getResidentState(this.configHolder.getConfig().getStateConfig().getResident().getDefaultState()).get();
    }

    public State getDefaultGuestState() {
        return this.getResidentState(this.configHolder.getConfig().getStateConfig().getGuest().getDefaultState()).get();
    }

}

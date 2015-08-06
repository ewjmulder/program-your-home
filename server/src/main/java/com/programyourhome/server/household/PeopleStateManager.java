package com.programyourhome.server.household;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.programyourhome.server.config.ServerConfigHolder;
import com.programyourhome.server.config.model.Person;
import com.programyourhome.server.config.model.PersonType;
import com.programyourhome.server.config.model.State;
import com.programyourhome.server.config.model.StateTransition;

@Component
public class PeopleStateManager {

    @Autowired
    private ServerConfigHolder configHolder;

    @Autowired
    private PeopleManager peopleManager;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

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

    public Collection<State> getResidentStates() {
        return this.configHolder.getConfig().getStateConfig().getResident().getStates();
    }

    public Collection<State> getGuestStates() {
        return this.configHolder.getConfig().getStateConfig().getResident().getStates();
    }

    public Optional<State> getPersonState(final int id) {
        return CollectionUtils.union(this.getResidentStates(), this.getGuestStates()).stream()
                .filter(state -> state.getId() == id)
                .findFirst();
    }

    public State getDefaultResidentState() {
        return this.getPersonState(this.configHolder.getConfig().getStateConfig().getResident().getDefaultState()).get();
    }

    public State getDefaultGuestState() {
        return this.getPersonState(this.configHolder.getConfig().getStateConfig().getGuest().getDefaultState()).get();
    }

    public Collection<StateTransition> getResidentStateTransitions() {
        return this.configHolder.getConfig().getStateConfig().getResident().getStateTransitions();
    }

    public Collection<StateTransition> getGuestStateTransitions() {
        return this.configHolder.getConfig().getStateConfig().getGuest().getStateTransitions();
    }

    public void setPersonState(final Person person, final int stateId) {
        if (!this.personStates.containsKey(person)) {
            throw new IllegalArgumentException("Person: '" + person + "' not found in person state map.");
        }
        final State oldState = this.getState(person);
        final State newState = this.getPersonState(stateId).get();
        if (person.getType() == PersonType.RESIDENT && !this.isValidResidentStateTransition(oldState, newState)) {
            throw new IllegalArgumentException("Invalid resident state transition from: '" + oldState + "' to '" + newState + "'.");
        } else if (person.getType() == PersonType.GUEST && !this.isValidGuestStateTransition(oldState, newState)) {
            throw new IllegalArgumentException("Invalid guest state transition from: '" + oldState + "' to '" + newState + "'.");
        } else {
            this.personStates.put(person, newState);
        }
    }

    public boolean isValidResidentStateTransition(final State oldState, final State newState) {
        return this.isValidStateTransition(this.getGuestStateTransitions(), oldState, newState);
    }

    public boolean isValidGuestStateTransition(final State oldState, final State newState) {
        return this.isValidStateTransition(this.getGuestStateTransitions(), oldState, newState);
    }

    private boolean isValidStateTransition(final Collection<StateTransition> stateTransitions, final State oldState, final State newState) {
        return stateTransitions.stream()
                .anyMatch(stateTransition -> stateTransition.getFrom() == oldState.getId() && stateTransition.getTo() == newState.getId());
    }

}

package com.programyourhome.server.controllers.household;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.server.config.model.Person;
import com.programyourhome.server.config.model.State;
import com.programyourhome.server.config.model.StateTransition;
import com.programyourhome.server.controllers.AbstractProgramYourHomeController;
import com.programyourhome.server.household.PeopleManager;
import com.programyourhome.server.household.PeopleStateManager;
import com.programyourhome.server.household.model.PyhPerson;
import com.programyourhome.server.household.model.PyhPersonImpl;
import com.programyourhome.server.household.model.PyhState;
import com.programyourhome.server.household.model.PyhStateImpl;
import com.programyourhome.server.household.model.PyhStateTransition;
import com.programyourhome.server.household.model.PyhStateTransitionImpl;
import com.programyourhome.server.response.ServiceResult;

@RestController
@RequestMapping("household/people")
public class ProgramYourHomeControllerHouseholdPeople extends AbstractProgramYourHomeController {

    @Inject
    private PeopleManager peopleManager;

    @Inject
    private PeopleStateManager peopleStateManager;

    @RequestMapping("residents")
    public ServiceResult<Collection<PyhPerson>> getResidents() {
        return this.produce("Residents", () -> this.getPyhResidents());
    }

    @RequestMapping("residents/states")
    public ServiceResult<Collection<PyhState>> getResidentStates() {
        return this.produce("States", () -> this.getPyhResidentStates());
    }

    @RequestMapping("residents/states/transitions")
    public ServiceResult<Collection<PyhStateTransition>> getResidentStateTransitions() {
        return this.produce("StateTransitions", () -> this.getPyhResidentStateTransitions());
    }

    @RequestMapping("guests")
    public ServiceResult<Collection<PyhPerson>> getGuests() {
        return this.produce("Guests", () -> this.getPyhGuests());
    }

    @RequestMapping("guests/states")
    public ServiceResult<Collection<PyhState>> getGuestStates() {
        return this.produce("States", () -> this.getPyhGuestStates());
    }

    @RequestMapping("guests/states/transitions")
    public ServiceResult<Collection<PyhStateTransition>> getGuestStateTransitions() {
        return this.produce("StateTransitions", () -> this.getPyhGuestStateTransitions());
    }

    @RequestMapping("")
    public ServiceResult<Collection<PyhPerson>> getPeople() {
        return this.produce("People", () -> CollectionUtils.union(this.getPyhResidents(), this.getPyhGuests()));
    }

    @RequestMapping("{id}")
    public ServiceResult<PyhPerson> getPerson(@PathVariable("id") final int id) {
        return this.find("Person", id, this.peopleManager::getPerson)
                .produce(this::createPyhPerson);
    }

    @RequestMapping("{id}/state/transition/{toState}")
    public ServiceResult<Void> personStateTransition(@PathVariable("id") final int id, @PathVariable("toState") final int toStateId) {
        return this.find("Person", id, this.peopleManager::getPerson)
                .process(person -> this.peopleStateManager.setPersonState(person, toStateId));
    }

    private Collection<PyhPerson> getPyhResidents() {
        return this.getPyhPersons(this.peopleManager.getResidents());
    }

    private Collection<PyhPerson> getPyhGuests() {
        return this.getPyhPersons(this.peopleManager.getGuests());
    }

    private Collection<PyhPerson> getPyhPersons(final Collection<Person> persons) {
        return persons.stream()
                .map(this::createPyhPerson)
                .collect(Collectors.toList());
    }

    private Collection<PyhState> getPyhResidentStates() {
        return this.getPyhPersonStates(this.peopleStateManager.getResidentStates());
    }

    private Collection<PyhState> getPyhGuestStates() {
        return this.getPyhPersonStates(this.peopleStateManager.getGuestStates());
    }

    private Collection<PyhState> getPyhPersonStates(final Collection<State> states) {
        return states.stream()
                .map(PyhStateImpl::new)
                .collect(Collectors.toList());
    }

    private Collection<PyhStateTransition> getPyhResidentStateTransitions() {
        return this.getPyhPersonStateTransitions(this.peopleStateManager.getResidentStateTransitions());
    }

    private Collection<PyhStateTransition> getPyhGuestStateTransitions() {
        return this.getPyhPersonStateTransitions(this.peopleStateManager.getGuestStateTransitions());
    }

    private Collection<PyhStateTransition> getPyhPersonStateTransitions(final Collection<StateTransition> stateTransitions) {
        return stateTransitions.stream()
                .map(PyhStateTransitionImpl::new)
                .collect(Collectors.toList());
    }

    private PyhPerson createPyhPerson(final Person person) {
        return new PyhPersonImpl(person, this.peopleStateManager.getStateOfPerson(person));
    }

}

package com.programyourhome.server.controllers.household;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.server.config.model.Person;
import com.programyourhome.server.controllers.AbstractProgramYourHomeController;
import com.programyourhome.server.household.PeopleManager;
import com.programyourhome.server.household.PeopleStateManager;
import com.programyourhome.server.household.model.PyhPerson;
import com.programyourhome.server.household.model.PyhPersonImpl;
import com.programyourhome.server.response.ServiceResult;

@RestController
@RequestMapping("household/people")
public class ProgramYourHomeControllerHouseholdPeople extends AbstractProgramYourHomeController {

    @Autowired
    private PeopleManager peopleManager;

    @Autowired
    private PeopleStateManager peopleStateManager;

    @RequestMapping("residents")
    public ServiceResult<Collection<PyhPerson>> getResidents() {
        return this.produce("Residents", () -> this.getPyhResidents());
    }

    @RequestMapping("guests")
    public ServiceResult<Collection<PyhPerson>> getGuests() {
        return this.produce("Guests", () -> this.getPyhGuests());
    }

    @RequestMapping("people")
    public ServiceResult<Collection<PyhPerson>> getPeople() {
        return this.produce("People", () -> CollectionUtils.union(this.getPyhResidents(), this.getPyhGuests()));
    }

    @RequestMapping("people/{id}")
    public ServiceResult<PyhPerson> getPerson(@PathVariable("id") final int id) {
        return this.find("Person", id, this.peopleManager::getPerson)
                .produce(this::createPyhPerson);
    }

    @RequestMapping("people/{id}/state/transition/{toState}")
    public ServiceResult<Void> personStateTransition(@PathVariable("id") final int id, @PathVariable("toState") final int toStateId) {
        return this.find("Person", id, this.peopleManager::getPerson)
                .process(person -> this.peopleStateManager.setPersonState(person, toStateId));
    }

    private Collection<PyhPerson> getPyhResidents() {
        return this.getPyhPersons(this.peopleManager.getResidents(), this::createPyhPerson);
    }

    private Collection<PyhPerson> getPyhGuests() {
        return this.getPyhPersons(this.peopleManager.getGuests(), this::createPyhPerson);
    }

    private Collection<PyhPerson> getPyhPersons(final List<Person> persons, final Function<Person, PyhPerson> converter) {
        return persons.stream()
                .map(converter)
                .collect(Collectors.toList());
    }

    private PyhPerson createPyhPerson(final Person person) {
        return new PyhPersonImpl(person, this.peopleStateManager.getPersonState(person.getId()).get());
    }

}

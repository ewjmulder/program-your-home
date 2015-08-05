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
import com.programyourhome.server.household.model.PersonType;
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
    public ServiceResult<Collection<PyhPerson>> getAllPeople() {
        return this.produce("People", () -> CollectionUtils.union(this.getPyhResidents(), this.getPyhGuests()));
    }

    @RequestMapping("residents/{id}")
    public ServiceResult<PyhPerson> getResident(@PathVariable final int id) {
        return this.find("Resident", id, this.peopleManager::getResident)
                .produce(this::createPyhResident);
    }

    @RequestMapping("guests/{id}")
    public ServiceResult<PyhPerson> getGuest(@PathVariable final int id) {
        return this.find("Guest", id, this.peopleManager::getGuest)
                .produce(this::createPyhGuest);
    }

    private Collection<PyhPerson> getPyhResidents() {
        return this.getPyhPersons(this.peopleManager.getResidents(), this::createPyhResident);
    }

    private Collection<PyhPerson> getPyhGuests() {
        return this.getPyhPersons(this.peopleManager.getGuests(), this::createPyhGuest);
    }

    private Collection<PyhPerson> getPyhPersons(final List<Person> persons, final Function<Person, PyhPerson> converter) {
        return persons.stream()
                .map(converter)
                .collect(Collectors.toList());
    }

    private PyhPerson createPyhResident(final Person person) {
        return new PyhPersonImpl(person, PersonType.RESIDENT, this.peopleStateManager.getResidentState(person.getId()).get());
    }

    private PyhPerson createPyhGuest(final Person person) {
        return new PyhPersonImpl(person, PersonType.GUEST, this.peopleStateManager.getGuestState(person.getId()).get());
    }

}

package com.programyourhome.server.household;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.programyourhome.server.config.ServerConfigHolder;
import com.programyourhome.server.config.model.Person;
import com.programyourhome.server.config.model.PersonType;

@Component
public class PeopleManager {

    @Inject
    private ServerConfigHolder configHolder;

    public List<Person> getResidents() {
        return this.getPeopleOfType(PersonType.RESIDENT);
    }

    public List<Person> getGuests() {
        return this.getPeopleOfType(PersonType.GUEST);
    }

    public List<Person> getPeopleOfType(final PersonType personType) {
        return this.configHolder.getConfig().getPeople().stream()
                .filter(person -> person.getType() == personType)
                .collect(Collectors.toList());
    }

    public List<Person> getPeople() {
        return this.configHolder.getConfig().getPeople();
    }

    public Optional<Person> getPerson(final int id) {
        return this.findPersonWithId(this.getPeople(), id);
    }

    private Optional<Person> findPersonWithId(final List<Person> people, final int id) {
        return people.stream()
                .filter(person -> person.getId() == id)
                .findFirst();
    }

}

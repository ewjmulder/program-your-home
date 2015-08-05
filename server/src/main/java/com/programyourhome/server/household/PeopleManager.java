package com.programyourhome.server.household;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.programyourhome.server.config.ServerConfigHolder;
import com.programyourhome.server.config.model.Person;

@Component
public class PeopleManager {

    @Autowired
    private ServerConfigHolder configHolder;

    public List<Person> getResidents() {
        return this.configHolder.getConfig().getPeople().getResidents();
    }

    public List<Person> getGuests() {
        return this.configHolder.getConfig().getPeople().getGuests();
    }

    public List<Person> getPeople() {
        return ListUtils.union(this.getResidents(), this.getGuests());
    }

    public Optional<Person> getResident(final int id) {
        return this.findPersonWithId(this.getResidents(), id);
    }

    public Optional<Person> getGuest(final int id) {
        return this.findPersonWithId(this.getGuests(), id);
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

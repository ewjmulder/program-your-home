package com.programyourhome.server.controllers.household;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.server.controllers.AbstractProgramYourHomeController;
import com.programyourhome.server.controllers.response.ServiceResult;
import com.programyourhome.server.household.PeopleManager;
import com.programyourhome.server.household.PeopleStateManager;
import com.programyourhome.server.household.model.PersonType;
import com.programyourhome.server.household.model.PyhPerson;
import com.programyourhome.server.household.model.PyhPersonImpl;

@RestController
@RequestMapping("household/people")
public class ProgramYourHomeControllerHouseholdPeople extends AbstractProgramYourHomeController {

    @Autowired
    private PeopleManager peopleManager;

    @Autowired
    private PeopleStateManager peopleStateManager;

    @RequestMapping("residents")
    public ServiceResult<Collection<PyhPerson>> getResidents() {
        return this.peopleManager.getResidents().stream()
                .map(resident -> new PyhPersonImpl(resident, PersonType.RESIDENT, this.peopleStateManager.getResidentState(person.getId()).get()));
    }
}

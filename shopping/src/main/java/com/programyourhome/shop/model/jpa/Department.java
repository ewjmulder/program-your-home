package com.programyourhome.shop.model.jpa;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.programyourhome.shop.common.NamedEntity;
import com.programyourhome.shop.model.PyhDepartment;

@Entity
public class Department extends NamedEntity implements PyhDepartment {

    @ManyToOne
    @JoinColumn(nullable = false)
    private final Company company;

    public Department() {
        this(null, null, null);
    }

    public Department(final Company company, final String name, final String desciption) {
        super(name, desciption);
        this.company = company;
    }

    public Company getCompany() {
        return this.company;
    }

}

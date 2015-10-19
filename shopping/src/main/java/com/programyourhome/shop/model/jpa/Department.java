package com.programyourhome.shop.model.jpa;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.programyourhome.shop.common.NamedEntity;

@Entity
public class Department extends NamedEntity {

    @ManyToOne
    @JoinColumn(nullable = false)
    private final Company company;

    public Department() {
        this(null, null);
    }

    public Department(final String name, final String desciption) {
        super(name, desciption);
        this.company = null;
    }

    public Company getCompany() {
        return this.company;
    }

}

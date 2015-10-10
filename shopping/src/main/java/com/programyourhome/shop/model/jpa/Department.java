package com.programyourhome.shop.model.jpa;

import javax.persistence.Entity;

import com.programyourhome.shop.common.NamedEntity;

@Entity
public class Department extends NamedEntity {

    public Department() {
    }

    public Department(final String name, final String desciption) {
        super(name, desciption);
    }

}

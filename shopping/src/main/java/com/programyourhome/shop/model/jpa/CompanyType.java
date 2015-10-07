package com.programyourhome.shop.model.jpa;

import javax.persistence.Entity;

import com.programyourhome.common.jpa.NamedEntity;

@Entity
public class CompanyType extends NamedEntity {

    public CompanyType() {
    }

    public CompanyType(final String name, final String desciption) {
        super(name, desciption);
    }

}

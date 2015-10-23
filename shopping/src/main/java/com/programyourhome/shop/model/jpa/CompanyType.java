package com.programyourhome.shop.model.jpa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.programyourhome.shop.common.NamedEntity;
import com.programyourhome.shop.model.PyhCompanyType;

@Entity
public class CompanyType extends NamedEntity implements PyhCompanyType {

    @OneToMany(mappedBy = "type")
    private final Set<Company> companies;

    public CompanyType() {
        this(null, null);
    }

    public CompanyType(final String name, final String desciption) {
        super(name, desciption);
        this.companies = new HashSet<>();
    }

}

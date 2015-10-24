package com.programyourhome.shop.model.jpa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.programyourhome.shop.common.NamedEntity;
import com.programyourhome.shop.model.PyhDepartment;

@Entity
public class Department extends NamedEntity implements PyhDepartment {

    @ManyToOne
    @JoinColumn(nullable = false)
    private final Company company;

    @OneToMany(mappedBy = "department")
    private final Set<ShopDepartment> shopDepartments;

    /** Only for JPA, we don't want an instance of this type to be constructed without a link to company. */
    @SuppressWarnings("unused")
    private Department() {
        this(null, null, null);
    }

    public Department(final Company company) {
        this(company, null, null);
    }

    public Department(final Company company, final String name, final String desciption) {
        super(name, desciption);
        this.company = company;
        this.shopDepartments = new HashSet<>();
    }

    public Company getCompany() {
        return this.company;
    }

    public Set<ShopDepartment> getShopDepartments() {
        return this.shopDepartments;
    }

}

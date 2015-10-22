package com.programyourhome.shop.model.jpa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.programyourhome.shop.common.NamedEntity;

@Entity
public class Company extends NamedEntity {

    @ManyToOne
    @JoinColumn(nullable = false)
    private CompanyType type;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "company_id")
    private final Set<Shop> shops;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "company_id")
    private final Set<Department> departments;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "company_id")
    private final Set<CompanyProduct> companyProducts;

    public Company() {
        this(null, null, null);
    }

    public Company(final String name, final String desciption, final CompanyType type) {
        super(name, desciption);
        this.shops = new HashSet<>();
        this.departments = new HashSet<>();
        this.companyProducts = new HashSet<>();
        this.type = type;
    }

    public CompanyType getType() {
        return this.type;
    }

    public void setType(final CompanyType type) {
        this.type = type;
    }

    public Set<Department> getDepartments() {
        return this.departments;
    }

    public void addDepartment(final Department department) {
        this.departments.add(department);
    }

    public void removeDepartment(final Department department) {
        this.departments.remove(department);
    }

    public Set<CompanyProduct> getCompanyProducts() {
        return this.companyProducts;
    }

    public void addCompanyProduct(final CompanyProduct companyProduct) {
        this.companyProducts.add(companyProduct);
    }

    public void removeCompanyProduct(final CompanyProduct companyProduct) {
        this.companyProducts.remove(companyProduct);
    }

}

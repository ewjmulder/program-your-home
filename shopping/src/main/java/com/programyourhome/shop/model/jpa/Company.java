package com.programyourhome.shop.model.jpa;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.programyourhome.common.jpa.NamedEntity;

@Entity
public class Company extends NamedEntity {

    @ManyToOne
    @JoinColumn(nullable = false)
    private CompanyType type;
    @OneToMany
    @JoinColumn(name = "company_id")
    private Set<Department> departments;

    public Company() {
    }

    public Company(final String name, final String desciption, final CompanyType type) {
        super(name, desciption);
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

}

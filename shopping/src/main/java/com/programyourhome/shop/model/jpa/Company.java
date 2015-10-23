package com.programyourhome.shop.model.jpa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.programyourhome.shop.common.NamedEntity;
import com.programyourhome.shop.model.PyhCompany;

@Entity
public class Company extends NamedEntity implements PyhCompany {

    @ManyToOne
    @JoinColumn(nullable = false)
    private CompanyType type;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "company")
    private final Set<Shop> shops;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "company")
    private final Set<Department> departments;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "company")
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

    @Override
    public CompanyType getType() {
        return this.type;
    }

    public void setType(final CompanyType type) {
        this.type = type;
    }

    @Override
    public Set<Shop> getShops() {
        return this.shops;
    }

    public Shop getShop(final int shopId) {
        return this.shops.stream()
                .filter(shop -> shop.getId() == shopId)
                .findFirst().orElse(null);
    }

    public void addShop(final Shop shop) {
        this.shops.add(shop);
    }

    public void removeShop(final Shop shop) {
        this.shops.remove(shop);
    }

    @Override
    public Set<Department> getDepartments() {
        return this.departments;
    }

    public Department getDepartment(final int departmentId) {
        return this.departments.stream()
                .filter(department -> department.getId() == departmentId)
                .findFirst().orElse(null);
    }

    public void addDepartment(final Department department) {
        this.departments.add(department);
    }

    public void removeDepartment(final Department department) {
        this.departments.remove(department);
    }

    @Override
    public Set<CompanyProduct> getCompanyProducts() {
        return this.companyProducts;
    }

    public Optional<CompanyProduct> findCompanyProduct(final int productId) {
        return this.companyProducts.stream()
                .filter(companyProduct -> companyProduct.getProduct().getId() == productId)
                .findFirst();
    }

    public void addCompanyProduct(final CompanyProduct companyProduct) {
        this.companyProducts.add(companyProduct);
    }

    public void removeCompanyProduct(final int productId) {
        new ArrayList<>(this.companyProducts).stream()
                .filter(companyProduct -> companyProduct.getProduct().getId() == productId)
                .forEach(this.companyProducts::remove);
    }

}

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

    // This is and explicit deviation from the naming scheme, because otherwise when the departments of the company are deleted,
    // that will trigger a update on the ShopDepartment's with that department. That results in a integrity violation, even though
    // those ShopDepartment's are to be deleted in the same operation anyway.
    // Giving the departments collection a name which is further in the alphabet then 'shops' will reverse the deletion order and prevent
    // the problem from happening. Very ugly workaround of course, so a better solution is very welcome!
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "company")
    private final Set<Department> theDepartments;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "company")
    private final Set<CompanyProduct> companyProducts;

    public Company() {
        this(null, null, null);
    }

    public Company(final String name, final String desciption, final CompanyType type) {
        super(name, desciption);
        this.shops = new HashSet<>();
        this.theDepartments = new HashSet<>();
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

    public Set<Shop> getShops() {
        return this.shops;
    }

    public Optional<Shop> findShop(final int shopId) {
        return this.shops.stream()
                .filter(shop -> shop.getId() == shopId)
                .findFirst();
    }

    public Optional<Shop> findShop(final String shopName) {
        return this.shops.stream()
                .filter(shop -> shop.getName().equals(shopName))
                .findFirst();
    }

    public void addShop(final Shop shop) {
        this.shops.add(shop);
    }

    public void removeShop(final Shop shop) {
        this.shops.remove(shop);
    }

    public Set<Department> getDepartments() {
        return this.theDepartments;
    }

    public Optional<Department> findDepartment(final int departmentId) {
        return this.theDepartments.stream()
                .filter(department -> department.getId() == departmentId)
                .findFirst();
    }

    public Optional<Department> findDepartment(final String departmentName) {
        return this.theDepartments.stream()
                .filter(department -> department.getName().equals(departmentName))
                .findFirst();
    }

    public void addDepartment(final Department department) {
        this.theDepartments.add(department);
    }

    public void removeDepartment(final Department department) {
        this.theDepartments.remove(department);
    }

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

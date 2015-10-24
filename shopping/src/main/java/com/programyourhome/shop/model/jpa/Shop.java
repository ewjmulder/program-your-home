package com.programyourhome.shop.model.jpa;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.programyourhome.shop.common.NamedEntity;
import com.programyourhome.shop.model.PyhShop;

@Entity
public class Shop extends NamedEntity implements PyhShop {

    @ManyToOne
    @JoinColumn(nullable = false)
    private final Company company;

    // TODO: Normalize address
    @Column(nullable = true, length = 512)
    private String address;

    // TODO: Add GPS coordinates

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "shop")
    private final Set<ShopDepartment> shopDepartments;

    /** Only for JPA, we don't want an instance of this type to be constructed without a link to company. */
    @SuppressWarnings("unused")
    private Shop() {
        this(null, null, null, null);
    }

    public Shop(final Company company) {
        this(company, null, null, null);
    }

    public Shop(final Company company, final String name, final String desciption, final String address) {
        super(name, desciption);
        this.shopDepartments = new HashSet<>();
        this.company = company;
        this.address = address;
    }

    public Company getCompany() {
        return this.company;
    }

    @Override
    public String getAddress() {
        return this.address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public Set<ShopDepartment> getShopDepartments() {
        return this.shopDepartments;
    }

    public Optional<ShopDepartment> findShopDepartment(final int departmentId) {
        return this.shopDepartments.stream()
                .filter(shopDepartment -> shopDepartment.getDepartment().getId() == departmentId)
                .findFirst();
    }

    public void addShopDepartment(final ShopDepartment shopDepartment) {
        this.shopDepartments.add(shopDepartment);
    }

    public void removeShopDepartment(final int departmentId) {
        new HashSet<>(this.shopDepartments).stream()
        .filter(shopDepartment -> shopDepartment.getDepartment().getId() == departmentId)
        .forEach(this.shopDepartments::remove);
    }

}

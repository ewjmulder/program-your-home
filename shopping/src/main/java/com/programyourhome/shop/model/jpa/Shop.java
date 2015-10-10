package com.programyourhome.shop.model.jpa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.programyourhome.shop.common.NamedEntity;

@Entity
public class Shop extends NamedEntity {

    @ManyToOne
    @JoinColumn(nullable = false)
    private Company company;

    // TODO: Normalize address
    @Column(nullable = true, length = 512)
    private String address;

    // TODO: Add GPS coordinates

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "shop_id")
    private final Set<ShopDepartment> shopDepartments;

    public Shop() {
        this(null, null, null, null);
    }

    public Shop(final String name, final String desciption, final Company company, final String address) {
        super(name, desciption);
        this.shopDepartments = new HashSet<>();
        this.company = company;
        this.address = address;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(final Company company) {
        this.company = company;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public Set<ShopDepartment> getShopDepartments() {
        return this.shopDepartments;
    }

    public void addShopDepartment(final ShopDepartment shopDepartment) {
        this.shopDepartments.add(shopDepartment);
    }

    public void removeShopDepartment(final ShopDepartment shopDepartment) {
        this.shopDepartments.remove(shopDepartment);
    }

}

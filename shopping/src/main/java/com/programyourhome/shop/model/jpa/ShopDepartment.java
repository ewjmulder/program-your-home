package com.programyourhome.shop.model.jpa;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.programyourhome.shop.common.Entity;

@javax.persistence.Entity
public class ShopDepartment extends Entity {

    @ManyToOne
    @JoinColumn(nullable = false)
    private Shop shop;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Department department;

    @Column(nullable = true, unique = true)
    private Integer sequence;

    /** Only for JPA, we don't want an instance of this type to be constructor without links to shop and department. */
    @SuppressWarnings("unused")
    private ShopDepartment() {
    }

    public ShopDepartment(final Shop shop, final Department department) {
        this(shop, department, null);
    }

    public ShopDepartment(final Shop shop, final Department department, final Integer sequence) {
        this.shop = shop;
        this.department = department;
        this.sequence = sequence;
    }

    public Shop getShop() {
        return this.shop;
    }

    public Department getDepartment() {
        return this.department;
    }

    public Integer getSequence() {
        return this.sequence;
    }

    public void setSequence(final Integer sequence) {
        this.sequence = sequence;
    }

}

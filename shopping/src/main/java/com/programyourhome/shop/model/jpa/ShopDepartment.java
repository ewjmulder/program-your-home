package com.programyourhome.shop.model.jpa;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.programyourhome.shop.common.Entity;
import com.programyourhome.shop.model.PyhShopDepartment;
import com.programyourhome.shop.model.PyhShopDepartmentToShop;

@javax.persistence.Entity
public class ShopDepartment extends Entity implements PyhShopDepartment, PyhShopDepartmentToShop {

    @ManyToOne
    @JoinColumn(nullable = false)
    private Shop shop;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Department department;

    @Column(nullable = true)
    private Integer sequence;

    /** Only for JPA, we don't want an instance of this type to be constructed without links to shop and department. */
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

    @Override
    public Shop getShop() {
        return this.shop;
    }

    @Override
    public Department getDepartment() {
        return this.department;
    }

    @Override
    public Integer getSequence() {
        return this.sequence;
    }

    public void setSequence(final Integer sequence) {
        this.sequence = sequence;
    }

}

package com.programyourhome.shop.model.jpa;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.javamoney.moneta.Money;

import com.programyourhome.shop.common.Entity;
import com.programyourhome.shop.common.MoneyConverter;

@javax.persistence.Entity
public class CompanyProduct extends Entity {

    @ManyToOne
    @JoinColumn(nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Product product;

    @Column(nullable = true)
    @Convert(converter = MoneyConverter.class)
    private Money money;

    /** Only for JPA, we don't want an instance of this type to be constructor without links to company and product. */
    @SuppressWarnings("unused")
    private CompanyProduct() {
    }

    public CompanyProduct(final Company company, final Product product) {
        this(company, product, null);
    }

    public CompanyProduct(final Company company, final Product product, final Money money) {
        this.company = company;
        this.product = product;
        this.money = money;
    }

    public Company getCompany() {
        return this.company;
    }

    public Product getProduct() {
        return this.product;
    }

    public Money getMoney() {
        return this.money;
    }

    public void setMoney(final Money money) {
        this.money = money;
    }

}

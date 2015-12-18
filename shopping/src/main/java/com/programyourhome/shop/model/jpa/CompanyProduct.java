package com.programyourhome.shop.model.jpa;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.internal.MoneyAmountBuilder;

import com.programyourhome.shop.common.Entity;
import com.programyourhome.shop.common.MoneyConverter;
import com.programyourhome.shop.model.Currency;
import com.programyourhome.shop.model.PyhCompanyProduct;
import com.programyourhome.shop.model.PyhCompanyProductToCompany;
import com.programyourhome.shop.model.PyhMonetaryAmount;
import com.programyourhome.shop.model.pojo.PyhMonetaryAmountImpl;

@javax.persistence.Entity
public class CompanyProduct extends Entity implements PyhCompanyProduct, PyhCompanyProductToCompany {

    @ManyToOne
    @JoinColumn(nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(nullable = true)
    private Department department;

    @Column(nullable = true)
    @Convert(converter = MoneyConverter.class)
    private Money price;

    /** Only for JPA, we don't want an instance of this type to be constructed without links to company and product. */
    @SuppressWarnings("unused")
    private CompanyProduct() {
    }

    public CompanyProduct(final Company company, final Product product) {
        this(company, product, null, null);
    }

    public CompanyProduct(final Company company, final Product product, final Department department, final Money price) {
        this.company = company;
        this.product = product;
        this.department = department;
        this.price = price;
    }

    @Override
    public Company getCompany() {
        return this.company;
    }

    @Override
    public Product getProduct() {
        return this.product;
    }

    @Override
    public Department getDepartment() {
        return this.department;
    }

    public void setDepartment(final Department department) {
        this.department = department;
    }

    @Override
    public PyhMonetaryAmount getPrice() {
        return new PyhMonetaryAmountImpl(Currency.valueOf(this.price.getCurrency().getCurrencyCode()), this.price.getNumberStripped());
    }

    public void setPrice(final PyhMonetaryAmount monetaryAmount) {
        this.price = new MoneyAmountBuilder().setCurrency(monetaryAmount.getCurrency().name()).setNumber(monetaryAmount.getAmount()).create();
    }

}

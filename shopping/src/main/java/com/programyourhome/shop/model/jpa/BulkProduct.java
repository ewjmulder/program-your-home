package com.programyourhome.shop.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.programyourhome.shop.common.NamedEntity;
import com.programyourhome.shop.model.PyhBulkProduct;

@Entity
public class BulkProduct extends NamedEntity implements PyhBulkProduct {

    @ManyToOne
    @JoinColumn(nullable = false)
    private final Product product;

    @Column(unique = true, nullable = false)
    private String barcode;

    @Column(nullable = false)
    private Integer amount;

    /** Only for JPA, we don't want an instance of this type to be constructed without a link to product. */
    @SuppressWarnings("unused")
    private BulkProduct() {
        this(null, null, null, null, null);
    }

    public BulkProduct(final Product product) {
        this(product, null, null, null, null);
    }

    public BulkProduct(final Product product, final String name, final String description, final String barcode, final Integer amount) {
        super(name, description);
        this.product = product;
        this.barcode = barcode;
        this.amount = amount;
    }

    public Product getProduct() {
        return this.product;
    }

    @Override
    public String getBarcode() {
        return this.barcode;
    }

    public void setBarcode(final String barcode) {
        this.barcode = barcode;
    }

    @Override
    public Integer getAmount() {
        return this.amount;
    }

    public void setAmount(final int amount) {
        this.amount = amount;
    }

}

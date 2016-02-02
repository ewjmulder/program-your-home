package com.programyourhome.shop.model.jpa;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.programyourhome.shop.common.NamedEntity;
import com.programyourhome.shop.model.PyhProduct;
import com.programyourhome.shop.model.PyhProductSize;
import com.programyourhome.shop.model.size.SizeUnit;

@Entity
public class Product extends NamedEntity implements PyhProduct {

    @Column(unique = true, nullable = false)
    private String barcode;

    @Column(nullable = false)
    private BigDecimal sizeAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SizeUnit sizeUnit;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "product")
    private ProductImage image;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "product")
    private final Set<BulkProduct> bulkProducts;

    // Modeled as a collection to allow for one product to be a part of several aggregations.
    @ManyToMany(mappedBy = "product")
    private final Set<ProductAggregationPart> aggregationParts;

    @ManyToMany(mappedBy = "product")
    private final Set<CompanyProduct> companyProducts;

    public Product() {
        this(null, null, null, null, null);
    }

    public Product(final String name, final String description, final String barcode, final BigDecimal sizeAmount, final SizeUnit sizeUnit) {
        super(name, description);
        this.bulkProducts = new HashSet<>();
        this.aggregationParts = new HashSet<>();
        this.companyProducts = new HashSet<>();
        this.barcode = barcode;
        this.sizeAmount = sizeAmount;
        this.sizeUnit = sizeUnit;
        this.image = null;
    }

    @Override
    public String getBarcode() {
        return this.barcode;
    }

    public void setBarcode(final String barcode) {
        this.barcode = barcode;
    }

    @Override
    public PyhProductSize getSize() {
        return new ProductSize(this.sizeAmount, this.sizeUnit);
    }

    public BigDecimal getSizeAmount() {
        return this.sizeAmount;
    }

    public void setSizeAmount(final BigDecimal sizeAmount) {
        this.sizeAmount = sizeAmount;
    }

    public SizeUnit getSizeUnit() {
        return this.sizeUnit;
    }

    public void setSizeUnit(final SizeUnit sizeUnit) {
        this.sizeUnit = sizeUnit;
    }

    public boolean hasImage() {
        return this.image != null;
    }

    public ProductImage getImage() {
        return this.image;
    }

    public void setImage(final ProductImage image) {
        this.image = image;
    }

    // @Override
    @Override
    public Set<BulkProduct> getBulkProducts() {
        return this.bulkProducts;
    }

    public Optional<BulkProduct> findBulkProduct(final int bulkProductId) {
        return this.bulkProducts.stream()
                .filter(bulkProduct -> bulkProduct.getId() == bulkProductId)
                .findFirst();
    }

    public Optional<BulkProduct> findBulkProduct(final String bulkProductName) {
        return this.bulkProducts.stream()
                .filter(bulkProduct -> bulkProduct.getName().equals(bulkProductName))
                .findFirst();
    }

    public void addBulkProduct(final BulkProduct bulkProduct) {
        this.bulkProducts.add(bulkProduct);
    }

    public void removeBulkProduct(final BulkProduct bulkProduct) {
        this.bulkProducts.remove(bulkProduct);
    }

    public Set<ProductAggregationPart> getAggregationParts() {
        return this.aggregationParts;
    }

    public Set<CompanyProduct> getCompanyProducts() {
        return this.companyProducts;
    }

}

package com.programyourhome.shop.model.jpa;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.programyourhome.shop.common.Entity;
import com.programyourhome.shop.model.ImageMimeType;
import com.programyourhome.shop.model.PyhProductImage;

@javax.persistence.Entity
public class ProductImage extends Entity implements PyhProductImage {

    // Set the max size of an image to 1 MB (base64 that is, so actual image file is somewhat smaller).
    private static final int MAX_IMAGE_SIZE = 1024 * 1024;

    @OneToOne
    @JoinColumn(nullable = false)
    private Product product;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ImageMimeType imageMimeType;

    @Column(nullable = false, length = MAX_IMAGE_SIZE)
    private String imageBase64;

    /** Only for JPA, we don't want an instance of this type to be constructed without a link to product. */
    @SuppressWarnings("unused")
    private ProductImage() {
    }

    public ProductImage(final Product product) {
        this(product, null, null);
    }

    public ProductImage(final Product product, final ImageMimeType imageMimeType, final String imageBase64) {
        this.product = product;
        this.imageMimeType = imageMimeType;
        this.imageBase64 = imageBase64;
    }

    public Product getProduct() {
        return this.product;
    }

    @Override
    public ImageMimeType getImageMimeType() {
        return this.imageMimeType;
    }

    public void setImageMimeType(final ImageMimeType imageMimeType) {
        this.imageMimeType = imageMimeType;
    }

    @Override
    public String getImageBase64() {
        return this.imageBase64;
    }

    public void setImageBase64(final String imageBase64) {
        this.imageBase64 = imageBase64;
    }

}

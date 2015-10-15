package com.programyourhome.shop.model.jpa;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.programyourhome.shop.model.ImageMimeType;
import com.programyourhome.shop.model.PyhProductImage;

@Embeddable
public class ProductImage implements PyhProductImage {

    // TODO: smarter annotation or validator that checks that if either field is not null, both aren't.

    // Set the max size of an image to 1 MB (base64 that is, so actual image file is somewhat smaller).
    private static final int MAX_IMAGE_SIZE = 1024 * 1024;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private ImageMimeType imageMimeType;

    @Column(nullable = true, length = MAX_IMAGE_SIZE)
    private String imageBase64;

    public ProductImage() {
    }

    public ProductImage(final ImageMimeType imageMimeType, final String imageBase64) {
        this.imageMimeType = imageMimeType;
        this.imageBase64 = imageBase64;
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

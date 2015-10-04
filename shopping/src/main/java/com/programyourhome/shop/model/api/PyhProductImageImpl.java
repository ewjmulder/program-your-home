package com.programyourhome.shop.model.api;

import com.programyourhome.common.model.PyhImpl;
import com.programyourhome.shop.model.ImageMimeType;
import com.programyourhome.shop.model.PyhProductImage;

public class PyhProductImageImpl extends PyhImpl implements PyhProductImage {

    private final ImageMimeType imageMimeType;
    private final String imageBase64;

    public PyhProductImageImpl(final ImageMimeType imageMimeType, final String imageBase64) {
        this.imageMimeType = imageMimeType;
        this.imageBase64 = imageBase64;
    }

    @Override
    public ImageMimeType getImageMimeType() {
        return this.imageMimeType;
    }

    @Override
    public String getImageBase64() {
        return this.imageBase64;
    }

}

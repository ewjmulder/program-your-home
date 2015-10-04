package com.programyourhome.shop.model;

public enum ImageMimeType {

    JPG("image/jpeg"),
    PNG("image/png"),
    GIF("image/gif");

    private String mimeType;

    private ImageMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return this.mimeType;
    }

}

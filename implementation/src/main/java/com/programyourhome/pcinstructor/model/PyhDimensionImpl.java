package com.programyourhome.pcinstructor.model;

public class PyhDimensionImpl implements PyhDimension {

    private final int width;
    private final int height;

    public PyhDimensionImpl(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

}

package com.programyourhome.pcinstructor.model;

public class PyhPointImpl implements PyhPoint {

    private final int x;
    private final int y;

    public PyhPointImpl(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

}

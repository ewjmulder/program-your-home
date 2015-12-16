package com.programyourhome.pcinstructor.model;

public class PyhDistanceImpl implements PyhDistance {

    private final int dx;
    private final int dy;

    public PyhDistanceImpl(final int dx, final int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public int getDx() {
        return this.dx;
    }

    @Override
    public int getDy() {
        return this.dy;
    }

}

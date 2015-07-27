package com.programyourhome.pc.model;

import com.programyourhome.common.model.PyhImpl;

public class PyhPointImpl extends PyhImpl implements PyhPoint {

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

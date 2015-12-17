package com.programyourhome.shop.model;

public interface PyhProduct extends PyhProductProperties {

    public int getId();

    @Override
    public PyhProductSize getSize();

}

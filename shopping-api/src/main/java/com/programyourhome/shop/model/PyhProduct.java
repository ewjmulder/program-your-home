package com.programyourhome.shop.model;

import java.util.Collection;

public interface PyhProduct extends PyhProductProperties {

    public int getId();

    public Collection<? extends PyhBulkProduct> getBulkProducts();

}

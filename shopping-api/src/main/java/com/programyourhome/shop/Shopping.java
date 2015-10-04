package com.programyourhome.shop;

import java.util.Collection;

import com.programyourhome.shop.model.PyhProduct;

public interface Shopping {

    public Collection<PyhProduct> getProducts();

    public PyhProduct getProduct(int productId);

}

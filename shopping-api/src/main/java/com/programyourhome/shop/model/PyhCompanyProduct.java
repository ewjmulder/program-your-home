package com.programyourhome.shop.model;

public interface PyhCompanyProduct extends PyhCompanyProductPropertiesBase {

    public PyhDepartment getDepartment();

    /**
     * Get the product that is part of this joined relation. The default is that
     * this object will be 'navigated to' from the company side, not from
     * the product side.
     *
     * @return the product
     */
    public PyhProduct getProduct();

}

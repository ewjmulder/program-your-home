package com.programyourhome.shop.model;

public interface PyhCompanyProduct extends PyhCompanyProductPropertiesBase {

    public PyhDepartment getDepartment();

    /**
     * Get the product that is part of this joined relation. It's assumed that in the API
     * this object will always be 'navigated to' from the company side, never from
     * the product side. That's why there is no getCompany method.
     *
     * @return the product
     */
    public PyhProduct getProduct();

}

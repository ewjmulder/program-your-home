package com.programyourhome.shop;

import com.fasterxml.jackson.annotation.JsonView;
import com.programyourhome.shop.model.PyhCompany;
import com.programyourhome.shop.model.PyhDepartment;
import com.programyourhome.shop.model.PyhMonetaryAmount;
import com.programyourhome.shop.model.PyhProduct;

public interface CompanyProductJsonView {

    @JsonView(WriterToProduct.class)
    public PyhProduct getProduct();

    @JsonView(WriterToCompany.class)
    public PyhCompany getCompany();

    public PyhMonetaryAmount getPrice();

    public PyhDepartment getDepartment();

}

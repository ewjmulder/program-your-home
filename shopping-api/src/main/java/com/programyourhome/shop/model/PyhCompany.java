package com.programyourhome.shop.model;

import java.util.Collection;

public interface PyhCompany extends PyhCompanyPropertiesBase {

    public int getId();

    public PyhCompanyType getType();

    public Collection<? extends PyhShop> getShops();

    public Collection<? extends PyhDepartment> getDepartments();

    public Collection<? extends PyhCompanyProduct> getCompanyProducts();

}

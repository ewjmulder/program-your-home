package com.programyourhome.shop.model;

public interface PyhShopDepartment extends PyhShopDepartmentProperties {

    /**
     * Get the department that is part of joined relation. It's assumed that in the API
     * this object will always be 'navigated to' from the shop side, never from
     * the department side. That's why there is no getShop method.
     *
     * @return the department
     */
    public PyhDepartment getDepartment();

}

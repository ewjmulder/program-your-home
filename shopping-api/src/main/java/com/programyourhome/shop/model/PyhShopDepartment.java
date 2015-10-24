package com.programyourhome.shop.model;

public interface PyhShopDepartment extends PyhShopDepartmentProperties {

    /**
     * Get the department that is part of joined relation. The default is that
     * this object will be 'navigated to' from the shop side, not from
     * the department side.
     *
     * @return the department
     */
    public PyhDepartment getDepartment();

}

package com.programyourhome.server.controllers.shop;

import java.util.Collection;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.server.controllers.AbstractProgramYourHomeController;
import com.programyourhome.server.response.ServiceResult;
import com.programyourhome.shop.Shopping;
import com.programyourhome.shop.model.PyhCompany;
import com.programyourhome.shop.model.PyhCompanyProduct;
import com.programyourhome.shop.model.PyhCompanyProductProperties;
import com.programyourhome.shop.model.PyhCompanyProperties;
import com.programyourhome.shop.model.PyhDepartment;
import com.programyourhome.shop.model.PyhDepartmentProperties;
import com.programyourhome.shop.model.PyhShop;
import com.programyourhome.shop.model.PyhShopDepartment;
import com.programyourhome.shop.model.PyhShopDepartmentProperties;
import com.programyourhome.shop.model.PyhShopDepartmentToShop;
import com.programyourhome.shop.model.PyhShopProperties;

@RestController
@RequestMapping("shop/companies")
public class ProgramYourHomeControllerShopCompanies extends AbstractProgramYourHomeController {

    @Inject
    private Shopping shopping;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ServiceResult<Collection<? extends PyhCompany>> getCompanys() {
        return this.produce("Companies", () -> this.shopping.getCompanies());
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ServiceResult<PyhCompany> getCompany(@PathVariable("id") final int companyId) {
        return this.produce("Company", () -> this.shopping.getCompany(companyId));
    }

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MIME_JSON)
    public ServiceResult<PyhCompany> createCompany(@RequestBody final PyhCompanyProperties companyProperties) {
        return this.produce("Company", () -> this.shopping.createCompany(companyProperties));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT, consumes = MIME_JSON)
    public ServiceResult<PyhCompany> updateCompany(@PathVariable("id") final int companyId,
            @RequestBody final PyhCompanyProperties companyProperties) {
        return this.produce("Company", () -> this.shopping.updateCompany(companyId, companyProperties));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ServiceResult<Void> deleteCompany(@PathVariable("id") final int companyId) {
        return this.run(() -> this.shopping.deleteCompany(companyId));
    }

    @RequestMapping(value = "{id}/shops", method = RequestMethod.GET)
    public ServiceResult<Collection<? extends PyhShop>> getShops(@PathVariable("id") final int companyId) {
        return this.produce("Shops", () -> this.shopping.getShops(companyId));
    }

    @RequestMapping(value = "{id}/shops/{shopId}", method = RequestMethod.GET)
    public ServiceResult<PyhShop> getShop(@PathVariable("id") final int companyId, @PathVariable("shopId") final int shopId) {
        return this.produce("Shop", () -> this.shopping.getShop(companyId, shopId));
    }

    @RequestMapping(value = "{id}/shops", method = RequestMethod.POST, consumes = MIME_JSON)
    public ServiceResult<PyhShop> createShop(@PathVariable("id") final int companyId, @RequestBody final PyhShopProperties shopProperties) {
        return this.produce("Shop", () -> this.shopping.addShop(companyId, shopProperties));
    }

    @RequestMapping(value = "{id}/shops/{shopId}", method = RequestMethod.PUT, consumes = MIME_JSON)
    public ServiceResult<PyhShop> updateShop(@PathVariable("id") final int companyId, @PathVariable("shopId") final int shopId,
            @RequestBody final PyhShopProperties shopProperties) {
        return this.produce("Shop", () -> this.shopping.updateShop(companyId, shopId, shopProperties));
    }

    @RequestMapping(value = "{id}/shops/{shopId}", method = RequestMethod.DELETE)
    public ServiceResult<Void> deleteShop(@PathVariable("id") final int companyId, @PathVariable("shopId") final int shopId) {
        return this.run(() -> this.shopping.deleteShop(companyId, shopId));
    }

    @RequestMapping(value = "{id}/departments", method = RequestMethod.GET)
    public ServiceResult<Collection<? extends PyhDepartment>> getDepartments(@PathVariable("id") final int companyId) {
        return this.produce("Departments", () -> this.shopping.getDepartments(companyId));
    }

    @RequestMapping(value = "{id}/departments/{departmentId}", method = RequestMethod.GET)
    public ServiceResult<PyhDepartment> getDepartment(@PathVariable("id") final int companyId, @PathVariable("departmentId") final int departmentId) {
        return this.produce("Department", () -> this.shopping.getDepartment(companyId, departmentId));
    }

    @RequestMapping(value = "{id}/departments", method = RequestMethod.POST, consumes = MIME_JSON)
    public ServiceResult<PyhDepartment> createDepartment(@PathVariable("id") final int companyId,
            @RequestBody final PyhDepartmentProperties departmentProperties) {
        return this.produce("Department", () -> this.shopping.addDepartment(companyId, departmentProperties));
    }

    @RequestMapping(value = "{id}/departments/{departmentId}", method = RequestMethod.PUT, consumes = MIME_JSON)
    public ServiceResult<PyhDepartment> updateDepartment(@PathVariable("id") final int companyId, @PathVariable("departmentId") final int departmentId,
            @RequestBody final PyhDepartmentProperties departmentProperties) {
        return this.produce("Department", () -> this.shopping.updateDepartment(companyId, departmentId, departmentProperties));
    }

    @RequestMapping(value = "{id}/departments/{departmentId}", method = RequestMethod.DELETE)
    public ServiceResult<Void> deleteDepartment(@PathVariable("id") final int companyId, @PathVariable("departmentId") final int departmentId) {
        return this.run(() -> this.shopping.deleteDepartment(companyId, departmentId));
    }

    @RequestMapping(value = "{id}/departments/{departmentId}/shopDepartments", method = RequestMethod.GET)
    public ServiceResult<Collection<? extends PyhShopDepartmentToShop>> getShopDepartmentsToShop(@PathVariable("id") final int companyId,
            @PathVariable("departmentId") final int departmentId) {
        return this.produce("ShopDepartmentsToShop", () -> this.shopping.getShopDepartmentsToShop(companyId, departmentId));
    }

    @RequestMapping(value = "{id}/shops/{shopId}/shopDepartments", method = RequestMethod.GET)
    public ServiceResult<Collection<? extends PyhShopDepartment>> getShopDepartments(@PathVariable("id") final int companyId,
            @PathVariable("shopId") final int shopId) {
        return this.produce("ShopDepartments", () -> this.shopping.getShopDepartments(companyId, shopId));
    }

    @RequestMapping(value = "{id}/shops/{shopId}/shopDepartments/{departmentId}", method = RequestMethod.GET)
    public ServiceResult<PyhShopDepartment> getShopDepartment(@PathVariable("id") final int companyId,
            @PathVariable("shopId") final int shopId, @PathVariable("departmentId") final int departmentId) {
        return this.produce("ShopDepartment", () -> this.shopping.getShopDepartment(companyId, shopId, departmentId));
    }

    @RequestMapping(value = "{id}/shops/{shopId}/shopDepartments/{departmentId}", method = RequestMethod.PUT, consumes = MIME_JSON)
    public ServiceResult<PyhShopDepartment> setDepartmentInShopDepartment(@PathVariable("id") final int companyId,
            @PathVariable("shopId") final int shopId, @PathVariable("departmentId") final int departmentId,
            @RequestBody final PyhShopDepartmentProperties shopDepartmentProperties) {
        return this.produce("ShopDepartment", () -> this.shopping.setDepartmentInShopDepartment(companyId, shopId, departmentId, shopDepartmentProperties));
    }

    @RequestMapping(value = "{id}/shops/{shopId}/shopDepartments/{departmentId}", method = RequestMethod.DELETE)
    public ServiceResult<Void> removeDepartmentFromShopDepartment(@PathVariable("id") final int companyId,
            @PathVariable("shopId") final int shopId, @PathVariable("departmentId") final int departmentId) {
        return this.run(() -> this.shopping.removeDepartmentFromShopDepartment(companyId, shopId, departmentId));
    }

    @RequestMapping(value = "{id}/companyProducts", method = RequestMethod.GET)
    public ServiceResult<Collection<? extends PyhCompanyProduct>> getCompanyProducts(@PathVariable("id") final int companyId) {
        return this.produce("CompanyProducts", () -> this.shopping.getCompanyProducts(companyId));
    }

    @RequestMapping(value = "{id}/companyProducts/{productId}", method = RequestMethod.GET)
    public ServiceResult<PyhCompanyProduct> getCompanyProduct(@PathVariable("id") final int companyId,
            @PathVariable("productId") final int productId) {
        return this.produce("CompanyProduct", () -> this.shopping.getCompanyProduct(companyId, productId));
    }

    @RequestMapping(value = "{id}/companyProducts/{productId}", method = RequestMethod.PUT, consumes = MIME_JSON)
    public ServiceResult<PyhCompanyProduct> setProductInCompanyProduct(@PathVariable("id") final int companyId,
            @PathVariable("productId") final int productId,
            @RequestBody final PyhCompanyProductProperties companyProductProperties) {
        return this.produce("CompanyProduct", () -> this.shopping.setProductInCompanyProduct(companyId, productId, companyProductProperties));
    }

    @RequestMapping(value = "{id}/companyProducts/{productId}", method = RequestMethod.DELETE)
    public ServiceResult<Void> removeProductFromCompanyProduct(@PathVariable("id") final int companyId,
            @PathVariable("productId") final int productId) {
        return this.run(() -> this.shopping.removeProductFromCompanyProduct(companyId, productId));
    }

}

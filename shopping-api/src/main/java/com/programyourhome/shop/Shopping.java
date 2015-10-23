package com.programyourhome.shop;

import java.util.Collection;

import com.programyourhome.shop.model.PyhCompany;
import com.programyourhome.shop.model.PyhCompanyProduct;
import com.programyourhome.shop.model.PyhCompanyProductProperties;
import com.programyourhome.shop.model.PyhCompanyProperties;
import com.programyourhome.shop.model.PyhCompanyType;
import com.programyourhome.shop.model.PyhCompanyTypeProperties;
import com.programyourhome.shop.model.PyhDepartment;
import com.programyourhome.shop.model.PyhDepartmentProperties;
import com.programyourhome.shop.model.PyhProduct;
import com.programyourhome.shop.model.PyhProductAggregation;
import com.programyourhome.shop.model.PyhProductAggregationPart;
import com.programyourhome.shop.model.PyhProductAggregationPartProperties;
import com.programyourhome.shop.model.PyhProductAggregationProperties;
import com.programyourhome.shop.model.PyhProductAggregationState;
import com.programyourhome.shop.model.PyhProductImage;
import com.programyourhome.shop.model.PyhProductProperties;
import com.programyourhome.shop.model.PyhProductState;
import com.programyourhome.shop.model.PyhShop;
import com.programyourhome.shop.model.PyhShopDepartment;
import com.programyourhome.shop.model.PyhShopDepartmentProperties;
import com.programyourhome.shop.model.PyhShopProperties;

public interface Shopping {

    public Collection<? extends PyhProduct> getProducts();

    public PyhProduct getProduct(int productId);

    public PyhProduct createProduct(PyhProductProperties productProperties);

    public PyhProduct updateProduct(int productId, PyhProductProperties product);

    public void deleteProduct(int productId);

    public PyhProductImage getProductImage(int productId);

    public PyhProductImage setImageForProduct(int productId, PyhProductImage image);

    public void removeImageFromProduct(int productId);

    public Collection<? extends PyhProductAggregation> getProductAggregations();

    public PyhProductAggregation getProductAggregation(int productAggregationId);

    public PyhProductAggregation createProductAggregation(PyhProductAggregationProperties productAggregation);

    public PyhProductAggregation updateProductAggregation(int productAggregationId, PyhProductAggregationProperties productAggregation);

    public void deleteProductAggregation(int productAggregationId);

    public Collection<? extends PyhProductAggregationPart> getProductAggregationParts(int productAggregationId);

    public PyhProductAggregationPart getProductAggregationPart(int productAggregationId, int productId);

    public PyhProductAggregation setProductInProductAggregation(int productAggregationId, int productId, PyhProductAggregationPartProperties aggregationPart);

    public PyhProductAggregation removeProductFromProductAggregation(int productAggregationId, int productId);

    public Collection<PyhProductState> getProductStates();

    public PyhProductState getProductState(int productId);

    public Collection<PyhProductAggregationState> getProductAggregationStates();

    public PyhProductAggregationState getProductAggregationState(int productAggregationId);

    public PyhProductState addProductItem(int productId);

    public PyhProductState addProductItem(String barcode);

    public PyhProductState removeProductItem(int productId);

    public PyhProductState removeProductItem(String barcode);

    public Collection<? extends PyhCompanyType> getCompanyTypes();

    public PyhCompanyType getCompanyType(int companyTypeId);

    public PyhCompanyType createCompanyType(PyhCompanyTypeProperties companyTypeProperties);

    public PyhCompanyType updateCompanyType(int companyTypeId, PyhCompanyTypeProperties companyTypeProperties);

    public void deleteCompanyType(int companyTypeId);

    public Collection<? extends PyhCompany> getCompanies();

    public PyhCompany getCompany(int companyId);

    public PyhCompany createCompany(PyhCompanyProperties companyProperties);

    public PyhCompany updateCompany(int companyId, PyhCompanyProperties companyProperties);

    public void deleteCompany(int companyId);

    public Collection<? extends PyhShop> getShops(int companyId);

    public PyhShop getShop(final int companyId, final int shopId);

    public PyhCompany addShop(final int companyId, final PyhShopProperties shopProperties);

    public PyhCompany updateShop(final int companyId, final int shopId, final PyhShopProperties shopProperties);

    public PyhCompany deleteShop(final int companyId, final int shopId);

    public Collection<? extends PyhDepartment> getDepartments(int companyId);

    public PyhDepartment getDepartment(final int companyId, final int departmentId);

    public PyhCompany addDepartment(final int companyId, final PyhDepartmentProperties shopProperties);

    public PyhCompany updateDepartment(final int companyId, final int departmentId, final PyhDepartmentProperties shopProperties);

    public PyhCompany deleteDepartment(final int companyId, final int departmentId);

    public Collection<? extends PyhShopDepartment> getShopDepartments(final int companyId, int shopId);

    public PyhShopDepartment getShopDepartment(final int companyId, int shopId, int departmentId);

    public PyhShop setDepartmentInShopDepartment(final int companyId, int shopId, int departmentId, PyhShopDepartmentProperties shopDepartmentProperties);

    public PyhShop removeDepartmentFromShopDepartment(final int companyId, int shopId, int departmentId);

    public Collection<? extends PyhCompanyProduct> getCompanyProducts(final int companyId);

    public PyhCompanyProduct getCompanyProduct(final int companyId, int productId);

    public PyhCompany setProductInCompanyProduct(int companyId, int productId, PyhCompanyProductProperties aggregationPart);

    public PyhCompany removeProductFromCompanyProduct(int companyId, int productId);

}

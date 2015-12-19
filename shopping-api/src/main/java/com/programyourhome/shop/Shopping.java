package com.programyourhome.shop;

import java.util.Collection;

import com.programyourhome.api.PyhApi;
import com.programyourhome.shop.model.PyhBulkProduct;
import com.programyourhome.shop.model.PyhBulkProductProperties;
import com.programyourhome.shop.model.PyhCompany;
import com.programyourhome.shop.model.PyhCompanyProduct;
import com.programyourhome.shop.model.PyhCompanyProductProperties;
import com.programyourhome.shop.model.PyhCompanyProductToCompany;
import com.programyourhome.shop.model.PyhCompanyProperties;
import com.programyourhome.shop.model.PyhCompanyType;
import com.programyourhome.shop.model.PyhCompanyTypeProperties;
import com.programyourhome.shop.model.PyhDepartment;
import com.programyourhome.shop.model.PyhDepartmentProperties;
import com.programyourhome.shop.model.PyhProduct;
import com.programyourhome.shop.model.PyhProductAggregation;
import com.programyourhome.shop.model.PyhProductAggregationPart;
import com.programyourhome.shop.model.PyhProductAggregationPartProperties;
import com.programyourhome.shop.model.PyhProductAggregationPartToProductAggregation;
import com.programyourhome.shop.model.PyhProductAggregationProperties;
import com.programyourhome.shop.model.PyhProductAggregationState;
import com.programyourhome.shop.model.PyhProductImage;
import com.programyourhome.shop.model.PyhProductProperties;
import com.programyourhome.shop.model.PyhProductState;
import com.programyourhome.shop.model.PyhShop;
import com.programyourhome.shop.model.PyhShopDepartment;
import com.programyourhome.shop.model.PyhShopDepartmentProperties;
import com.programyourhome.shop.model.PyhShopDepartmentToShop;
import com.programyourhome.shop.model.PyhShopProperties;

public interface Shopping extends PyhApi {

    @Override
    public default String getName() {
        return "Shopping";
    }

    public Collection<? extends PyhProduct> getProducts();

    public PyhProduct getProduct(int productId);

    public PyhProduct createProduct(PyhProductProperties productProperties);

    public PyhProduct updateProduct(int productId, PyhProductProperties product);

    public void deleteProduct(int productId);

    public Collection<? extends PyhBulkProduct> getBulkProducts(int productId);

    public PyhBulkProduct getBulkProduct(final int productId, final int bulkProductId);

    public PyhBulkProduct addBulkProduct(final int productId, final PyhBulkProductProperties bulkProductProperties);

    public PyhBulkProduct updateBulkProduct(final int productId, final int bulkProductId, final PyhBulkProductProperties bulkProductProperties);

    public void deleteBulkProduct(final int productId, final int bulkProductId);

    public Collection<? extends PyhProductAggregationPartToProductAggregation> getProductAggregationPartsToProductAggregation(int productId);

    public Collection<? extends PyhCompanyProductToCompany> getCompanyProductsToCompany(int productId);

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

    public PyhProductAggregationPart setProductInProductAggregationPart(int productAggregationId, int productId,
            PyhProductAggregationPartProperties aggregationPart);

    public void removeProductFromProductAggregationPart(int productAggregationId, int productId);

    public Collection<PyhProductState> getProductStates();

    public PyhProductState getProductState(int productId);

    public Collection<PyhProductAggregationState> getProductAggregationStates();

    public PyhProductAggregationState getProductAggregationState(int productAggregationId);

    public PyhProductState increaseByProductId(int productId, final int amount);

    public PyhProductState increaseByBarcode(String barcode);

    public PyhProductState decreaseByProductId(int productId, final int amount);

    public PyhProductState decreaseByBarcode(String barcode);

    public Collection<? extends PyhCompanyType> getCompanyTypes();

    public PyhCompanyType getCompanyType(int companyTypeId);

    public PyhCompanyType createCompanyType(PyhCompanyTypeProperties companyTypeProperties);

    public PyhCompanyType updateCompanyType(int companyTypeId, PyhCompanyTypeProperties companyTypeProperties);

    public void deleteCompanyType(int companyTypeId);

    public Collection<? extends PyhCompany> getCompanies(int companyTypeId);

    public Collection<? extends PyhCompany> getCompanies();

    public PyhCompany getCompany(int companyId);

    public PyhCompany createCompany(PyhCompanyProperties companyProperties);

    public PyhCompany updateCompany(int companyId, PyhCompanyProperties companyProperties);

    public void deleteCompany(int companyId);

    public Collection<? extends PyhShop> getShops(int companyId);

    public PyhShop getShop(final int companyId, final int shopId);

    public PyhShop addShop(final int companyId, final PyhShopProperties shopProperties);

    public PyhShop updateShop(final int companyId, final int shopId, final PyhShopProperties shopProperties);

    public void deleteShop(final int companyId, final int shopId);

    public Collection<? extends PyhDepartment> getDepartments(int companyId);

    public PyhDepartment getDepartment(final int companyId, final int departmentId);

    public PyhDepartment addDepartment(final int companyId, final PyhDepartmentProperties shopProperties);

    public PyhDepartment updateDepartment(final int companyId, final int departmentId, final PyhDepartmentProperties shopProperties);

    public void deleteDepartment(final int companyId, final int departmentId);

    public Collection<? extends PyhShopDepartmentToShop> getShopDepartmentsToShop(final int companyId, int departmentId);

    public Collection<? extends PyhShopDepartment> getShopDepartments(final int companyId, int shopId);

    public PyhShopDepartment getShopDepartment(final int companyId, int shopId, int departmentId);

    public PyhShopDepartment setDepartmentInShopDepartment(final int companyId, int shopId, int departmentId,
            PyhShopDepartmentProperties shopDepartmentProperties);

    public void removeDepartmentFromShopDepartment(final int companyId, int shopId, int departmentId);

    public Collection<? extends PyhCompanyProduct> getCompanyProducts(final int companyId);

    public PyhCompanyProduct getCompanyProduct(final int companyId, int productId);

    public PyhCompanyProduct setProductInCompanyProduct(int companyId, int productId, PyhCompanyProductProperties aggregationPart);

    public void removeProductFromCompanyProduct(int companyId, int productId);

}

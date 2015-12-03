package com.programyourhome.server.controllers.shop;

import java.util.Collection;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.common.response.ServiceResult;
import com.programyourhome.server.controllers.AbstractProgramYourHomeServerController;
import com.programyourhome.shop.Shopping;
import com.programyourhome.shop.model.PyhProductAggregation;
import com.programyourhome.shop.model.PyhProductAggregationPart;
import com.programyourhome.shop.model.PyhProductAggregationPartProperties;
import com.programyourhome.shop.model.PyhProductAggregationProperties;
import com.programyourhome.shop.model.PyhProductAggregationState;

@RestController
@RequestMapping("shop/productAggregations")
public class ProgramYourHomeControllerShopProductAggregations extends AbstractProgramYourHomeServerController {

    @Inject
    private Shopping shopping;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ServiceResult<Collection<? extends PyhProductAggregation>> getProductAggregations() {
        return this.produce("ProductAggregations", () -> this.shopping.getProductAggregations());
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ServiceResult<PyhProductAggregation> getProductAggregation(@PathVariable("id") final int productAggregationId) {
        return this.produce("ProductAggregation", () -> this.shopping.getProductAggregation(productAggregationId));
    }

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MIME_JSON)
    public ServiceResult<PyhProductAggregation> createProductAggregation(@RequestBody final PyhProductAggregationProperties productAggregationProperties) {
        return this.produce("ProductAggregation", () -> this.shopping.createProductAggregation(productAggregationProperties));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT, consumes = MIME_JSON)
    public ServiceResult<PyhProductAggregation> updateProductAggregation(@PathVariable("id") final int productAggregationId,
            @RequestBody final PyhProductAggregationProperties productAggregationProperties) {
        return this.produce("ProductAggregation", () -> this.shopping.updateProductAggregation(productAggregationId, productAggregationProperties));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ServiceResult<Void> deleteProductAggregation(@PathVariable("id") final int productAggregationId) {
        return this.run(() -> this.shopping.deleteProductAggregation(productAggregationId));
    }

    @RequestMapping(value = "{id}/productAggregationParts", method = RequestMethod.GET)
    public ServiceResult<Collection<? extends PyhProductAggregation>> getProductAggregationParts() {
        return this.produce("ProductAggregations", () -> this.shopping.getProductAggregations());
    }

    @RequestMapping(value = "{id}/productAggregationParts/{productId}", method = RequestMethod.POST, consumes = MIME_JSON)
    public ServiceResult<PyhProductAggregationPart> setProductInProductAggregationPart(@PathVariable("id") final int productAggregationId,
            @PathVariable("productId") final int productId, @RequestBody final PyhProductAggregationPartProperties productAggregationPartProperties) {
        return this.produce("ProductAggregation",
                () -> this.shopping.setProductInProductAggregationPart(productId, productAggregationId, productAggregationPartProperties));
    }

    @RequestMapping(value = "{id}/productAggregationParts/{productId}", method = RequestMethod.DELETE)
    public ServiceResult<Void> removeProductFromProductAggregationPart(@PathVariable("id") final int productAggregationId,
            @PathVariable("productId") final int productId) {
        return this.run(() -> this.shopping.removeProductFromProductAggregationPart(productId, productAggregationId));
    }

    @RequestMapping(value = "{id}/state", method = RequestMethod.GET)
    public ServiceResult<PyhProductAggregationState> getProductAggregationState(@PathVariable("id") final int productAggregationId) {
        return this.produce("ProductAggregationState", () -> this.shopping.getProductAggregationState(productAggregationId));
    }

}

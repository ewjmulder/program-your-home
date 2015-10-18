package com.programyourhome.server.controllers.shop;

import java.math.BigDecimal;
import java.util.Collection;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.server.controllers.AbstractProgramYourHomeController;
import com.programyourhome.server.response.ServiceResult;
import com.programyourhome.shop.Shopping;
import com.programyourhome.shop.model.PyhProductAggregation;
import com.programyourhome.shop.model.PyhProductAggregationState;

@RestController
@RequestMapping("shop/productAggregations")
public class ProgramYourHomeControllerShopProductAggregations extends AbstractProgramYourHomeController {

    @Inject
    private Shopping shopping;

    @RequestMapping("")
    public ServiceResult<Collection<PyhProductAggregation>> getProductAggregations() {
        return this.produce("ProductAggregations", () -> this.shopping.getProductAggregations());
    }

    @RequestMapping("{id}")
    public ServiceResult<PyhProductAggregation> getProductAggregation(@PathVariable("id") final int productAggregationId) {
        return this.produce("ProductAggregation", () -> this.shopping.getProductAggregation(productAggregationId));
    }

    @RequestMapping("{id}/addProduct/{productId}/quantity/{quantity}/preference/{preference}")
    public ServiceResult<PyhProductAggregation> addProductToProductAggregation(@PathVariable("id") final int productAggregationId,
            @PathVariable("productId") final int productId,
            @PathVariable("quantity") final BigDecimal quantity, @PathVariable("preference") final int preference) {
        return this.produce("ProductAggregation", () -> this.shopping.addProductToProductAggregation(productId, productAggregationId, quantity, preference));
    }

    @RequestMapping("{id}/state")
    public ServiceResult<PyhProductAggregationState> getProductAggregationState(@PathVariable("id") final int productAggregationId) {
        return this.produce("ProductAggregationState", () -> this.shopping.getProductAggregationState(productAggregationId));
    }

    @RequestMapping("{id}/state/add/{amount}")
    public ServiceResult<PyhProductAggregationState> addToProductAggregation(@PathVariable("id") final int productAggregationId,
            @PathVariable("amount") final BigDecimal amount) {
        return this.produce("ProductAggregationState", () -> this.shopping.addToProductAggregationState(productAggregationId, amount));
    }

    @RequestMapping("{id}/state/remove/{amount}")
    public ServiceResult<PyhProductAggregationState> removeFromProductAggregation(@PathVariable("id") final int productAggregationId,
            @PathVariable("amount") final BigDecimal amount) {
        return this.produce("ProductAggregationState", () -> this.shopping.removeFromProductAggregationState(productAggregationId, amount));
    }

}

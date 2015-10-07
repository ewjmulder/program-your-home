package com.programyourhome.server.controllers.shop;

import java.util.Collection;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.server.controllers.AbstractProgramYourHomeController;
import com.programyourhome.server.response.ServiceResult;
import com.programyourhome.shop.Shopping;
import com.programyourhome.shop.model.PyhProduct;

@RestController
@RequestMapping("shop/products")
public class ProgramYourHomeControllerShopProducts extends AbstractProgramYourHomeController {

    @Inject
    private Shopping shopping;

    @RequestMapping("")
    public ServiceResult<Collection<PyhProduct>> getProducts() {
        return this.produce("Products", () -> this.shopping.getProducts());
    }

    @RequestMapping("{id}")
    public ServiceResult<PyhProduct> getProduct(@PathVariable("id") final int productId) {
        return this.produce("Product", () -> this.shopping.getProduct(productId));
    }

}

package com.programyourhome.shop;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.programyourhome.common.util.StreamUtil;
import com.programyourhome.shop.dao.ProductRepository;
import com.programyourhome.shop.model.PyhProduct;
import com.programyourhome.shop.model.api.PyhProductImpl;
import com.programyourhome.shop.model.jpa.Product;

@Component
public class ShoppingImpl implements Shopping {

    @Inject
    private ProductRepository productRepository;

    // TODO: Temp code to test
    @PostConstruct
    public void init() {
        this.productRepository.save(new Product("1234", "Spappel", "SPA Fruit Appel"));
        this.productRepository.save(new Product("5678", "Pindakaas", "AH Pindakaas met stukjes noot"));
    }

    @Override
    public Collection<PyhProduct> getProducts() {
        return StreamUtil.fromIterable(this.productRepository.findAll())
                .map(product -> new PyhProductImpl(product))
                .collect(Collectors.toList());
    }

    @Override
    public PyhProduct getProduct(final int productId) {
        return new PyhProductImpl(this.productRepository.findOne(productId));
    }

}

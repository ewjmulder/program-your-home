package com.programyourhome.shop;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.programyourhome.shop.dao.ProductRepository;
import com.programyourhome.shop.model.PyhProduct;
import com.programyourhome.shop.model.jpa.Product;

@Component
public class ShoppingImpl implements Shopping {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Collection<PyhProduct> getProducts() {
        // TODO: Temp code to test
        this.productRepository.save(new Product(1, "1234", "Spappel", "SPA Fruit Appel"));
        this.productRepository.save(new Product(2, "5678", "Pindakaas", "AH Pindakaas met stukjes noot"));

        System.out.println("Products found with findAll():");
        System.out.println("-------------------------------");
        for (final Product product : this.productRepository.findAll()) {
            System.out.println(product.getId() + ", " + product.getName());
        }
        System.out.println("");

        final Product product = this.productRepository.findOne(1);
        System.out.println("Product found with findOne(1):");
        System.out.println("--------------------------------");
        System.out.println(product.getId() + ", " + product.getName());
        System.out.println("");

        System.out.println("Product found with findByBarcode('1234'):");
        System.out.println("--------------------------------------------");
        final Product product2 = this.productRepository.findByBarcode("1234");
        System.out.println(product2.getId() + ", " + product2.getName());
        System.out.println("");

        return new ArrayList<>();
    }

    @Override
    public PyhProduct getProduct(final int productId) {
        return null;
    }

}

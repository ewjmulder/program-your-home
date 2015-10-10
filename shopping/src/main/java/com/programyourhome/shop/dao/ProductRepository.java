package com.programyourhome.shop.dao;

import com.programyourhome.shop.common.NamedEntityRepository;
import com.programyourhome.shop.model.jpa.Product;

public interface ProductRepository extends NamedEntityRepository<Product, Integer> {

    public Product findByBarcode(String barcode);

}

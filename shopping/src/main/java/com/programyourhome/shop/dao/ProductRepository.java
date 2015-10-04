package com.programyourhome.shop.dao;

import org.springframework.data.repository.CrudRepository;

import com.programyourhome.shop.model.jpa.Product;

public interface ProductRepository extends CrudRepository<Product, Integer> {

    public Product findByBarcode(String barcode);

}

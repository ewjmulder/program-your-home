package com.programyourhome.shop.dao;

import com.programyourhome.shop.common.NamedEntityRepository;
import com.programyourhome.shop.model.jpa.BulkProduct;

public interface BulkProductRepository extends NamedEntityRepository<BulkProduct, Integer> {

    public BulkProduct findByBarcode(String barcode);

}

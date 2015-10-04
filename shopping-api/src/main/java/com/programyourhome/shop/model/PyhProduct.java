package com.programyourhome.shop.model;

public interface PyhProduct {

    public int getId();

    public String getBarcode();

    public String getName();

    public String getDescription();

    public PyhProductImage getImage();

    /**
     * Get the number of items of this type of product that are 'available'. This means either
     * in stock or in use. This amount should be reduced if an item is finished / thrown away / no longer usable.
     * This amount should be increased when new (a) item(s) are bought and put in stock or in use.
     *
     * @return the amount 'available'
     */
    public int getAmount();

    /**
     * Get the minimum amount of items that should be 'available' of this product.
     *
     * @return the minimum amount
     */
    public int getMinimumAmount();

    /**
     * Get the maximum amount of items that should be 'available' of this product.
     *
     * @return the maximum amount
     */
    public int getMaximumAmount();

}

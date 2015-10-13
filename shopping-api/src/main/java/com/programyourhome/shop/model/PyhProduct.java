package com.programyourhome.shop.model;

public interface PyhProduct {

    public int getId();

    public String getBarcode();

    public String getName();

    public String getDescription();

    public PyhProductImage getImage();

    // FIXME: amount should not be a field of product. Static and dynamic data should be separated, at least in this PYH model. (maybe others as well)

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

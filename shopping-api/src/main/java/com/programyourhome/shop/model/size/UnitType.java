package com.programyourhome.shop.model.size;

import java.math.BigDecimal;

public interface UnitType extends UnitTypeIdentification {

    public default String getName() {
        return name();
    }

    /**
     * Will be present on all Enum types, which is assumed to be the implementing type of this interface.
     *
     * @return the (enum) name
     */
    public String name();

    public default boolean isSmallestUnit() {
        return getAmountInSmallestUnit().equals(BigDecimal.ONE);
    }

    public BigDecimal getAmountInSmallestUnit();

}

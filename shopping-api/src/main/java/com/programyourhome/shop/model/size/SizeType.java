package com.programyourhome.shop.model.size;

public enum SizeType {

    WEIGHT(WeightUnit.class),
    VOLUME(VolumeUnit.class),
    AREA(AreaUnit.class),
    LENGTH(LengthUnit.class),
    PIECE(PieceUnit.class);

    private Class<? extends Enum<?>> unitType;

    private SizeType(final Class<? extends Enum<?>> unitType) {
        this.unitType = unitType;
    }

    public Class<? extends Enum<?>> getUnitType() {
        return this.unitType;
    }
}

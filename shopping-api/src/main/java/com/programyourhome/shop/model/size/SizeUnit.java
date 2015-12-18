package com.programyourhome.shop.model.size;

import java.util.Arrays;

public enum SizeUnit {

    WEIGHT_GRAM(SizeType.WEIGHT, WeightUnit.GRAM),
    WEIGHT_KILOGRAM(SizeType.WEIGHT, WeightUnit.KILOGRAM),

    VOLUME_MILLILITER(SizeType.VOLUME, VolumeUnit.MILLILITER),
    VOLUME_LITER(SizeType.VOLUME, VolumeUnit.LITER),

    AREA_SQUARE_MILLIMETER(SizeType.AREA, AreaUnit.SQUARE_MILLIMETER),
    AREA_SQUARE_CENTIMETER(SizeType.AREA, AreaUnit.SQUARE_CENTIMETER),
    AREA_SQUARE_DECIMETER(SizeType.AREA, AreaUnit.SQUARE_DECIMETER),
    AREA_SQUARE_METER(SizeType.AREA, AreaUnit.SQUARE_METER),

    LENGTH_MILLIMETER(SizeType.LENGTH, LengthUnit.MILLIMETER),
    LENGTH_CENTIMETER(SizeType.LENGTH, LengthUnit.CENTIMETER),
    LENGTH_DECIMETER(SizeType.LENGTH, LengthUnit.DECIMETER),
    LENGTH_METER(SizeType.LENGTH, LengthUnit.METER),

    PIECE(SizeType.PIECE, PieceUnit.PIECE);

    private SizeType sizeType;
    private UnitType unit;

    private <T extends Enum<? extends UnitType> & UnitType> SizeUnit(final SizeType sizeType, final T unit) {
        this.sizeType = sizeType;
        this.unit = unit;
        if (!unit.getClass().equals(sizeType.getUnitType())) {
            throw new IllegalStateException("Size unit created for size type: " + sizeType +
                    " with unit type: " + unit.getClass() + ", but expected unit type: " + sizeType.getUnitType());
        }
    }

    public SizeType getSizeType() {
        return this.sizeType;
    }

    public UnitType getUnit() {
        return this.unit;
    }

    public static SizeUnit findByIdentification(final String unitTypeName, final String unitAbbreviation) {
        return Arrays.stream(SizeUnit.values())
                .filter(sizeUnit -> sizeUnit.getUnit().getTypeName().equals(unitTypeName)
                        && sizeUnit.getUnit().getAbbreviation().equals(unitAbbreviation))
                .findFirst()
                .orElse(null);
    }

}

package com.programyourhome.barcodescanner.model;

public enum MetaBarcode {

    SET_MODE_INFO(MetaBarcodeNumbers.META_BARCODE_0),
    SET_MODE_ADD_TO_STOCK(MetaBarcodeNumbers.META_BARCODE_1),
    SET_MODE_REMOVE_FROM_STOCK(MetaBarcodeNumbers.META_BARCODE_2);

    private String barcode;

    private MetaBarcode(final String barcode) {
        this.barcode = barcode;
    }

    public String getBarcode() {
        return this.barcode;
    }

}

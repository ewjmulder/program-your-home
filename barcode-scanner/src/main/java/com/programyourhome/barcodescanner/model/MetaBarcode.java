package com.programyourhome.barcodescanner.model;

public enum MetaBarcode {

    SET_MODE_INFO("0000000000000"),
    SET_MODE_ADD_TO_STOCK("0000000000001"),
    SET_MODE_REMOVE_FROM_STOCK("0000000000002");

    private String barcode;

    private MetaBarcode(final String barcode) {
        this.barcode = barcode;
    }

    public String getBarcode() {
        return this.barcode;
    }

}

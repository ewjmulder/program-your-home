package com.programyourhome.barcodescanner.model;

/**
 * Note: The actual produced barcodes have 13 digits, but somehow the scanner returns
 * just 12 digits for these 'meta' barcodes. (the first zero falls off)
 */
public interface MetaBarcodeNumbers {

    public static final String META_BARCODE_0 = "000000000000";
    public static final String META_BARCODE_1 = "000000000017";
    public static final String META_BARCODE_2 = "000000000024";
    public static final String META_BARCODE_3 = "000000000031";
    public static final String META_BARCODE_4 = "000000000048";
    public static final String META_BARCODE_5 = "000000000055";
    public static final String META_BARCODE_6 = "000000000062";
    public static final String META_BARCODE_7 = "000000000079";
    public static final String META_BARCODE_8 = "000000000086";
    public static final String META_BARCODE_9 = "000000000093";

}

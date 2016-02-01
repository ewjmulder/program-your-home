package com.programyourhome.barcodescanner.ui;

public interface RgbLedLights {

    public void setSystemStateBooting();

    public void setSystemStateNormal();

    public void setSystemStateError();

    public void setModeInfo();

    public void setModeAddToStock();

    public void setModeRemoveFromStock();

    public void setTransactionNone();

    public void setTransactionProcessing();

    public void setTransactionOk();

    public void setTransactionError();

}

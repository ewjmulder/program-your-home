package com.programyourhome.shop.model;

import java.math.BigDecimal;

public interface PyhMonetaryAmount {

    public Currency getCurrency();

    public BigDecimal getAmount();

}

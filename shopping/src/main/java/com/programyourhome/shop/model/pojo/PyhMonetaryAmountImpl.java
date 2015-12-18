package com.programyourhome.shop.model.pojo;

import java.math.BigDecimal;

import com.programyourhome.common.model.PyhImpl;
import com.programyourhome.shop.model.Currency;
import com.programyourhome.shop.model.PyhMonetaryAmount;

public class PyhMonetaryAmountImpl extends PyhImpl implements PyhMonetaryAmount {

    private final Currency currency;
    private final BigDecimal amount;

    public PyhMonetaryAmountImpl(final Currency currency, final BigDecimal amount) {
        this.currency = currency;
        this.amount = amount;
    }

    @Override
    public Currency getCurrency() {
        return this.currency;
    }

    @Override
    public BigDecimal getAmount() {
        return this.amount;
    }

}

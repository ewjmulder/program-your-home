package com.programyourhome.shop.model.pojo;

import com.programyourhome.common.model.PyhImpl;
import com.programyourhome.shop.model.PyhProductState;

public class PyhProductStateImpl extends PyhImpl implements PyhProductState {

    private final int id;
    private final int amount;

    public PyhProductStateImpl(final int id, final int amount) {
        this.id = id;
        this.amount = amount;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

}

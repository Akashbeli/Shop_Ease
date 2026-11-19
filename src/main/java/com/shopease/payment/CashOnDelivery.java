package com.shopease.payment;

import com.shopease.model.OrderStatus;

public class CashOnDelivery extends AbstractPayment {

    public CashOnDelivery() {
        super("COD", "Cash on delivery", "Pay in cash when the order reaches you.", "cash");
    }

    @Override
    public OrderStatus getInitialStatus() {
        return OrderStatus.PLACED;
    }
}

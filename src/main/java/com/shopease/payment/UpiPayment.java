package com.shopease.payment;

import com.shopease.model.OrderStatus;

/** Demo only: no real money moves. The order is simply marked as paid. */
public class UpiPayment extends AbstractPayment {

    public UpiPayment() {
        super("UPI", "UPI", "Pay with any UPI app (demo, nothing is charged).", "upi");
    }

    @Override
    public OrderStatus getInitialStatus() {
        return OrderStatus.PAID;
    }
}

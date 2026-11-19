package com.shopease.payment;

import com.shopease.model.OrderStatus;

/** Demo only: we never ask for or store card numbers. */
public class CardPayment extends AbstractPayment {

    public CardPayment() {
        super("CARD", "Debit or credit card", "Visa, Mastercard, RuPay (demo, nothing is charged).", "card");
    }

    @Override
    public OrderStatus getInitialStatus() {
        return OrderStatus.PAID;
    }
}

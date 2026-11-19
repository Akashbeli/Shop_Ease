package com.shopease.payment;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.shopease.exception.ShopException;

/** Factory pattern: hands out the right PaymentMethod for a code such as "UPI". */
public final class PaymentFactory {

    private static final Map<String, PaymentMethod> METHODS = new LinkedHashMap<>();

    static {
        register(new UpiPayment());
        register(new CardPayment());
        register(new CashOnDelivery());
    }

    private PaymentFactory() {
    }

    private static void register(PaymentMethod method) {
        METHODS.put(method.getCode(), method);
    }

    public static PaymentMethod find(String code) throws ShopException {
        PaymentMethod method = code == null ? null : METHODS.get(code);
        if (method == null) {
            throw new ShopException("Please choose a payment method.");
        }
        return method;
    }

    public static Collection<PaymentMethod> all() {
        return Collections.unmodifiableCollection(METHODS.values());
    }
}

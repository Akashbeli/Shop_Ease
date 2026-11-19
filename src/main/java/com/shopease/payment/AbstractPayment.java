package com.shopease.payment;

/** Shared plumbing for all payment methods (Abstract class). Subclasses only decide the status. */
public abstract class AbstractPayment implements PaymentMethod {

    private final String code;
    private final String label;
    private final String hint;
    private final String icon;

    protected AbstractPayment(String code, String label, String hint, String icon) {
        this.code = code;
        this.label = label;
        this.hint = hint;
        this.icon = icon;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getHint() {
        return hint;
    }

    @Override
    public String getIcon() {
        return icon;
    }
}

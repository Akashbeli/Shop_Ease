package com.shopease.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Formats rupee amounts the Indian way: 1,29,999 (not 129,999). */
public final class Money {

    private static final String RUPEE = "\u20B9";

    private Money() {
    }

    public static String format(BigDecimal amount) {
        BigDecimal value = (amount == null ? BigDecimal.ZERO : amount).setScale(2, RoundingMode.HALF_UP);
        String plain = value.abs().toPlainString();
        String whole = plain.substring(0, plain.indexOf('.'));
        String paise = plain.substring(plain.indexOf('.') + 1);

        String grouped = whole;
        if (whole.length() > 3) {
            String lastThree = whole.substring(whole.length() - 3);
            String rest = whole.substring(0, whole.length() - 3);
            StringBuilder head = new StringBuilder();
            for (int end = rest.length(); end > 0; end -= 2) {
                int start = Math.max(0, end - 2);
                head.insert(0, rest.substring(start, end));
                if (start > 0) {
                    head.insert(0, ',');
                }
            }
            grouped = head + "," + lastThree;
        }
        String text = RUPEE + grouped + ("00".equals(paise) ? "" : "." + paise);
        return value.signum() < 0 ? "-" + text : text;
    }
}

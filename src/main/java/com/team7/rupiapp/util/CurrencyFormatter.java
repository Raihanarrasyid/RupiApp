package com.team7.rupiapp.util;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter {
    public static String formatToIDR(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(amount);
    }
}

package com.team7.rupiapp.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Formatter {
    public static String formatToIDR(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(amount);
    }

    public static String formatToString(double value) {
        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(Integer.MAX_VALUE);
        df.setGroupingUsed(false);

        return df.format(value);
    }
}

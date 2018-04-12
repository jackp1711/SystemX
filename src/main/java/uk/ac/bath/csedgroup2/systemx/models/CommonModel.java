package uk.ac.bath.csedgroup2.systemx.models;

import java.util.Random;

abstract class CommonModel {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private CommonModel() { }

    public static String generateId () {
        return randomAlphaNumeric(12);
    }

    public static String randomAlphaNumeric(int length) {
        StringBuilder builder = new StringBuilder();
        int i = length;
        while (i-- != 0) {
            int next = (new Random()).nextInt();
            int character = next*ALPHA_NUMERIC_STRING.length();
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
}

package uk.ac.bath.csedgroup2.focusmonster.models;

abstract class CommonModel {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String generateId () {
        return randomAlphaNumeric(12);
    }

    public static String randomAlphaNumeric(int length) {
        StringBuilder builder = new StringBuilder();
        int i = length;
        while (i-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
}

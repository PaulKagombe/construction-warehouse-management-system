package util;

public final class ValidationHelper {

    private ValidationHelper() {
    }

    public static boolean isBlank(String text) {

        return text == null || text.trim().isEmpty();

    }

    public static boolean isPositiveInteger(String text) {

        try {

            return Integer.parseInt(text) > 0;

        } catch (Exception e) {

            return false;

        }
    }

    public static boolean isPositiveDouble(String text) {

        try {

            return Double.parseDouble(text) > 0;

        } catch (Exception e) {

            return false;

        }
    }

    public static int parseInteger(String text) {

        return Integer.parseInt(text);

    }

    public static double parseDouble(String text) {

        return Double.parseDouble(text);

    }

}
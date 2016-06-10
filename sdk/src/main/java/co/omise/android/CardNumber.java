package co.omise.android;

import co.omise.android.models.CardBrand;

/**
 * CardNumber provides helper methods for working with Personal Account Numbers.
 */
public final class CardNumber {

    /**
     * Normalize removes spaces and other non-numerical characters from the input string.
     *
     * @param number The card number to normalize.
     * @return Normalized string or an empty string if the input is null.
     */
    public static String normalize(String number) {
        if (number == null) return "";
        return number.replaceAll("[^0-9]", "");
    }

    /**
     * Format formats the given string by adding a single whitespace between group of
     * four digits.
     *
     * @param number The card number to format.
     * @return The input string with every four digits grouped together, or an empty string if the input is null.
     */
    public static String format(String number) {
        if (number == null) return "";

        StringBuilder builder = new StringBuilder();
        char[] chars = number.toCharArray();
        for (char ch : chars) {
            if ('0' <= ch && ch <= '9') {
                if ((builder.length() - 4) % 5 == 0) {
                    builder.append(' ');
                }
                builder.append(ch);
            }
        }

        return builder.toString();
    }

    /**
     * Brand returns {@link CardBrand} of a credit card given a number. The result from this method is
     * intended purely for displaying the brand on user interfaces and does not guarantee correctness.
     *
     * @param number The card number to check against.
     * @return A {@link CardBrand}, or null if the brand could not be determined.
     */
    public static CardBrand brand(String number) {
        number = normalize(number);
        for (CardBrand brand : CardBrand.ALL) {
            if (brand.match(number)) return brand;
        }

        return null;
    }

    /**
     * Luhn checks the input card number for validity using the
     * <a href="https://en.wikipedia.org/wiki/Luhn_algorithm">Luhn algorithm</a>.
     *
     * @param number The card number to check against.
     * @return true if the given card number passes the Luhn check, otherwise false.
     */
    public static boolean luhn(String number) {
        number = normalize(number);

        char[] chars = number.toCharArray();
        int[] digits = new int[chars.length];
        for (int i = 0; i < chars.length; i++) {
            digits[i] = (int) (chars[i] - '0');
        }

        int oddSum = 0, evenSum = 0;
        for (int i = digits.length - 1; i >= 0; i -= 2) {
            oddSum += digits[i];
        }
        for (int i = digits.length - 2; i >= 0; i -= 2) {
            evenSum += digits[i] * 2;
            if (digits[i] > 4) { // doubles > 9
                evenSum -= 9;
            }
        }

        return (oddSum + evenSum) % 10 == 0;
    }
}

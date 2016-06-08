package co.omise.android;

import co.omise.android.models.CardBrand;

/**
 * PAN provides helper methods for working with Personal Account Numbers.
 */
public final class PAN {

    /**
     * Normalize removes spaces and other non-numerical characters from the input string.
     * @param pan The PAN to normalize.
     * @return Normalized string or an empty string if the input is null.
     */
    public static String normalize(String pan) {
        if (pan == null) return "";
        return pan.replaceAll("[^0-9]", "");
    }

    /**
     * Format formats the given string by adding a single whitespace between group of
     * four digits.
     *
     * @param pan The PAN to format.
     * @return The input string with every four digits grouped together, or an empty string if the input is null.
     */
    public static String format(String pan) {
        if (pan == null) return "";

        StringBuilder builder = new StringBuilder();
        char[] chars = pan.toCharArray();
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
     * Brand returns {@link CardBrand} of a credit card given a PAN. The result from this method is
     * intended purely for displaying the brand on user interfaces and does not guarantee correctness.
     *
     * @param pan The PAN to check against.
     * @return A {@link CardBrand}, or null if the brand could not be determined.
     */
    public static CardBrand brand(String pan) {
        pan = normalize(pan);
        for (CardBrand brand : CardBrand.ALL) {
            if (brand.match(pan)) return brand;
        }

        return null;
    }

    /**
     * Luhn checks the input PAN for validity using the
     * <a href="https://en.wikipedia.org/wiki/Luhn_algorithm">Luhn algorithm</a>.
     *
     * @param pan The PAN to check against.
     * @return true if the given PAN passes the Luhn check, otherwise false.
     */
    public static boolean luhn(String pan) {
        pan = normalize(pan);

        char[] chars = pan.toCharArray();
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

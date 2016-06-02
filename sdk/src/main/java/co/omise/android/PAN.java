package co.omise.android;

public final class PAN {
    public static String normalize(String pan) {
        if (pan == null) return "";
        return pan.replaceAll("[^0-9]", "");
    }

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

    public static CardBrand brand(String pan) {
        pan = normalize(pan);
        for (CardBrand brand : CardBrand.ALL) {
            if (brand.match(pan)) return brand;
        }

        return null;
    }

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

    public static boolean isValid(String pan) {
        return luhn(pan) && brand(pan) != null;
    }
}

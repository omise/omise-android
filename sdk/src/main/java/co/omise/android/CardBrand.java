package co.omise.android;

import java.util.regex.Pattern;

public final class CardBrand {
    public static final CardBrand AMEX = new CardBrand("amex", "^3[47]", 15, 15);
    public static final CardBrand DINERS = new CardBrand("diners", "^3(0[0-5]|6)", 14, 14);
    public static final CardBrand JCB = new CardBrand("jcb", "^35(2[89]|[3-8])", 16, 16);
    public static final CardBrand LASER = new CardBrand("laser", "^(6304|670[69]|6771)", 16, 19);
    public static final CardBrand VISA = new CardBrand("visa", "^4", 16, 16);
    public static final CardBrand MASTERCARD = new CardBrand("mastercard", "^5[1-5]", 16, 16);
    public static final CardBrand MAESTRO = new CardBrand("maestro", "^(5018|5020|5038|6304|6759|676[1-3])", 12, 19);
    public static final CardBrand DISCOVER = new CardBrand("discover", "^(6011|622(12[6-9]|1[3-9][0-9]|[2-8][0-9]{2}|9[0-1][0-9]|92[0-5]|64[4-9])|65)", 16, 16);

    public static final CardBrand[] ALL = new CardBrand[] {
            AMEX,
            DINERS,
            JCB,
            LASER,
            VISA,
            MASTERCARD,
            MAESTRO,
            DISCOVER
    };

    private final String name;
    private final Pattern pattern;
    private final int minLength;
    private final int maxLength;

    public CardBrand(String name, String pattern, int minLength, int maxLength) {
        this.name = name;
        this.pattern = Pattern.compile(pattern + "[0-9]+");
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public String getName() {
        return name;
    }

    public boolean match(String pan) {
        if (pan == null || pan.isEmpty()) return false;
        return pattern.matcher(pan).matches();
    }

    public boolean valid(String pan) {
        return match(pan) && minLength <= pan.length() && pan.length() <= maxLength;
    }
}

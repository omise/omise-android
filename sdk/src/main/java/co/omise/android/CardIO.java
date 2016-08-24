package co.omise.android;

import android.content.Context;
import android.content.Intent;

import io.card.payment.CardIOActivity;

public final class CardIO {
    private static final Boolean _available = null;

    public static boolean isAvailable() {
        try {
            Class.forName("io.card.payment.CardIOActivity");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static Intent buildIntent(Context context) {
        Intent intent = new Intent(context, CardIOActivity.class);
        intent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true);
        intent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false);
        intent.putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, false);
        intent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false);
        intent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false);
        intent.putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, true);
        intent.putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, true);
        intent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true);
        return intent;
    }
}

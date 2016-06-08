package co.omise.android.ui;

import java.util.Locale;

public class ExpiryMonthSpinnerAdapter extends NumberRangeSpinnerAdapter {
    protected ExpiryMonthSpinnerAdapter() {
        super(1, 12);
    }

    @Override
    protected String getItemDropDownLabel(int number) {
        return String.format(Locale.getDefault(), "%02d", number);
    }

    @Override
    protected String getItemLabel(int number) {
        return String.format(Locale.getDefault(), "%02d", number);
    }

}

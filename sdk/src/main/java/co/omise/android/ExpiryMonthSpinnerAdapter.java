package co.omise.android;

import org.joda.time.YearMonth;

public class ExpiryMonthSpinnerAdapter extends NumberRangeSpinnerAdapter {
    protected ExpiryMonthSpinnerAdapter() {
        super(1, 12);
    }

    @Override
    protected String getItemDropDownLabel(int number) {
        String monthName = new YearMonth(2099, number).monthOfYear().getAsShortText();
        return String.format("%02d - %s", number, monthName);
    }

    @Override
    protected String getItemLabel(int number) {
        return String.format("%02d", number);
    }

}

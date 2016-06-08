package co.omise.android.ui;

import org.joda.time.YearMonth;

public class ExpiryYearSpinnerAdapter extends NumberRangeSpinnerAdapter {
    protected ExpiryYearSpinnerAdapter() {
        super(YearMonth.now().getYear(), YearMonth.now().getYear() + 12);
    }

    @Override
    protected String getItemDropDownLabel(int number) {
        return Integer.toString(number);
    }

    @Override
    protected String getItemLabel(int number) {
        return Integer.toString(number).substring(2, 4);
    }
}

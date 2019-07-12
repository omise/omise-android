package co.omise.android.ui;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;

public class CreditCardEditText extends OmiseEditText {

    public CreditCardEditText(Context context) {
        super(context);
        init();
    }

    public CreditCardEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CreditCardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
        setInputType(InputType.TYPE_CLASS_PHONE);

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable e) {
                if (e.length() <= 0 || (e.length() % 5) != 0) {
                    return;
                }

                char c = e.charAt(e.length() - 1);
                if (Character.isDigit(c)) {
                    // Insert space bar
                    e.insert(e.length() - 1, " ");
                } else if (c == ' ') {
                    // Delete space bar
                    e.delete(e.length() - 1, e.length());
                }
            }
        });
    }

}

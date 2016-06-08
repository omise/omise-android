package co.omise.android.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class CreditCardEditText extends EditText {
    private boolean suppressEvent = false;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CreditCardEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
        setInputType(InputType.TYPE_CLASS_PHONE);
    }

    /* inject SPC and DEL keys as the user types to make sure the textbox always show numbers in
     * groups of 4.
     *
     * For example:
     * [1234]   -> input [5]  output [1234 5] (space + 5)
     * [1234 5] -> input [^H] output [1234]   (del + del)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (suppressEvent) {
            return super.onKeyDown(keyCode, event);
        }

        int length = getText().length();
        boolean beforeSeparator = (length - 4) % 5 == 0;
        boolean afterSeparator = length % 5 == 0;
        if (isDeletion(keyCode) && afterSeparator) {
            sendKey(KeyEvent.KEYCODE_DEL);
        } else if (isDigit(keyCode) && beforeSeparator) {
            sendKey(KeyEvent.KEYCODE_SPACE);
        }

        return super.onKeyDown(keyCode, event);
    }

    private boolean isDigit(int keyCode) {
        return (KeyEvent.KEYCODE_0 <= keyCode && keyCode <= KeyEvent.KEYCODE_9) ||
                (KeyEvent.KEYCODE_NUMPAD_0 <= keyCode && keyCode <= KeyEvent.KEYCODE_NUMPAD_9);
    }

    private boolean isDeletion(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_DEL;
    }

    private void sendKey(int keyCode) {
        suppressEvent = true;
        try {
            dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
            dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyCode));
        } finally {
            suppressEvent = false;
        }
    }
}

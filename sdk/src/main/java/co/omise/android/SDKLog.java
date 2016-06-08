package co.omise.android;

public final class SDKLog {
    public static String TAG = "co.omise.android";

    public static void d(String message) {
        android.util.Log.d(TAG, message);
    }

    public static void e(String message, Throwable e) {
        android.util.Log.e(TAG, message, e);
    }

    public static void wtf(String message, Throwable e) {
        android.util.Log.wtf(TAG, message, e);
    }
}

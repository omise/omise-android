package co.omise.android;

import android.os.Handler;
import android.os.Looper;

class BackgroundThread extends Thread {
    private Handler handler;

    BackgroundThread() {
        setName("co.omise.android");
    }

    public void post(Runnable runnable) {
        handler.post(runnable);
    }

    @Override
    public void run() {
        handler = new Handler();
        Looper.loop();
    }
}

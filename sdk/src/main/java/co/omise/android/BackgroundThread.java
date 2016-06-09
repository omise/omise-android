package co.omise.android;

import android.os.Handler;
import android.os.Looper;

import java.util.UUID;

class BackgroundThread extends Thread {
    private Handler handler;

    BackgroundThread() {
        setName("co.omise.android." + UUID.randomUUID().toString());
    }

    public void post(Runnable runnable) {
        handler.post(runnable);
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new Handler();
        Looper.loop();
    }
}

package co.domix.android.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import co.domix.android.R;

public class PayToCancel extends Service {

    public static final String ACTION_COUNTER_BUTTON = CounterButtonImHere.class.getName() + "CounterButton";
    private CountDownTimer countDown;
    public static final String COUNTER = "counter_button";

    public void onCreate() {
        super.onCreate();
    }

    private void finishTime(boolean timeout) {
        Intent intent = new Intent(ACTION_COUNTER_BUTTON);
        intent.putExtra(COUNTER, timeout);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        countDown = new CountDownTimer(2 * 60 * 1000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                finishTime(true);
            }
        };
        countDown.start();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDown.cancel();
    }
}

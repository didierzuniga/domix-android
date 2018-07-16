package co.domix.android.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import co.domix.android.R;

public class CounterButtonImHere extends Service {

    public static final String ACTION_COUNTER_BUTTON = CounterButtonImHere.class.getName() + "CounterButton";
    private CountDownTimer countDown;
    public static final String COUNTER = "counter_button";
    private SharedPreferences shaPref;
    private SharedPreferences.Editor editor;

    public void onCreate() {
        super.onCreate();
        shaPref = getSharedPreferences(getString(R.string.const_sharedpreference_file_name), MODE_PRIVATE);
        editor = shaPref.edit();
    }

    private void finishTime(boolean timeout) {
        editor.putBoolean(getString(R.string.const_sharedPref_key_button_i_am_here), timeout);
        editor.commit();
        Intent intent = new Intent(ACTION_COUNTER_BUTTON);
        intent.putExtra(COUNTER, timeout);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        editor.putBoolean(getString(R.string.const_sharedPref_key_created_service_count_im_here), true);
        editor.commit();
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

    public void onDestroy(){
        super.onDestroy();
    }
}

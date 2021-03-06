package co.domix.android.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.domix.android.R;
import co.domix.android.domiciliary.view.Domiciliary;
import co.domix.android.model.Order;

/**
 * Created by unicorn on 2/11/2018.
 */

public class NotificationService extends Service {

    private int identifyOrder;
    private boolean isServiceActive;
    private SharedPreferences shaPref;
    private SharedPreferences.Editor editor;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceOrder = database.getReference("order");

    public void queryForNewOrder(){
        identifyOrder = 100;
        referenceOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (!order.isX_catched()) {
                        if (order.getX_id() > identifyOrder) {
                            identifyOrder = order.getX_id();
//                            if (shaPref.getBoolean("IsServiceActive", false)) {
                            if (isServiceActive) {
                                createNotification();
                                break; // This line is for test
                            } else {
                                referenceOrder.removeEventListener(this);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void createNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_white)
                .setLargeIcon((((BitmapDrawable)getResources()
                        .getDrawable(R.drawable.ic_isotipo_domix)).getBitmap()))
                .setContentTitle(getString(R.string.notification_incoming_order_title))
                .setContentText(getString(R.string.notification_incoming_order_text));

        Intent intent = new Intent(this, Domiciliary.class);
        PendingIntent contIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(contIntent);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        shaPref = getSharedPreferences(getString(R.string.const_sharedpreference_file_name), MODE_PRIVATE);
        editor = shaPref.edit();
//        editor.putBoolean("IsServiceActive", true);
        isServiceActive = true;
        editor.commit();
        queryForNewOrder();
    }

    @Override
    public void onDestroy() {
//        editor.putBoolean("IsServiceActive", false);
        isServiceActive = false;
        editor.putBoolean(getString(R.string.const_sharedPref_key_backfromServiceNotification), true);
        editor.commit();
        super.onDestroy();
    }
}

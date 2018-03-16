package co.domix.android.domiciliary.service;

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
import android.util.Log;

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

    private SharedPreferences location;
    private SharedPreferences.Editor editor;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceOrder = database.getReference("order");

    public void queryForNewOrder(){
        referenceOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (!order.isX_catched()) {
                        if (location.getBoolean("IsServiceActive", false)){
                            createNotification();
                        } else {
                            referenceOrder.removeEventListener(this);
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
                .setContentTitle("Nuevo")
                .setContentText("Hay una nueva solicitud de domicilio")
                .setTicker("¡Nuevo domicilio!");

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
        location = getSharedPreferences("Locate_prefs", MODE_PRIVATE);
        editor = location.edit();
        editor.putBoolean("IsServiceActive", true);
        editor.commit();
        queryForNewOrder();
    }

    @Override
    public void onDestroy() {
        editor.putBoolean("IsServiceActive", false);
        editor.putBoolean("backFromServiceNotification", true);
        editor.commit();
        super.onDestroy();
    }
}

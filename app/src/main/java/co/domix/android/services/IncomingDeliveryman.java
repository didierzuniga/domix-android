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
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.model.Order;
import co.domix.android.user.view.Requested;

public class IncomingDeliveryman extends Service {

    private boolean isServiceActive;
    private DomixApplication app;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceOrd = database.getReference("order");

    public void queryForIncomingDeliveryman(){
        referenceOrd.child(String.valueOf(app.idOrder)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Order order = dataSnapshot.getValue(Order.class);
                if (isServiceActive) {
                    if (order.isX_catched()){
                        createNotification();
                    }
                } else {
                    Log.w("jjj", "Remove listener from service");
                    referenceOrd.child(String.valueOf(app.idOrder)).removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

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
                .setContentTitle(getString(R.string.notification_incoming_deliveryman_title))
                .setContentText(getString(R.string.notification_incoming_deliveryman_text));

        Intent intent = new Intent(this, Requested.class);
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
        app = (DomixApplication) getApplicationContext();
        isServiceActive = true;
        queryForIncomingDeliveryman();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceActive = false;
        queryForIncomingDeliveryman();
    }
}

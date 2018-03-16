package co.domix.android.domiciliary.repository;

import android.app.Activity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import co.domix.android.R;
import co.domix.android.domiciliary.presenter.OrderCatchedPresenter;
import co.domix.android.model.Counter;
import co.domix.android.model.Order;
import co.domix.android.model.User;

/**
 * Created by unicorn on 11/13/2017.
 */

public class OrderCatchedRepositoryImpl implements OrderCatchedRepository {

    private int whatToast = 0;
    private boolean queryForOrderActive;
    private OrderCatchedPresenter presenter;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceUser = database.getReference("user");
    DatabaseReference referenceOrder = database.getReference("order");
    DatabaseReference referenceCoord = database.getReference("coordinate");
    DatabaseReference referenceCounter = database.getReference("counter");

    public OrderCatchedRepositoryImpl(OrderCatchedPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getUserRequest(final int idOrder, final String uid, final Activity activity) {
        referenceOrder.child(String.valueOf(idOrder)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Order order = dataSnapshot.getValue(Order.class);

                String uidAuthor = order.getA_id();
                String countryAuthor = order.getX_country();
                String cityAuthor = order.getX_city();
                String fromAuthor = order.getX_nameFrom();
                String toAuthor = order.getX_nameTo();
                String titleAuthor = order.getX_description1();
                String descriptionAuthor = order.getX_description2();
                String oriLa = order.x_latitudeFrom.toString();
                String oriLo = order.x_longitudeFrom.toString();
                String desLa = order.x_latitudeTo.toString();
                String desLo = order.x_longitudeTo.toString();
                int moneyAuthor = order.getX_moneyToPay();
                getNameAndPhoneAuthor(uidAuthor, countryAuthor, cityAuthor, fromAuthor, toAuthor,
                        titleAuthor, descriptionAuthor, oriLa, oriLo,
                        desLa, desLo, moneyAuthor);
                verifyOrderActive(uid, activity);

                referenceOrder.child(String.valueOf(idOrder)).removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Error en getUserRequest
            }
        });
    }

    @Override
    public void getNameAndPhoneAuthor(final String uidAuthor, final String countryAuthor, final String cityAuthor, final String fromAuthor,
                                      final String toAuthor, final String titleAuthor, final String descriptionAuthor,
                                      final String oriLa, final String oriLo, final String desLa,
                                      final String desLo, final int moneyAuthor) {
        referenceUser.child(uidAuthor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String firstName = user.getFirstName();
                String lastName = user.getLastName();
                String fullName = firstName + " " + lastName;
                String phone = user.getPhone();
                presenter.responseUserRequested(fullName, phone, countryAuthor, cityAuthor, fromAuthor,
                        toAuthor, titleAuthor, descriptionAuthor, oriLa,
                        oriLo, desLa, desLo, moneyAuthor);
                referenceUser.child(uidAuthor).removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Error en getUserRequest
            }
        });
    }

    @Override
    public void dialogCancel(final String idOrder, String uid, final Activity activity) {
        whatToast = 1;
        referenceOrder.child(idOrder).child("d_id").removeValue();
        referenceOrder.child(idOrder).child("x_catched").setValue(false);
        removeCoordDomiciliary(uid);
        Toast.makeText(activity, R.string.toast_you_has_cancelled_order, Toast.LENGTH_LONG).show();
        presenter.responseBackDomiciliaryActivity();
    }

    @Override
    public void dialogFinish(String idOrder, final String uidDomicili, Activity activity) {
        deductCounterRealtime();
        referenceOrder.child(idOrder).child("x_completed").setValue(true);
        //removeCoordDomiciliary(uidDomicili);
        presenter.goRateDomiciliary();
    }

    @Override
    public void submitCoord(String uid, String la, String lo, Activity activity) {
        referenceCoord.child(uid).child("latitude").setValue(la);
        referenceCoord.child(uid).child("longitude").setValue(lo);
    }

    @Override
    public void verifyOrderActive(final String uid, final Activity activity) {
        referenceOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                queryForOrderActive = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Order order = snapshot.getValue(Order.class);
                    if (uid.equals(order.getD_id()) && (order.isX_completed()) == false){
                        queryForOrderActive = true;
                    }
                }
                if (!queryForOrderActive){
                    if (whatToast == 0){
                        Toast.makeText(activity, R.string.toast_user_has_cancelled_order, Toast.LENGTH_SHORT).show();
                    }
                    presenter.responseBackDomiciliaryActivity();
                    referenceOrder.removeEventListener(this);
                    removeCoordDomiciliary(uid);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void removeCoordDomiciliary(final String uid) {
        Thread mThread = new Thread(){
            public void run () {
                try {
                    sleep(5000);
                    referenceCoord.child(uid).child("latitude").removeValue();
                    referenceCoord.child(uid).child("longitude").removeValue();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
    }

    @Override
    public void deductCounterRealtime() {
        referenceCounter.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Counter c = mutableData.getValue(Counter.class);
                if (c == null){
                    return Transaction.success(mutableData);
                }

                c.countRealTime = c.countRealTime - 1;
                mutableData.setValue(c);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }
}

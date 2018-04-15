package co.domix.android.domiciliary.repository;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.domix.android.R;
import co.domix.android.domiciliary.interactor.DomiciliaryInteractor;
import co.domix.android.domiciliary.presenter.DomiciliaryPresenter;
import co.domix.android.model.Order;
import co.domix.android.model.User;

/**
 * Created by unicorn on 11/13/2017.
 */

public class DomiciliaryRepositoryImpl implements DomiciliaryRepository {

    private int countChild = 0;
    private String i;
    private boolean catchedOrderAvailable;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceUser = database.getReference("user");
    DatabaseReference referenceOrder = database.getReference("order");
    private DomiciliaryPresenter presenter;
    private DomiciliaryInteractor interactor;

    public DomiciliaryRepositoryImpl(DomiciliaryPresenter presenter, DomiciliaryInteractor interactor) {
        this.presenter = presenter;
        this.interactor = interactor;
    }

    @Override
    public void searchDeliveries(final String latDomi, final String lonDomi) {
        referenceOrder.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Order order = snapshot.getValue(Order.class);
                    boolean catched = order.isX_catched();
                    if (!catched){
                        countChild++;
                        int idOrder = order.getX_id();
                        String ago = order.getRelativeTimeStamp();
                        String from = order.getX_nameFrom();
                        String to = order.getX_nameTo();
                        int sizeOrder = order.getX_transportUsed();
                        String description1 = order.getX_description1();
                        String description2 = order.getX_description2();
                        String oriLa = order.getX_latitudeFrom();
                        String oriLo = order.getX_longitudeFrom();
                        String desLa = order.getX_latitudeTo();
                        String desLo = order.getX_longitudeTo();
//                        presenter.goCompareDistance(idOrder, ago, from, to, description1, description2,
//                                                    oriLa, oriLo, desLa, desLo, latDomi, lonDomi);
                        interactor.goCompareDistance(idOrder, ago, from, to, sizeOrder, description1, description2,
                                oriLa, oriLo, desLa, desLo, latDomi, lonDomi);
                    }
                }
//                presenter.countChild(countChild);
                interactor.countChild(countChild);
                countChild = 0;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void sendDataDomiciliary(final Activity activity, int idOrderToSend, final String uid, final int transportUsed) {
        i = String.valueOf(idOrderToSend);
        referenceOrder.child(i).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Order order = dataSnapshot.getValue(Order.class);
                catchedOrderAvailable = order.isX_catched();
                if (catchedOrderAvailable == false) {
//                    updateDataDomiciliary(uid, i);
                    referenceOrder.child(i).child("d_id").setValue(uid);
                    presenter.responseGoOrderCatched(i);
                    referenceOrder.child(i).child("x_catched").setValue(true);
                    referenceOrder.child(i).child("x_transportUsed").setValue(transportUsed);
                    referenceUser.child(uid).child("usedVehicle").setValue(transportUsed);
                } else {
                    presenter.responseOrderHasBeenTaken();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Error en sendDataDomiciliary
            }
        });
    }

    @Override
    public void updateDataDomiciliary(final String uidCurrentUser, final String i) {
//        referenceOrder.child(i).child("d_id").setValue(uidCurrentUser);
//        presenter.responseGoOrderCatched(i);
//        referenceOrder.child(i).child("x_catched").setValue(true);
    }

    @Override
    public void queryForFullnameAndPhone(String uid) {
        referenceUser.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getFirstName() == null ||
                        user.getLastName() == null ||
                        user.getPhone() == null) {
                    presenter.responseForFullnameAndPhone(false);
                } else {
                    presenter.responseForFullnameAndPhone(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void queryUserRate(String idOrder) {
        referenceOrder.child(idOrder).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Order order = dataSnapshot.getValue(Order.class);
                String uidAuthor = order.getA_id();
                referenceUser.child(uidAuthor).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        String userRate = String.format("%.2f", user.getScoreAsUser());
                        presenter.responseQueryRate(userRate);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void sendContactData(String uid, String firstName, String lastName, String phone) {
        referenceUser.child(uid).child("firstName").setValue(firstName);
        referenceUser.child(uid).child("lastName").setValue(lastName);
        referenceUser.child(uid).child("phone").setValue(phone);
        presenter.contactDataSent();
    }
}

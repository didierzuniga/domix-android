package co.domix.android.domiciliary.repository;

import android.app.Activity;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import co.domix.android.R;
import co.domix.android.api.RetrofitDatetimeAdapter;
import co.domix.android.api.RetrofitDatetimeService;
import co.domix.android.domiciliary.interactor.DomiciliaryInteractor;
import co.domix.android.domiciliary.presenter.DomiciliaryPresenter;
import co.domix.android.model.Fare;
import co.domix.android.model.Order;
import co.domix.android.model.Parameter;
import co.domix.android.model.Time;
import co.domix.android.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by unicorn on 11/13/2017.
 */

public class DomiciliaryRepositoryImpl implements DomiciliaryRepository {

    private int countChild = 0, minDistanceBetweenRequiredForCyclist, minDistanceBetweenRequiredForOther;
    private String i;
    private int stampNow;
    private boolean catchedOrderAvailable, orderStill;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceUser = database.getReference("user");
    DatabaseReference referenceOrder = database.getReference("order");
    DatabaseReference referenceFare = database.getReference("fare");
    DatabaseReference referenceParameter = database.getReference("parameter");
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
                    final Order order = snapshot.getValue(Order.class);
                    boolean catched = order.isX_catched();
                    if (!catched){
                        countChild++;
                        final String country = order.getX_country();
                        final int idOrder = order.getX_id();
                        final String ago = order.getX_time();
                        final String from = order.getX_name_from();
                        final String to = order.getX_name_to();
                        final int sizeOrder = order.getX_dimension_selected();
                        final String description1 = order.getX_description1();
                        final String description2 = order.getX_description2();
                        final String origenCoordinate = order.getX_coordinate_from();
                        final String destineCoordinate = order.getX_coordinate_to();
                        final int distanceBetween = order.getX_distance_between_points();
                        referenceParameter.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Parameter parameter = dataSnapshot.getValue(Parameter.class);
                                minDistanceBetweenRequiredForCyclist = parameter.getMin_dis_between_points_for_cyclist();
                                minDistanceBetweenRequiredForOther = parameter.getMin_dis_between_points_for_other();
                                interactor.goCompareDistance(idOrder, ago, country, from, to, sizeOrder, description1, description2,
                                        origenCoordinate, destineCoordinate, latDomi, lonDomi, distanceBetween,
                                        minDistanceBetweenRequiredForCyclist, minDistanceBetweenRequiredForOther);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
//                        interactor.goCompareDistance(idOrder, ago, country, from, to, sizeOrder, description1, description2,
//                                oriLa, oriLo, desLa, desLo, latDomi, lonDomi, distanceBetween, minDistanceBetweenRequired);
                    }
                }
                interactor.countChild(countChild);
                countChild = 0;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void sendDataDomiciliary(final Activity activity, final int idOrderToSend, final String uid,
                                    final int transportUsed, String country) {
        orderStill = false;
        referenceFare.child(country).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Fare fare = dataSnapshot.getValue(Fare.class);
                final float appliedFare;
                if (transportUsed == 1){
                    appliedFare = fare.getFare_domix_for_cyclist();
                } else {
                    appliedFare = fare.getFare_domix();
                }

                i = String.valueOf(idOrderToSend);
                referenceOrder.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if ((snapshot.getKey()).equals(i)){
                                orderStill = true;
                                referenceOrder.child(i).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Order order = dataSnapshot.getValue(Order.class);
//                                      catchedOrderAvailable = order.isX_catched();
                                        if (!order.isX_catched()) {
                                            DecimalFormat df = new DecimalFormat();
                                            df.setMaximumFractionDigits(2);
                                            referenceOrder.child(i).child("d_id").setValue(uid);
                                            presenter.responseGoOrderCatched(i);
                                            referenceOrder.child(i).child("x_applied_fare").setValue(appliedFare);
                                            referenceOrder.child(i).child("x_catched").setValue(true);
                                            referenceOrder.child(i).child("x_transport_used").setValue(transportUsed);
                                            referenceUser.child(uid).child("transport_used").setValue(transportUsed); // For User model
                                        } else {
                                            presenter.responseOrderHasBeenTaken();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Error en sendDataDomiciliary
                                    }
                                });
                                break;
                            }
                        }
                        if (!orderStill){
                            presenter.responseOrderHasBeenTaken();
                        }
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
    public void queryPersonalDataFill(String uid) {
        referenceUser.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getFirst_name() == null ||
                        user.getLast_name() == null ||
                        user.getDni() == null ||
                        user.getPhone() == null ||
                        user.isImage_profile() == false) {
                    presenter.responseQueryPersonalDataFill(false);
                } else {
                    presenter.responseQueryPersonalDataFill(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void queryUserRate(String idOrder) {
        final NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        referenceOrder.child(idOrder).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Order order = dataSnapshot.getValue(Order.class);
                String uidAuthor = order.getA_id();
                referenceUser.child(uidAuthor).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        Float userRate = new Float(formatter.format(user.getScore_as_user()));
                        presenter.responseQueryRate(String.valueOf(userRate));
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
}

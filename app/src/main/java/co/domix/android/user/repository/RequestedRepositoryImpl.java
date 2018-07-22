package co.domix.android.user.repository;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import co.domix.android.R;
import co.domix.android.model.Coordinate;
import co.domix.android.model.Counter;
import co.domix.android.model.Fare;
import co.domix.android.model.Order;
import co.domix.android.model.User;
import co.domix.android.user.presenter.RequestedPresenter;

/**
 * Created by unicorn on 11/12/2017.
 */

public class RequestedRepositoryImpl implements RequestedRepository {

    private static final String ORDER_FIELD_PATERN = "order";
    private static final String COUNTER_FIELD_PATERN = "counter";
    private String idDomiciliaryListen, idDomiciliaryUpdate;
    private double latDomiciliary, lonDomiciliary;
    private boolean finallyListener = false, orderHasBenCompleted = false, orderHasBenCatched, thereCoordinate;
    private RequestedPresenter presenter;

    public RequestedRepositoryImpl(RequestedPresenter presenter) {
        this.presenter = presenter;
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceOrder = database.getReference(ORDER_FIELD_PATERN);
    DatabaseReference referenceUser = database.getReference("user");
    DatabaseReference referenceFare = database.getReference("fare");
    DatabaseReference referenceCounter = database.getReference(COUNTER_FIELD_PATERN);
    DatabaseReference referenceCoordenatesDomi = database.getReference("coordinate");

    @Override
    public void listenForUpdate(final int idOrder, final Activity activity) {
        referenceOrder.child(String.valueOf(idOrder)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (finallyListener){
                    referenceOrder.child(String.valueOf(idOrder)).removeEventListener(this);
                } else {
                    try {
                        final Order order = dataSnapshot.getValue(Order.class);
                        final int totalCostDelivery = order.getX_credit_used() + order.getX_money_to_pay();
                        boolean completed = order.isX_completed();
                        Double scoredDomi = order.getX_score_deliveryman();
                        if (!completed) {
                            String origenCoordinate = order.x_coordinate_from.toString();
                            String destineCoordinate = order.x_coordinate_to.toString();
                            presenter.responseCoordinatesFromTo(origenCoordinate, destineCoordinate);
                            if (order.isX_catched()) {
                                orderHasBenCompleted = false;
                                idDomiciliaryListen = order.getD_id();
                                getDataDomiciliary(idDomiciliaryListen);
                            } else {
                                orderHasBenCompleted = true;
                                presenter.resultNotCatched();
                            }
                        } else if (completed == true && scoredDomi == null) {
                            referenceOrder.child(String.valueOf(idOrder)).removeEventListener(this);
//                            Codigo para remunerar al usuario con un porcentaje del costo del servicio
                            referenceUser.child(order.getA_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final User user = dataSnapshot.getValue(User.class);
                                    referenceFare.child(order.getX_country()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Fare fare = dataSnapshot.getValue(Fare.class);
                                            int resultEarnedCredit = (int)(totalCostDelivery * fare.getPrctg_earned_credit());
                                            referenceUser.child(order.getA_id().toString()).child("my_credit").setValue(resultEarnedCredit + user.getMy_credit());
                                            presenter.goRateUser(resultEarnedCredit, fare.getCurrency_code());
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
                    } catch (Exception e){
                    }
                }
//                Aqui no coloco Remove para que escuche domiciliario entrante
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Error en DB
            }
        });


    }

    @Override
    public void dialogCancel(boolean afterTwoMinutes, final String uid, final int idOrder, final Activity activity) {
        if (afterTwoMinutes){
            // Tratar el cargo de la tarifa minima al solicitante
            finallyListener = true;
            removeOrder(uid, idOrder, activity);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(R.string.message_cancel_request);
            builder.setPositiveButton(R.string.message_yes,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finallyListener = true;
                            removeOrder(uid, idOrder, activity);
                        }
                    }
            )
                    .setNegativeButton(R.string.message_no, null);
            builder.create().show();
        }
    }

    @Override
    public void removeOrder(final String uid, int idOrder, Activity activity) {
        referenceOrder.child(String.valueOf(idOrder)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Order order = dataSnapshot.getValue(Order.class);

                referenceUser.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        referenceUser.child(uid).child("my_credit").setValue(order.getX_credit_used() + user.getMy_credit());
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
        referenceOrder.child(String.valueOf(idOrder)).removeValue();
        deductCounters();
    }

    @Override
    public void deductCounters() {
        referenceCounter.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Counter c = mutableData.getValue(Counter.class);
                if (c == null){
                    return Transaction.success(mutableData);
                }
                c.count_realtime = c.count_realtime - 1;
                mutableData.setValue(c);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                //This call was in deductCounters()
                presenter.resultGoUserActivity();
            }
        });
    }

    @Override
    public void updateDomiPosition(final int idOrder, final Activity activity) {
        referenceOrder.child(String.valueOf(idOrder)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (finallyListener){
                    orderHasBenCompleted = true;
                    referenceOrder.child(String.valueOf(idOrder)).removeEventListener(this);
                } else {
                    Order order = dataSnapshot.getValue(Order.class);
                    orderHasBenCatched = false;
                    try {
                        boolean completed = order.isX_completed();
                        if (!completed) {
                            if (order.isX_catched()) {
                                orderHasBenCatched = true;
                                idDomiciliaryUpdate = order.getD_id();
                                requestCoordinates(order.getD_id(), activity);
                            }
                        } else {
                            orderHasBenCompleted = true;
                            referenceOrder.child(String.valueOf(idOrder)).removeEventListener(this);
                            removeCoordDomiciliary(idDomiciliaryUpdate);
                        }
                    } catch (Exception e){

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Error en DB
            }
        });
    }

    @Override
    public void requestCoordinates(final String idDomiciliary, Activity activity) {
        // Here the thread
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            referenceCoordenatesDomi.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                        if (snapshot.getKey().equals(idDomiciliary)){
                                            thereCoordinate = true;
                                            break;
                                        } else {
                                            thereCoordinate = false;
                                        }
                                    }

                                    if (thereCoordinate){
                                        referenceCoordenatesDomi.child(idDomiciliary).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshotx) {
                                                if (!orderHasBenCompleted && orderHasBenCatched) {
                                                    Coordinate coordinate = dataSnapshotx.getValue(Coordinate.class);
                                                    String lt = coordinate.getLatitude();
                                                    String ln = coordinate.getLongitude();
                                                    latDomiciliary = Double.valueOf(lt);
                                                    lonDomiciliary = Double.valueOf(ln);
                                                    presenter.responseCoordDomiciliary(latDomiciliary, lonDomiciliary);
                                                } else {
                                                    referenceCoordenatesDomi.child(idDomiciliary).removeEventListener(this);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                // Error DB
                                            }
                                        });
                                    } else {
                                        presenter.repeatUpdateDomi();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } catch (Exception e){}
                    }
                }, 3000);
            }
        });
    }

    @Override
    public void getDataDomiciliary(final String uidDomiciliary) {
        final NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(1);
        formatter.setMinimumFractionDigits(1);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        try{
            referenceUser.child(uidDomiciliary).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    String fullName = user.getFirst_name()+" "+user.getLast_name();
                    String cellPhone = user.getPhone();
                    Float rate = new Float(formatter.format(user.getScore_as_deliveryman()));
                    int usedVehicle = user.getTransport_used();
                    presenter.responseDomiciliaryCatched(uidDomiciliary, String.valueOf(rate), fullName,
                            cellPhone, usedVehicle);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){}

    }

    @Override
    public void removeCoordDomiciliary(String uid) {
        referenceCoordenatesDomi.child(uid).child("latitude").removeValue();
        referenceCoordenatesDomi.child(uid).child("longitude").removeValue();
    }
}
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

import co.domix.android.R;
import co.domix.android.model.Coordinate;
import co.domix.android.model.Counter;
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
    private boolean finallyListener = false, orderHasBenCompleted = false, orderHasBenCatched;
    private RequestedPresenter presenter;

    public RequestedRepositoryImpl(RequestedPresenter presenter) {
        this.presenter = presenter;
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceOrder = database.getReference(ORDER_FIELD_PATERN);
    DatabaseReference referenceUser = database.getReference("user");
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
                    Order order = dataSnapshot.getValue(Order.class);
                    boolean completed = order.isX_completed();
                    Double scoredDomi = order.getX_scoreDomiciliary();
                    if (!completed) {
                        String oriLa = order.x_latitudeFrom.toString();
                        String oriLo = order.x_longitudeFrom.toString();
                        String desLa = order.x_latitudeTo.toString();
                        String desLo = order.x_longitudeTo.toString();
                        presenter.responseCoordinatesFromTo(oriLa, oriLo, desLa, desLo);
                        if (order.isX_catched()) {
                            orderHasBenCompleted = false;
                            idDomiciliaryListen = order.getD_id();
                            getDataDomiciliary(idDomiciliaryListen);
                        } else {
                            orderHasBenCompleted = true;
                            presenter.resultNotCatched();
                        }
                    } else if (completed == true && scoredDomi == null) {
                        goRateUser();
                        referenceOrder.child(String.valueOf(idOrder)).removeEventListener(this);
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
    public void dialogCancel(final int idOrder, final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.message_cancel_request);
        builder.setPositiveButton(R.string.message_yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finallyListener = true;
                        removeOrder(idOrder, activity);
                    }
                }
        )
                .setNegativeButton(R.string.message_no, null);
        builder.create().show();
    }

    @Override
    public void removeOrder(int idOrder, Activity activity) {
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

                c.countRealTime = c.countRealTime - 1;
                c.counterDone = c.counterDone - 1;
                mutableData.setValue(c);
                presenter.resultGoUserActivity();
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    @Override
    public void goRateUser() {
        presenter.goRateUser();
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
                        } catch (Exception e){}
                    }
                }, 15000);
            }
        });
    }

    @Override
    public void getDataDomiciliary(final String uidDomiciliary) {
        try{
            referenceUser.child(uidDomiciliary).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    String fullName = user.getFirstName()+" "+user.getLastName();
                    String cellPhone = user.getPhone();
                    String rate = String.format("%.2f", user.getScoreAsDomiciliary());
                    presenter.responseDomiciliaryCatched(uidDomiciliary, rate, fullName,
                            cellPhone);
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

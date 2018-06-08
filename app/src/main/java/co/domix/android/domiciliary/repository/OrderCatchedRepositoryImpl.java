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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.domiciliary.interactor.OrderCatchedInteractor;
import co.domix.android.domiciliary.presenter.OrderCatchedPresenter;
import co.domix.android.model.Counter;
import co.domix.android.model.Fare;
import co.domix.android.model.Order;
import co.domix.android.model.User;
import co.domix.android.model.Wallet;

/**
 * Created by unicorn on 11/13/2017.
 */

public class OrderCatchedRepositoryImpl implements OrderCatchedRepository {

    private boolean finishedByDeliveryman = false, cancelledByDeliveryman= false, thereWallet;
    private String date;
    private int fareToPayDomix, paidOut;
    private OrderCatchedPresenter presenter;
    private OrderCatchedInteractor interactor;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceUser = database.getReference("user");
    DatabaseReference referenceFare = database.getReference("fare");
    DatabaseReference referenceOrder = database.getReference("order");
    DatabaseReference referenceWallet = database.getReference("wallet");
    DatabaseReference referenceCoord = database.getReference("coordinate");
    DatabaseReference referenceCounter = database.getReference("counter");

    public OrderCatchedRepositoryImpl(OrderCatchedPresenter presenter, OrderCatchedInteractor interactor) {
        this.presenter = presenter;
        this.interactor = interactor;
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
                String fromAuthor = order.getX_name_from();
                String toAuthor = order.getX_name_to();
                String description1 = order.getX_description1();
                String description2 = order.getX_description2();
                String origenCoordinate = order.getX_coordinate_from();
                String destineCoordinate = order.getX_coordinate_to();
                int moneyCash = order.getX_money_to_pay();
                int moneyCredit = order.getX_credit_used();
                int paymentMethod = order.getX_pay_method();
                getNameAndPhoneAuthor(uidAuthor, countryAuthor, cityAuthor, fromAuthor, toAuthor,
                        description1, description2, origenCoordinate, destineCoordinate, moneyCash,
                        moneyCredit, paymentMethod);
                verifyStatusOrder(uid, String.valueOf(idOrder), activity);
//                referenceOrder.child(String.valueOf(idOrder)).removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Error en getUserRequest
            }
        });
    }

    @Override
    public void getNameAndPhoneAuthor(final String uidAuthor, final String countryAuthor, final String cityAuthor,
                                      final String fromAuthor, final String toAuthor, final String description1,
                                      final String description2, final String origenCoordinate, final String destineCoordinate,
                                      final int moneyCash, final int moneyCredit,
                                      final int paymentMethod) {
        referenceUser.child(uidAuthor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String firstName = user.getFirst_name();
                String lastName = user.getLast_name();
                final String fullName = firstName + " " + lastName;
                final String phone = user.getPhone();
                referenceFare.child(countryAuthor).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Fare fare = dataSnapshot.getValue(Fare.class);
                        interactor.responseUserRequested(fullName, phone, fare.getCurrency_code(),
                                                        cityAuthor, fromAuthor, toAuthor, description1,
                                                        description2, origenCoordinate, destineCoordinate,
                                                        moneyCash, moneyCredit, paymentMethod);
                        referenceUser.child(uidAuthor).removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Error en getUserRequest
            }
        });
    }

    @Override
    public void dialogCancel(final String idOrder, String uid, final Activity activity) {
        referenceOrder.child(idOrder).child("d_id").removeValue();
        referenceOrder.child(idOrder).child("x_applied_fare").removeValue();
        referenceOrder.child(idOrder).child("x_transportUsed").removeValue();
        referenceOrder.child(idOrder).child("x_catched").setValue(false);
        removeCoordDomiciliary(uid);
        presenter.showToastDeliverymanCancelledOrder();
        presenter.responseBackDomiciliaryActivity();
        cancelledByDeliveryman = true;
    }

    @Override
    public void dialogFinish(final String idOrder, final String uid) {
        thereWallet = false;
        finishedByDeliveryman = true;
        referenceOrder.child(idOrder).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Order order = dataSnapshot.getValue(Order.class);
                date = order.getX_date();
                fareToPayDomix += (int) ((order.getX_money_to_pay() + order.getX_credit_used()) *
                        order.getX_applied_fare());
                if (order.getX_pay_method() == 1 || order.getX_pay_method() == 2){
                    paidOut += order.getX_money_to_pay() + order.getX_credit_used();
                } else if (order.getX_credit_used() > 0){
                    paidOut += order.getX_credit_used();
                }

                referenceWallet.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if ((snapshot.getKey()).equals(uid)){
                                thereWallet = true;
                                referenceWallet.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Wallet wallet = dataSnapshot.getValue(Wallet.class);
                                        List<String> li = wallet.getOrder_id();
                                        li.add(idOrder);
                                        referenceWallet.child(uid).child("order_id").setValue(li);
                                        referenceWallet.child(uid).child("to_domix").setValue(fareToPayDomix + wallet.getTo_domix());
                                        referenceWallet.child(uid).child("charged").setValue(paidOut + wallet.getCharged());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                break;
                            }
                        }
                        if (!thereWallet){
                            List<String> li = new ArrayList<String>();
                            li.add(idOrder);
                            referenceWallet.child(uid).child("order_id").setValue(li);
                            referenceWallet.child(uid).child("to_domix").setValue(fareToPayDomix);
                            referenceWallet.child(uid).child("charged").setValue(paidOut);
                            referenceWallet.child(uid).child("date").setValue(date);
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
        referenceOrder.child(idOrder).child("x_completed").setValue(true);

        //Remove coordinates???
        modifyCounterRealtimeAndDone();
    }

    @Override
    public void submitCoord(String uid, String la, String lo, Activity activity) {
        referenceCoord.child(uid).child("latitude").setValue(la);
        referenceCoord.child(uid).child("longitude").setValue(lo);
    }

    @Override
    public void verifyStatusOrder(final String uid, String idorder, final Activity activity) {
        referenceOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean wasTakedByDeliveryman = false;
                long countTotalOrders = dataSnapshot.getChildrenCount();
                long counter = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    counter++;
                    Order order = snapshot.getValue(Order.class);


                    if (!finishedByDeliveryman && !cancelledByDeliveryman){
                        if (!wasTakedByDeliveryman){
                            if (uid.equals(order.getD_id()) && !(order.isX_completed())) {
                                wasTakedByDeliveryman = true;
                            } else {
                                if (counter == countTotalOrders) {
                                    presenter.showToastUserCancelledOrder();
                                    presenter.responseBackDomiciliaryActivity();
                                    removeCoordDomiciliary(uid);
                                    referenceOrder.removeEventListener(this);
                                }
                            }
                        }
                    } else {
                        referenceOrder.removeEventListener(this);
                    }
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
    public void modifyCounterRealtimeAndDone() {
        referenceCounter.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Counter c = mutableData.getValue(Counter.class);
                if (c == null){
                    return Transaction.success(mutableData);
                }
                c.count_realtime -= 1;
                c.count_done += 1;
                mutableData.setValue(c);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                //This call was in dialogFinish()
                presenter.goRateDomiciliary();
            }
        });
    }
}

package co.domix.android.customizer.repository;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import co.domix.android.customizer.interactor.TotalToPayInteractor;
import co.domix.android.customizer.presenter.TotalToPayPresenter;
import co.domix.android.model.Fare;
import co.domix.android.model.Order;
import co.domix.android.model.User;

/**
 * Created by unicorn on 1/14/2018.
 */

public class TotalToPayRepositoryImpl implements TotalToPayRepository {

    private String country, currencyCode, userId;
    private int payUFullRate, minPayment, fareToPayDomix, pagado;
    private float nationalTaxe, payUCommission, fareDomix, fareDomixForCyclist;
    private boolean areThereOrders;
    private TotalToPayPresenter presenter;
    private TotalToPayInteractor interactor;

    public TotalToPayRepositoryImpl(TotalToPayPresenter presenter, TotalToPayInteractor interactor) {
        this.presenter = presenter;
        this.interactor = interactor;
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceOrder = database.getReference("order");
    DatabaseReference referenceFare = database.getReference("fare");
    DatabaseReference referenceUser = database.getReference("user");

    @Override
    public void queryOrderToPay(final String uid) {
        areThereOrders = false;
        userId = uid;
        referenceUser.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                if (user.getCounter_score_as_deliveryman() > 0){
                    referenceOrder.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final List<String> listOrders = new ArrayList<String>();

                            for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Order order = snapshot.getValue(Order.class);
                                if ((order.getD_id()).equals(uid)) {
                                    if (!order.isX_paid_out()) {
                                        areThereOrders = true;
                                        country = order.getX_country();
                                        listOrders.add(snapshot.getKey()); //ID to save
                                        fareToPayDomix += (int) ((order.getX_money_to_pay() + order.getX_credit_used()) *
                                                order.getX_applied_fare());
                                        if (order.getX_pay_method() == 1 || order.getX_pay_method() == 2){
                                            pagado += order.getX_money_to_pay() + order.getX_credit_used();
                                        } else if (order.getX_credit_used() > 0){
                                            pagado += order.getX_credit_used();
                                        }
                                    }
                                }
                            }
                            if (areThereOrders) {
                                referenceFare.child(country).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Fare fare = dataSnapshot.getValue(Fare.class);
                                        currencyCode = fare.getCurrency_code();
                                        nationalTaxe = fare.getNational_tax();
                                        payUCommission = fare.getPayu_commission();
                                        payUFullRate = fare.getPayu_full_rate();
                                        minPayment = fare.getMin_payment();
                                        interactor.responseTotalToPay(currencyCode, fareToPayDomix,
                                                (pagado + user.getPositive_balance()), nationalTaxe,
                                                minPayment, payUCommission, payUFullRate, country, listOrders);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                presenter.thereAreNotOrders();
                            }





                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    presenter.thereAreNotOrders();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void goPayU(List<String> list, int balance) {
        for (int i = 0; i < list.size(); i++) {
            referenceOrder.child(String.valueOf(list.get(i))).child("x_paid_out").setValue(true);
        }

        referenceUser.child(userId).child("positive_balance").setValue(balance);
    }
}

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

/**
 * Created by unicorn on 1/14/2018.
 */

public class TotalToPayRepositoryImpl implements TotalToPayRepository {

    String country = "CO";
    private int totalToPayCash;
    private int payUFullRate, minPayment;
    private float nationalTaxe, payUCommission, fareDomix;
    private TotalToPayPresenter presenter;
    private TotalToPayInteractor interactor;

    public TotalToPayRepositoryImpl(TotalToPayPresenter presenter, TotalToPayInteractor interactor) {
        this.presenter = presenter;
        this.interactor = interactor;
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceOrder = database.getReference("order");
    DatabaseReference referenceFare = database.getReference("fare");

    @Override
    public void queryOrderToPay(final String uid) {
        referenceOrder.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> listOrders = new ArrayList<String>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if ((order.getD_id()).equals(uid)) {
                        if (!order.isX_paid_out()){
                            country = order.getX_country();
                            listOrders.add(snapshot.getKey()); //ID to save
                            totalToPayCash += order.getX_money_to_pay();
                        }
                    }
                }
                referenceFare.child(country).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Fare fare = dataSnapshot.getValue(Fare.class);
                        nationalTaxe = fare.getNational_tax();
                        fareDomix = fare.getFare_domix();
                        payUCommission = fare.getPayu_commission();
                        payUFullRate = fare.getPayu_full_rate();
                        minPayment = fare.getMin_payment();
                        interactor.responseTotalToPay(totalToPayCash, nationalTaxe, fareDomix, minPayment,
                                                      payUCommission, payUFullRate, country);
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

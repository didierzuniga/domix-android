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
import co.domix.android.model.Wallet;

/**
 * Created by unicorn on 1/14/2018.
 */

public class TotalToPayRepositoryImpl implements TotalToPayRepository {

    private String country, currencyCode, userId;
    private int payUFullRate, minPayment, fareToPayDomix, pagado;
    private float nationalTaxe, payUCommission, fareDomix, fareDomixForCyclist;
    private boolean areThereOrders;
    private List<String> listOrders = new ArrayList<String>();
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
    DatabaseReference referenceWallet = database.getReference("wallet");

    @Override
    public void queryOrderToPay(final String uid) {
        areThereOrders = false;
        userId = uid;
        referenceWallet.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Wallet wallet = snapshot.getValue(Wallet.class);
                    if ((snapshot.getKey()).equals(uid)){
                        areThereOrders = true;
                        country = wallet.getCountry_code();
                        fareToPayDomix = wallet.getTo_domix();
                        pagado = wallet.getCharged();
                        listOrders = wallet.getOrder_id();
                        break;
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
                                    pagado, nationalTaxe, minPayment, payUCommission, payUFullRate, country, listOrders);
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

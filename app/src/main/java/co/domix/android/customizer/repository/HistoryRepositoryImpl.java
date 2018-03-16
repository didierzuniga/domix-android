package co.domix.android.customizer.repository;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import co.domix.android.adapter.AdapterHistory;
import co.domix.android.customizer.presenter.HistoryPresenter;
import co.domix.android.model.Order;

/**
 * Created by unicorn on 1/30/2018.
 */

public class HistoryRepositoryImpl implements HistoryRepository {

    AdapterHistory adapter;
    ArrayList<Order> orders;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceOrder = database.getReference("order");
    private HistoryPresenter presenter;

    public HistoryRepositoryImpl(HistoryPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void listOrder(Activity activity, RecyclerView rv, final String uid) {
        orders = new ArrayList<>();
        adapter = new AdapterHistory(orders, activity);
        rv.setAdapter(adapter);
        referenceOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orders.removeAll(orders);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    String uidAuthor = order.getA_id();
                    String uidDomiciliary = order.getD_id();
                    if (uid.equals(uidAuthor) || uid.equals(uidDomiciliary)){
                        if (order.isX_completed()) {
                            orders.add(order);
                        } else {
                            orders.isEmpty();
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Error en listOrders
            }
        });
    }
}

package co.domix.android.user.repository;

import android.app.Activity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import co.domix.android.model.Order;
import co.domix.android.model.User;
import co.domix.android.user.presenter.UserScorePresenter;

/**
 * Created by unicorn on 11/13/2017.
 */

public class UserScoreRepositoryImpl implements UserScoreRepository {

    private static final String ORDER_FIELD_PATERN = "order";
    private static final String USER_FIELD_PATERN = "user";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceOrder = database.getReference(ORDER_FIELD_PATERN);
    DatabaseReference referenceUser = database.getReference(USER_FIELD_PATERN);
    private UserScorePresenter presenter;

    public UserScoreRepositoryImpl(UserScorePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void sendScore(final Double score, int idOrder, Activity activity) {
        referenceOrder.child(String.valueOf(idOrder)).child("x_scoreDomiciliary").setValue(score);
        referenceOrder.child(String.valueOf(idOrder)).child("x_scored").setValue(true);
        referenceOrder.child(String.valueOf(idOrder)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Order order = dataSnapshot.getValue(Order.class);
                String uidDomiciliary = order.getD_id();
                insertScore(score, uidDomiciliary);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void insertScore(final Double score, String uidDomiciliary) {
        referenceUser.child(uidDomiciliary).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User u = mutableData.getValue(User.class);
                if (u == null){
                    return Transaction.success(mutableData);
                }
                u.scoreAsDomiciliary = ((u.scoreAsDomiciliary * u.counterScoreAsDomi) + score) / (u.counterScoreAsDomi + 1);
                u.counterScoreAsDomi = u.counterScoreAsDomi + 1;
                mutableData.setValue(u);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                presenter.responseBackHomeActivity();
            }
        });
    }
}

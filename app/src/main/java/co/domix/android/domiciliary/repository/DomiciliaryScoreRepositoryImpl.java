package co.domix.android.domiciliary.repository;

import android.app.Activity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import co.domix.android.domiciliary.presenter.DomiciliaryScorePresenter;
import co.domix.android.model.Order;
import co.domix.android.model.User;

/**
 * Created by unicorn on 11/13/2017.
 */

public class DomiciliaryScoreRepositoryImpl implements DomiciliaryScoreRepository {

    private static final String ORDER_FIELD_PATERN = "order";
    private static final String USER_FIELD_PATERN = "user";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceOrder = database.getReference(ORDER_FIELD_PATERN);
    DatabaseReference referenceUser = database.getReference(USER_FIELD_PATERN);
    private DomiciliaryScorePresenter presenter;

    public DomiciliaryScoreRepositoryImpl(DomiciliaryScorePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void sendScore(final Double score, int idOrder, Activity activity) {
        referenceOrder.child(String.valueOf(idOrder)).child("x_score_author").setValue(score);
        referenceOrder.child(String.valueOf(idOrder)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Order order = dataSnapshot.getValue(Order.class);
                String uidAuthor = order.getA_id();
                insertRate(score, uidAuthor);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void insertRate(final Double score, String uidAuthor) {
        referenceUser.child(uidAuthor).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User u = mutableData.getValue(User.class);
                if (u == null){
                    return Transaction.success(mutableData);
                }
                u.score_as_user = ((u.score_as_user * u.counter_score_as_user) + score) / (u.counter_score_as_user + 1);
                u.counter_score_as_user += 1;
                mutableData.setValue(u);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                presenter.responseBackDomiciliaryActivity();
            }
        });
    }
}

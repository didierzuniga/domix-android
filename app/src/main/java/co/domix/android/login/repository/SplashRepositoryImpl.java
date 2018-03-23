package co.domix.android.login.repository;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.domix.android.login.presenter.SplashPresenter;
import co.domix.android.model.Order;

/**
 * Created by unicorn on 11/22/2017.
 */

public class SplashRepositoryImpl extends AppCompatActivity implements SplashRepository {

    private static final String ORDER_FIELD_PATERN = "order";
    private Order order;
    private int orderId;
    private String uidAuthor, uidDomiciliary;
    private Double scoreDomiciliary, scoreAuthor;
    private int withoutOrder = 0;
    private boolean completed;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceOrder = database.getReference(ORDER_FIELD_PATERN);
    private SplashPresenter presenter;

    public SplashRepositoryImpl(SplashPresenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public void queryStatePosition(final String uid, final Activity activity) {
        referenceOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    order = snapshot.getValue(Order.class);
                    orderId = order.getX_id();
                    uidAuthor = order.getA_id();
                    uidDomiciliary = order.getD_id();
                    completed = order.isX_completed();
                    scoreDomiciliary = order.getX_scoreDomiciliary();
                    scoreAuthor = order.getX_scoreAuthor();
                    if (uid.equals(uidAuthor)) {
                        withoutOrder = 1;
                        if (completed == false) {
                            presenter.goOrderRequested(orderId);
                        } else if (scoreDomiciliary == null) {
                            presenter.goUserScore(orderId);
                        } else {
                            presenter.goHome();
                        }
                    } else if (uid.equals(uidDomiciliary)) {
                        withoutOrder = 1;
                        if (completed == false) {
                            presenter.goOrderCatched(orderId);
                        } else if (scoreAuthor == null) {
                            presenter.goDomiciliaryScore(orderId);
                        } else {
                            presenter.goHome();
                        }
                    }
                }
                if (withoutOrder == 0){
                    presenter.goHome();
                }
                referenceOrder.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}

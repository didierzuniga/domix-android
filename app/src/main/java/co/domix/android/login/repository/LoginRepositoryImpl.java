package co.domix.android.login.repository;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import co.domix.android.login.presenter.LoginPresenter;
import co.domix.android.model.Counter;
import co.domix.android.model.Order;
import co.domix.android.model.User;

/**
 * Created by unicorn on 11/11/2017.
 */

public class LoginRepositoryImpl implements LoginRepository {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceUser = database.getReference("user");
    private DatabaseReference referenceOrder = database.getReference("order");
    private FirebaseAuth firebaseAuth;
    private int orderId;
    private String uidAuthor, uidDomiciliary;
    private Double scoreDomiciliary, scoreAuthor;
    private boolean completed, tr;
    private FirebaseUser user;
    private int withoutOrder = 0;
    private Order order;
    private LoginPresenter presenter;

    public LoginRepositoryImpl(LoginPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void signin(final String email, String password, final Activity activity, FirebaseAuth firebaseAuth) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                if (user.isEmailVerified()){
                                    queryStatePosition(user.getUid(), user.getEmail(), activity);
                                    referenceUser.child(user.getUid()).child("active").setValue(true);
                                } else {
                                    presenter.responseVerifyEmailFalse();
                                }
                            }
                        } else {
                            presenter.signinError(task.getException().toString());
                        }
                    }
                });
    }

    @Override
    public void restorePassword(String email) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            presenter.resetPasswordSent();
                        }
                    }
                });
    }

    @Override
    public void queryStatePosition(final String uid, final String email, final Activity activity) {
        referenceOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    order = snapshot.getValue(Order.class);
                    orderId = order.getX_id();
                    uidAuthor = order.getA_id();
                    uidDomiciliary = order.getD_id();
                    completed = order.isX_completed();
                    scoreDomiciliary = order.getX_score_deliveryman();
                    scoreAuthor = order.getX_score_author();
                    if (uid.equals(uidAuthor)) {
                        withoutOrder = 1;
                        if (completed == false) {
                            presenter.goOrderRequested(uid, email, orderId);
                        } else if (scoreDomiciliary == null) {
                            presenter.goUserScore(uid, email, orderId);
                        } else {
                            presenter.goHome(uid, email);
                        }
                    } else if (uid.equals(uidDomiciliary)) {
                        withoutOrder = 1;
                        if (completed == false) {
                            presenter.goOrderCatched(uid, email, orderId);
                        } else if (scoreAuthor == null) {
                            presenter.goDomiciliaryScore(uid, email, orderId);
                        } else {
                            presenter.goHome(uid, email);
                        }
                    }
                }
                if (withoutOrder == 0){
                    presenter.goHome(uid, email);
                }
                referenceOrder.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}

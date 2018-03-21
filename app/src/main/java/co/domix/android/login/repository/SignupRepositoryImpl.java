package co.domix.android.login.repository;

import android.app.Activity;
import android.support.annotation.NonNull;

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

import co.domix.android.login.presenter.SignupPresenter;
import co.domix.android.model.Counter;
import co.domix.android.model.User;

/**
 * Created by unicorn on 3/20/2018.
 */

public class SignupRepositoryImpl implements SignupRepository {

    private FirebaseUser user;
    private SignupPresenter presenter;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceCounter = database.getReference("counter");
    private DatabaseReference referenceUser = database.getReference("user");

    public SignupRepositoryImpl(SignupPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void signup(final String email, String password, final Activity activity, final FirebaseAuth firebaseAuth) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                final String mail = user.getEmail();
                                final String uid = user.getUid();
                                sendEmailVerification(mail, uid, activity);
                            }
                        } else {
                            presenter.responseErrorSignup();
                        }
                    }
                });
    }

    @Override
    public void sendEmailVerification(final String email, final String uid, final Activity activity) {
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    setDataUser(email, uid, activity);
                    presenter.responseSuccessSignup(email);
                }
            }
        });
    }

    @Override
    public void setDataUser(final String email, final String uid, Activity activity) {
        referenceCounter.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Counter c = mutableData.getValue(Counter.class);
                if (c == null) {
                    return Transaction.success(mutableData);
                }
                c.counterUser++;
                User usr = new User(email, 10, 0.0, 0.0, 0,
                        0, false, false);
                referenceUser.child(uid).setValue(usr);
                mutableData.setValue(c);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }
}

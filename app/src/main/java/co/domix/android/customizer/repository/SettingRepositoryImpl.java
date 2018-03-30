package co.domix.android.customizer.repository;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import co.domix.android.customizer.interactor.SettingInteractor;
import co.domix.android.customizer.presenter.SettingPresenter;

/**
 * Created by unicorn on 3/27/2018.
 */

public class SettingRepositoryImpl implements SettingRepository {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceUser = database.getReference("user");
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private SettingPresenter presenter;

    public SettingRepositoryImpl(SettingPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void dialogReauthenticate(String email, String password, final int opt) {
        user = firebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    if (opt == 1){
                        presenter.goToChangePassword();
                    } else if (opt == 2){
                        referenceUser.child(uid).child("active").setValue(false);
                        deleteAccount();
                    }
                } else {
                    presenter.failedCredential();
                }
            }
        });
    }

    @Override
    public void changePassword(String password) {
        user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            presenter.successChangePassword();
                        } else {
                            presenter.errorChangePassword();
                        }
                    }
                });
    }


    public void deleteAccount(){
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    presenter.goLogin();
                    // User account deleted
                }
            }
        });
    }
}

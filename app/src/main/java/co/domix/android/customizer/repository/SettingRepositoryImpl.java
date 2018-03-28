package co.domix.android.customizer.repository;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import co.domix.android.customizer.interactor.SettingInteractor;
import co.domix.android.customizer.presenter.SettingPresenter;

/**
 * Created by unicorn on 3/27/2018.
 */

public class SettingRepositoryImpl implements SettingRepository {

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private SettingPresenter presenter;

    public SettingRepositoryImpl(SettingPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void dialogReauthenticate(String email, String password, final int opt) {
        user = firebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (opt == 1){
                    presenter.goToChangePassword();
                } else if (opt == 2){
                    deleteAccount();
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
                            Log.d("jjj", "User password updated.");
                        }
                    }
                });
    }


    public void deleteAccount(){
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("jjj", "User account deleted.");
                }
            }
        });
    }
}

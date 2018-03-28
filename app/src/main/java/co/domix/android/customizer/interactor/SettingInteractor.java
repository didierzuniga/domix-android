package co.domix.android.customizer.interactor;

/**
 * Created by unicorn on 3/27/2018.
 */

public interface SettingInteractor {
    void dialogReauthenticate(String email, String password, int opt);
    void changePassword(String newPassword);
}

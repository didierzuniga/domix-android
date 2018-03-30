package co.domix.android.customizer.presenter;

/**
 * Created by unicorn on 3/27/2018.
 */

public interface SettingPresenter {
    void dialogReauthenticate(String email, String password, int opt);
    void goToChangePassword();
    void changePassword(String newPassword);
    void successChangePassword();
    void errorChangePassword();
    void failedCredential();
    void goLogin();
}

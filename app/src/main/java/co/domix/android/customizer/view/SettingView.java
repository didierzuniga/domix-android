package co.domix.android.customizer.view;

/**
 * Created by unicorn on 3/27/2018.
 */

public interface SettingView {
    void showProgressBar();
    void hideProgressBar();
    void goToChangePassword();
    void successChangePassword();
    void errorChangePassword();
    void failedCredential();
    void goLogin();
}

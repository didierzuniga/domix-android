package co.domix.android.customizer.view;

/**
 * Created by unicorn on 12/18/2017.
 */

public interface ProfileView {
    void responseDataUser(boolean verifyGlide, String firstname, String lastname, String dnidentification, String phone,
                          String email, String scoreAsDomi, String scoreAsUser, String credit);
    void queryVerifyGlide();
    void putTrueImage();
    void executeGlide();
    void showProgressBar();
    void hideProgressBar();
    void goHome();
    void goProfile();
    void goHistory();
    void goSetting();
    void goPayment();
    void logOut();
}

package co.domix.android.customizer.view;

/**
 * Created by unicorn on 12/18/2017.
 */

public interface ProfileView {
    void responseDataUser(boolean verifyGlide, String firstname, String lastname, String email, String scoreAsDomi,
                          String scoreAsUser);
    void queryVerifyGlide();
    void putTrueImage();
    void executeGlide();
    void showProgressBar();
    void hideProgressBar();
}

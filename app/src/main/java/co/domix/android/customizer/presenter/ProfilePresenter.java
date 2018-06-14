package co.domix.android.customizer.presenter;

/**
 * Created by unicorn on 12/18/2017.
 */

public interface ProfilePresenter {
    void queryImageSeted(String uid);
    void responseDataUser(boolean verifyGlide, String firstname, String lastname, String dni, String phone,
                          String email, String scoreAsDomi, String scoreAsUser, String credit);
    void trueImageSeted(String uid);
}

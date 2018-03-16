package co.domix.android.customizer.presenter;

/**
 * Created by unicorn on 12/18/2017.
 */

public interface ProfilePresenter {
    void queryImageSeted(String uid);
    void responseDataUser(boolean verifyGlide, String firstname, String lastname, String email, String scoreAsDomi,
                          String scoreAsUser);
    void trueImageSeted(String uid);
}

package co.domix.android.customizer.interactor;

/**
 * Created by unicorn on 12/18/2017.
 */

public interface ProfileInteractor {
    void queryImageSeted(String uid);
    void trueImageSeted(String uid);
    void responseDataUser(boolean verifyGlide, String firstname, String lastname, String email,
                          String scoreAsDomi, String scoreAsUser, int credit);
}

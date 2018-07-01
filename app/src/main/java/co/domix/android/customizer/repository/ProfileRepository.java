package co.domix.android.customizer.repository;

/**
 * Created by unicorn on 12/18/2017.
 */

public interface ProfileRepository {
    void queryImageSeted(String uid, boolean searchImage);
    void trueImageSeted(String uid);
}

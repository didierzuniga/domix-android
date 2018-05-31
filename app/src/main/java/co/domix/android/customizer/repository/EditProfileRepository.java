package co.domix.android.customizer.repository;

/**
 * Created by unicorn on 5/30/2018.
 */

public interface EditProfileRepository {
    void changePersonalData(String uid, int field, String data);
}

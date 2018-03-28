package co.domix.android.customizer.repository;

/**
 * Created by unicorn on 3/27/2018.
 */

public interface SettingRepository {
    void dialogReauthenticate(String email, String password, int opt);
    void changePassword(String password);
}

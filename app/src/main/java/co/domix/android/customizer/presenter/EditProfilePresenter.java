package co.domix.android.customizer.presenter;

/**
 * Created by unicorn on 5/30/2018.
 */

public interface EditProfilePresenter {
    void changePersonalData(String uid, int field, String data);
    void dataChangeSuccess();
}

package co.domix.android.customizer.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import co.domix.android.customizer.interactor.EditProfileInteractor;
import co.domix.android.customizer.presenter.EditProfilePresenter;

/**
 * Created by unicorn on 5/30/2018.
 */

public class EditProfileRepositoryImpl implements EditProfileRepository {

    private EditProfilePresenter presenter;
    private EditProfileInteractor interactor;

    public EditProfileRepositoryImpl(EditProfilePresenter presenter, EditProfileInteractor interactor) {
        this.presenter = presenter;
        this.interactor = interactor;
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceUser = database.getReference("user");

    @Override
    public void changePersonalData(String uid, int field, String data) {
        if (field == 1){
            referenceUser.child(uid).child("first_name").setValue(data);
        } else if (field == 2){
            referenceUser.child(uid).child("last_name").setValue(data);
        } else if (field == 3){
            referenceUser.child(uid).child("phone").setValue(data);
        } else if (field == 4){
            referenceUser.child(uid).child("dni").setValue(data);
        }
        presenter.dataChangeSuccess();
    }
}

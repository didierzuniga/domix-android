package co.domix.android.customizer.repository;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.domix.android.customizer.interactor.ProfileInteractor;
import co.domix.android.customizer.presenter.ProfilePresenter;
import co.domix.android.model.User;

/**
 * Created by unicorn on 12/18/2017.
 */

public class ProfileRepositoryImpl implements ProfileRepository {

    private ProfilePresenter presenter;
    private ProfileInteractor interactor;

    public ProfileRepositoryImpl(ProfilePresenter presenter, ProfileInteractor interactor) {
        this.presenter = presenter;
        this.interactor = interactor;
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceUser = database.getReference("user");

    @Override
    public void queryImageSeted(String uid) {
        referenceUser.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                boolean verifyGlide = user.isImage_profile();
                interactor.responseDataUser(verifyGlide, user.getFirst_name(), user.getLast_name(),
                                            user.getEmail(), String.format("%.2f", user.getScore_as_deliveryman()),
                                            String.format("%.2f", user.getScore_as_user()), user.getMy_credit());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void trueImageSeted(String uid) {
        referenceUser.child(uid).child("image_profile").setValue(true);
    }
}

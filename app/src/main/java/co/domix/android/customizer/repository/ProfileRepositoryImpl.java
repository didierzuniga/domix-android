package co.domix.android.customizer.repository;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import co.domix.android.customizer.interactor.ProfileInteractor;
import co.domix.android.customizer.presenter.ProfilePresenter;
import co.domix.android.model.Fare;
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
    DatabaseReference referenceFare = database.getReference("fare");

    @Override
    public void queryImageSeted(String uid) {
        final NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        formatter.setRoundingMode(RoundingMode.HALF_UP);

        referenceUser.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                final boolean verifyGlide = user.isImage_profile();
                final Float scoreAsDeliveryman = new Float(formatter.format(user.getScore_as_deliveryman()));
                final Float scoreAsUser = new Float(formatter.format(user.getScore_as_user()));

                referenceFare.child(user.getCountry_code()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Fare fare = dataSnapshot.getValue(Fare.class);

                        interactor.responseDataUser(verifyGlide, user.getFirst_name(), user.getLast_name(),
                                user.getDni(), user.getPhone(), user.getEmail(), scoreAsDeliveryman,
                                scoreAsUser, user.getMy_credit(), fare.getCurrency_code());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


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

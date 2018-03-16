package co.domix.android.home.repository;

import android.app.Activity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.domix.android.home.presenter.HomePresenter;
import co.domix.android.model.User;

/**
 * Created by unicorn on 11/14/2017.
 */

public class HomeRepositoryImpl implements HomeRepository {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceUser = database.getReference("user");
    private HomePresenter presenter;

    public HomeRepositoryImpl(HomePresenter presenter) {
        this.presenter = presenter;
    }

}

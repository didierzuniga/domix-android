package co.domix.android.user.repository;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import co.domix.android.model.Counter;
import co.domix.android.model.Fare;
import co.domix.android.model.Order;
import co.domix.android.model.User;
import co.domix.android.user.interactor.UserInteractor;
import co.domix.android.user.interactor.UserInteractorImpl;
import co.domix.android.user.presenter.UserPresenter;

/**
 * Created by unicorn on 11/12/2017.
 */

public class UserRepositoryImpl implements UserRepository {

    public Double fare;
    private UserPresenter presenter;
    private UserInteractor interactor;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceCounter = database.getReference("counter");
    DatabaseReference referenceOrder = database.getReference("order");
    DatabaseReference referenceUser = database.getReference("user");
    DatabaseReference referenceFare = database.getReference("fare");

    public UserRepositoryImpl(UserPresenter presenter, UserInteractor interactor) {
        this.presenter = presenter;
        this.interactor = interactor;
    }

    @Override
    public void requestForFullnameAndPhone(String uid) {
        referenceUser.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getFirstName() == null ||
                        user.getLastName() == null ||
                        user.getPhone() == null) {
                    presenter.responseForFullnameAndPhone(false);
                } else {
                    presenter.responseForFullnameAndPhone(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void sendContactData(String uid, String firstName, String lastName, String phone, Activity activity) {
        referenceUser.child(uid).child("firstName").setValue(firstName);
        referenceUser.child(uid).child("lastName").setValue(lastName);
        referenceUser.child(uid).child("phone").setValue(phone);
        presenter.contactDataSent();
    }

    @Override
    public void request(String uid, String email, final String country, final String city,
                        final String from, final String to, final String description1, final String description2,
                        final byte dimenSelected, final byte payMethod, int paymentCash, Activity activity) {
        SharedPreferences location = activity
                .getSharedPreferences("domx_prefs", Context.MODE_PRIVATE);
        final String uidCurrentUser = uid;
        final String latFrom = location.getString("latFrom", "");
        final String lonFrom = location.getString("lonFrom", "");
        final String latTo = location.getString("latTo", "");
        final String lonTo = location.getString("lonTo", "");
        final int vvPaymentCash = paymentCash;
        final Double scoreAuthor = null;
        final Double scoreDomiciliary = null;

        Date date = Calendar.getInstance().getTime();
        DateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat formatTime = new SimpleDateFormat("HH:mm");
        final String today = formatDate.format(date);
        final String time = formatTime.format(date);

        referenceFare.child(country).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Fare fa = dataSnapshot.getValue(Fare.class);
                String f = String.format("%.2f", fa.getFarePerMeter());
                fare = Double.valueOf(f);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        referenceCounter.runTransaction(new Transaction.Handler() {
            int couForResponse;
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                final Counter c = mutableData.getValue(Counter.class);
                if (c == null) {
                    return Transaction.success(mutableData);
                }
                if (c.countFull != 0) {
                    c.countFull++;
                    c.counterDone++;
                    c.countRealTime++;
                    couForResponse = c.countFull;
                    String couString = String.valueOf(c.countFull);
                    Order order = new Order(uidCurrentUser, country, city, c.countFull, from, to,
                            latFrom, lonFrom, latTo, lonTo, description1, description2, dimenSelected, payMethod,
                            vvPaymentCash, fare, today, time, (double) new Date().getTime(),
                            scoreAuthor, scoreDomiciliary);
                    referenceOrder.child(couString).setValue(order);
//                    presenter.responseSuccessRequest(c.countFull);
                }
                mutableData.setValue(c);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                presenter.responseSuccessRequest(couForResponse);
            }
        });
    }

    @Override
    public void requestFare(final String code) {
        referenceFare.child(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Fare fare = dataSnapshot.getValue(Fare.class);
                interactor.responseFare(fare.getFarePerMeter());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

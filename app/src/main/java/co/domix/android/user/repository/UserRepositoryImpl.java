package co.domix.android.user.repository;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import co.domix.android.R;
import co.domix.android.api.RetrofitDatetimeAdapter;
import co.domix.android.api.RetrofitDatetimeService;
import co.domix.android.model.Time;
import co.domix.android.model.Counter;
import co.domix.android.model.Fare;
import co.domix.android.model.Order;
import co.domix.android.model.User;
import co.domix.android.user.interactor.UserInteractor;
import co.domix.android.user.presenter.UserPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by unicorn on 11/12/2017.
 */

public class UserRepositoryImpl implements UserRepository {

    public Double fare;
    private boolean countryAvailable = false;
    private Timer timer;
    private int couForResponse, countFinal, vvPaymentCash, credit, updateCreditUser, disbetween;
    private Double scoreAuthor, scoreDomiciliary;
    private byte dimenSelected, payMethod;
    private String couString, uidCurrentUser, country, city, from, to, latFrom, lonFrom, latTo, lonTo, description1,
                    description2, dateNow, timeNow;
    private int stamp;
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
    public void queryPersonalDataFill(String uid) {
        referenceUser.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getFirst_name() == null ||
                        user.getLast_name() == null ||
                        user.getDni() == null ||
                        user.getPhone() == null ||
                        user.isImage_profile() == false){
                    presenter.responseQueryPersonalDataFill(false);
                } else {
                    presenter.responseQueryPersonalDataFill(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void request(String uid, String email, final String countryCode, final String cityCode,
                        final String fromm, final String too, int disBetweenPoints, final String descriptionOne,
                        final String descriptionTwo, final byte dimenSelect, final byte payMeth, int paymentCash,
                        int creditUsed, int updateCredit, final Activity activity) {
        SharedPreferences location = activity
                .getSharedPreferences(activity.getString(R.string.const_sharedpreference_file_name), Context.MODE_PRIVATE);
        uidCurrentUser = uid;
        country = countryCode;
        city = cityCode;
        from = fromm;
        to = too;
        latFrom = location.getString("latFrom", "");
        lonFrom = location.getString("lonFrom", "");
        latTo = location.getString("latTo", "");
        lonTo = location.getString("lonTo", "");
        disbetween = disBetweenPoints;
        description1 = descriptionOne;
        description2 = descriptionTwo;
        dimenSelected = dimenSelect;
        payMethod = payMeth;
        vvPaymentCash = paymentCash;
        credit = creditUsed;
        updateCreditUser = updateCredit;
        scoreAuthor = null;
        scoreDomiciliary = null;

        //DATETIME
        Retrofit retrofit = new RetrofitDatetimeAdapter().getAdapter();
        RetrofitDatetimeService service = retrofit.create(RetrofitDatetimeService.class);
        Call<Time> call;
        call = service.loadTime(country);
        call.enqueue(new Callback<Time>() {
            @Override
            public void onResponse(Call<Time> call, Response<Time> response) {
                dateNow = response.body().getDate();
                timeNow = response.body().getTime();
                stamp = response.body().getTimestamp();
            }

            @Override
            public void onFailure(Call<Time> call, Throwable t) {
                //Error handler
            }
        });
        //DATETIME

        referenceCounter.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                final Counter c = mutableData.getValue(Counter.class);
                if (c == null) {
                    return Transaction.success(mutableData);
                }
                if (c.count_full != 0) {
                    c.count_full++;
                    c.count_realtime++;
                    couForResponse = c.count_full;
                    couString = String.valueOf(c.count_full);
                    countFinal = c.count_full;
                    timer = new Timer();
                    timer.schedule(new sendData(), 1000, 1000);
                }
                mutableData.setValue(c);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // Empty
            }
        });
    }

    public class sendData extends TimerTask{
        @Override
        public void run() {
            if (timeNow != null){
                Order order = new Order(uidCurrentUser, countFinal, country, city, from, to,
                        latFrom + ", " +lonFrom, latTo + ", " + lonTo,
                        disbetween, description1, description2, dimenSelected, payMethod,
                        vvPaymentCash, credit, dateNow, timeNow, stamp);
                if (credit > 0){
                    referenceUser.child(uidCurrentUser).child("my_credit").setValue(updateCreditUser);
                }
                referenceOrder.child(couString).setValue(order);
                timer.cancel();
                presenter.responseSuccessRequest(couForResponse);
            }
        }
    }

    @Override
    public void requestFareAndMyCredit(final String codeCountry, final String uid) {
        referenceFare.child(codeCountry).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Fare fare = dataSnapshot.getValue(Fare.class);

                referenceUser.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        interactor.responseFareAndMyCredit(fare.getCurrency_code(), fare.getFare_per_meter(),
                                                           fare.getMin_fare_cost(), user.getMy_credit());
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
    public void countriesAvailable() {
        final List<String> listCountries = new ArrayList<String>();
        referenceFare.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    listCountries.add(snapshot.getKey());
                }
                interactor.responseForCountriesAvailable(listCountries);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

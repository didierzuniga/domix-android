package co.domix.android.user.repository;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

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
    private Timer timer;
    private int countFinal, vvPaymentCash;
    private Double scoreAuthor, scoreDomiciliary;
    private byte dimenSelected, payMethod;
    private String couString, uidCurrentUser, country, city, from, to, latFrom, lonFrom, latTo, lonTo, description1,
                    description2, dateNow, timeNow;
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
    public void request(String uid, String email, final String countryCode, final String cityCode,
                        final String fromm, final String too, final String descriptionOne, final String descriptionTwo,
                        final byte dimenSelect, final byte payMeth, int paymentCash, final Activity activity) {
        SharedPreferences location = activity
                .getSharedPreferences("domx_prefs", Context.MODE_PRIVATE);
        uidCurrentUser = uid;
        country = countryCode;
        city = cityCode;
        from = fromm;
        to = too;
        latFrom = location.getString("latFrom", "");
        lonFrom = location.getString("lonFrom", "");
        latTo = location.getString("latTo", "");
        lonTo = location.getString("lonTo", "");
        description1 = descriptionOne;
        description2 = descriptionTwo;
        dimenSelected = dimenSelect;
        payMethod = payMeth;
        vvPaymentCash = paymentCash;
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
            }

            @Override
            public void onFailure(Call<Time> call, Throwable t) {
                //Error handler
            }
        });
        //DATETIME

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
                    couString = String.valueOf(c.countFull);
                    countFinal = c.countFull;
                    timer = new Timer();
                    timer.schedule(new sendData(), 1000, 1000);
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

    public class sendData extends TimerTask{
        @Override
        public void run() {
            if (timeNow != null){
                Order order = new Order(uidCurrentUser, country, city, countFinal, from, to,
                        latFrom, lonFrom, latTo, lonTo, description1, description2, dimenSelected, payMethod,
                        vvPaymentCash, fare, dateNow, timeNow, (double) new Date().getTime(),
                        scoreAuthor, scoreDomiciliary);
                referenceOrder.child(couString).setValue(order);
                timer.cancel();
            }
        }

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

package co.domix.android.customizer.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import co.domix.android.BuildConfig;
import co.domix.android.R;

public class EarnEcoin extends AppCompatActivity implements RewardedVideoAdListener, EarnCoinView {

    private Button buttonViewAd;
    private ProgressBar progressBar;
    private RewardedVideoAd rewardedVideoAd;
    private boolean wasVideoViewed;
    private int earnAmount;
    private String currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earn_ecoin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_earn_ecoin);

        MobileAds.initialize(this, BuildConfig.APP_ID_ADMOB);
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBarAd);
        buttonViewAd = (Button) findViewById(R.id.viewAd);
        buttonViewAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                loadRewardedVideoAd();
            }
        });
    }

    private void loadRewardedVideoAd() {
        rewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        wasVideoViewed = false;
        hideProgressBar();
        rewardedVideoAd.show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        //Video Opened
    }

    @Override
    public void onRewardedVideoStarted() {
        //Video initialize
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        wasVideoViewed = true;
        earnAmount = rewardItem.getAmount();
        currency = rewardItem.getType();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        if (wasVideoViewed){
            Toast.makeText(this, "Has ganado " + earnAmount + " " + currency, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No pudiste ganar 100 eCoins", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "LeftApplication", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        hideProgressBar();
        Toast.makeText(this, "Falló carga", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        rewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        rewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        rewardedVideoAd.destroy(this);
        super.onDestroy();
    }
}

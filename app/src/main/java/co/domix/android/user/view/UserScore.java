package co.domix.android.user.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.home.view.Home;
import co.domix.android.user.presenter.UserScorePresenter;
import co.domix.android.user.presenter.UserScorePresenterImpl;

/**
 * Created by unicorn on 11/13/2017.
 */

public class UserScore extends AppCompatActivity implements UserScoreView {

    private UserScorePresenter presenter;
    private ProgressBar progressBar;
    private Button buttonSendScoreUserToDomi;
    private RatingBar ratingBarUserToDomi;
    private DomixApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_score);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.text_qualify_deliveryman));

        presenter = new UserScorePresenterImpl(this);
        app = (DomixApplication) getApplicationContext();

        progressBar = (ProgressBar) findViewById(R.id.progressRateUser);
        ratingBarUserToDomi = (RatingBar) findViewById(R.id.ratingBarUserToDomi);
        buttonSendScoreUserToDomi = (Button) findViewById(R.id.buttonSendScoreUsToDo);
        buttonSendScoreUserToDomi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                Double score = Double.valueOf(ratingBarUserToDomi.getRating());
                sendScore(score);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getResources().getString(R.string.toast_action_not_allowed), Toast.LENGTH_SHORT).show();
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
    public void sendScore(Double score) {
        presenter.sendScore(score, app.idOrder, this);
    }

    @Override
    public void responseBackHomeActivity() {
        hideProgressBar();
        app.idOrder = 0;
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }
}

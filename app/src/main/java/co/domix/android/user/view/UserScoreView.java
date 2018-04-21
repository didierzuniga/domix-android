package co.domix.android.user.view;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface UserScoreView {
    void showProgressBar();
    void hideProgressBar();
    void sendScore(Double score);
    void responseBackHomeActivity();
}

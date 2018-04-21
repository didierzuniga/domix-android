package co.domix.android.domiciliary.view;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface DomiciliaryScoreView {
    void showProgressBar();
    void hideProgressBar();
    void sendScore(Double score);
    void responseBackDomiciliaryActivity();
}

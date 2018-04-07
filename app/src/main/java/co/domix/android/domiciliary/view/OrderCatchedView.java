package co.domix.android.domiciliary.view;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface OrderCatchedView {
    void showProgressBar();
    void hideProgressBar();
    void getUserRequest();
    void responseUserRequested(String nameAuthor, String cellphoneAuthor, String countryAuthor, String cityAuthor,
                             String fromAuthor, String toAuthor, String titleAuthor,
                             String descriptionAuthor, String oriLa, String oriLo, String desLa,
                             String desLo, int moneyAuthor);
    void goPreviewRouteOrder();
    void dialContactPhone(String phoneNumber);
    void dialogCancel();
    void showToastDeliverymanCancelledOrder();
    void showToastUserCancelledOrder();
    void responseBackDomiciliaryActivity();
    void goRateDomiciliary();
}

package co.domix.android.domiciliary.view;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface OrderCatchedView {
    void showProgressBar();
    void hideProgressBar();
    void getUserRequest();
    void responseUserRequested(String nameAuthor, String cellphoneAuthor, String countryAuthor, String cityAuthor,
                             String fromAuthor, String toAuthor, String description1,
                             String description2, String oriLa, String oriLo, String desLa,
                             String desLo, int totalCostDelivery, boolean cashReceivesDeliveryman, int moneyCash);
    void goPreviewRouteOrder();
    void dialContactPhone(String phoneNumber);
    void dialogCancel();
    void showToastDeliverymanCancelledOrder();
    void showToastUserCancelledOrder();
    void responseBackDomiciliaryActivity();
    void goRateDomiciliary();
}

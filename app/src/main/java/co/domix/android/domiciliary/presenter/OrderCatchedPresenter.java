package co.domix.android.domiciliary.presenter;

import co.domix.android.domiciliary.view.OrderCatched;

/**
 * Created by unicorn on 11/13/2017.
 */

public interface OrderCatchedPresenter {
    void getUserRequest(int idOrder, String uid, OrderCatched orderCatched);
    void responseUserRequested(String nameAuthor, String cellphoneAuthor, String countryAuthor, String cityAuthor,
                               String fromAuthor, String toAuthor, String titleAuthor, String descriptionAuthor,
                               String origenCoordinate, String destineCoordinate, int totalCostDelivery,
                               boolean cashReceivesDeliveryman, int moneyCash);
    void dialogCancel(String idOrder, String uid, OrderCatched orderCatched);
    void dialogFinish(String idOrder);
    void showToastDeliverymanCancelledOrder();
    void showToastUserCancelledOrder();
    void responseBackDomiciliaryActivity();
    void goRateDomiciliary();
    void submitCoord(String uid, String la, String lo, OrderCatched orderCatched);
}

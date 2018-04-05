package co.domix.android.domiciliary.presenter;

import co.domix.android.domiciliary.interactor.OrderCatchedInteractor;
import co.domix.android.domiciliary.interactor.OrderCatchedInteractorImpl;
import co.domix.android.domiciliary.view.OrderCatched;
import co.domix.android.domiciliary.view.OrderCatchedView;

/**
 * Created by unicorn on 11/13/2017.
 */

public class OrderCatchedPresenterImpl implements OrderCatchedPresenter {

    private OrderCatchedView view;
    private OrderCatchedInteractor interactor;


    public OrderCatchedPresenterImpl(OrderCatchedView view) {
        this.view = view;
        interactor = new OrderCatchedInteractorImpl(this);
    }

    @Override
    public void getUserRequest(int idOrder, String uid, OrderCatched orderCatched) {
        interactor.getUserRequest(idOrder, uid, orderCatched);
    }

    @Override
    public void responseUserRequested(String nameAuthor, String cellphoneAuthor, String countryAuthor, String cityAuthor,
                                    String fromAuthor, String toAuthor, String titleAuthor,
                                    String descriptionAuthor, String oriLa, String oriLo,
                                    String desLa, String desLo, int moneyAuthor) {
        view.responseUserRequested(nameAuthor, cellphoneAuthor, countryAuthor, cityAuthor, fromAuthor,
                toAuthor, titleAuthor, descriptionAuthor, oriLa, oriLo, desLa, desLo, moneyAuthor);
    }

    @Override
    public void dialogCancel(String idOrder, String uid, OrderCatched orderCatched) {
        interactor.dialogCancel(idOrder, uid, orderCatched);
    }

    @Override
    public void dialogFinish(String idOrder, String uidDomicili, OrderCatched orderCatched) {
        interactor.dialogFinish(idOrder, uidDomicili, orderCatched);
    }

    @Override
    public void showToastDeliverymanCancelledOrder() {
        view.showToastDeliverymanCancelledOrder();
    }

    @Override
    public void showToastUserCancelledOrder() {
        view.showToastUserCancelledOrder();
    }

    @Override
    public void responseBackDomiciliaryActivity() {
        view.responseBackDomiciliaryActivity();
    }

    @Override
    public void goRateDomiciliary() {
        view.goRateDomiciliary();
    }

    @Override
    public void submitCoord(String uid, String la, String lo, OrderCatched orderCatched) {
        interactor.submitCoord(uid, la, lo, orderCatched);
    }
}

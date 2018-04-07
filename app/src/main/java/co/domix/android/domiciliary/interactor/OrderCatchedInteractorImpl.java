package co.domix.android.domiciliary.interactor;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import co.domix.android.R;
import co.domix.android.domiciliary.presenter.OrderCatchedPresenter;
import co.domix.android.domiciliary.repository.OrderCatchedRepository;
import co.domix.android.domiciliary.repository.OrderCatchedRepositoryImpl;

/**
 * Created by unicorn on 11/13/2017.
 */

public class OrderCatchedInteractorImpl implements OrderCatchedInteractor {

    private OrderCatchedPresenter presenter;
    private OrderCatchedRepository repository;

    public OrderCatchedInteractorImpl(OrderCatchedPresenter presenter) {
        this.presenter = presenter;
        repository = new OrderCatchedRepositoryImpl(presenter);
    }

    @Override
    public void getUserRequest(int idOrder, String uid, Activity activity) {
        repository.getUserRequest(idOrder, uid, activity);
    }

    @Override
    public void dialogCancel(final String idOrder, final String uid, final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.message_cancel_request);
        builder.setPositiveButton(R.string.message_yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        repository.dialogCancel(idOrder, uid, activity);
                    }
                }
        )
                .setNegativeButton(R.string.message_no, null);
        builder.create().show();
    }

    @Override
    public void dialogFinish(String idOrder) {
        repository.dialogFinish(idOrder);
    }

    @Override
    public void submitCoord(String uid, String la, String lo, Activity activity) {
        repository.submitCoord(uid, la, lo, activity);
    }
}

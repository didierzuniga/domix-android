package co.domix.android.customizer.repository;

import java.util.List;

/**
 * Created by unicorn on 1/14/2018.
 */

public interface TotalToPayRepository {
    void queryOrderToPay(String uid);
    void goPayU(List<String> list, int balance);
}

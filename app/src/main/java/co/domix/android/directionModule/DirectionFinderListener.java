package co.domix.android.directionModule;

import java.util.List;

/**
 * Created by unicorn on 11/12/2017.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}

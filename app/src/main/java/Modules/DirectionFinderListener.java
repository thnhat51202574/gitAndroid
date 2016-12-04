package Modules;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by 51202_000 on 12/11/2016.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route) throws UnsupportedEncodingException;
}

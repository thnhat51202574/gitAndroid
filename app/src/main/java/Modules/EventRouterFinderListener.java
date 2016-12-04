package Modules;

import java.util.ArrayList;

/**
 * Created by 51202_000 on 04/12/2016.
 */

public interface EventRouterFinderListener {
    void onEventRouterFinderStart();
    void onEventRouterFinderFinish(ArrayList<EventRoute> arEventRoutes);
}

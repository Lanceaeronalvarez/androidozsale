package au.com.dealsdirect.utils.module;


import android.os.Bundle;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by smartwave on 16/10/2017.
 */

public class GateKeeper {

    private static Map<Router, Destination> sCURRENT_LOCATION = new HashMap<>();

    public enum Destination {
        EMPTY,
        FACET_FILTER,
        SPLASH,
        LOGIN,
        DETAILS,
        MAIN,
        REGISTER,
        SALEITEMS,
        SALEITEM_DETAILS,
        CHECKOUT,
        CATEGORIES,
        ACCOUNT,
        FORGOT_PASSWORD,
        VIEW_VOUCHERS,
        ADD_VOUCHERS,
        INVITE,
        PAYMENT_SELECT,
        PAYMENT_ADD,
        ORDERS,
        ORDER_DETAILS,
        CURRENT_RETURNS,
        VIEW_ADDRESSES,
        CONTACT_US,
        SEARCH_FILTER,
        LEGALITIES
        //add more destinations
    }

    private static HashMap<Destination, List<Destination>> mRouteMap = new HashMap<>();

    /**
     * Registers the list of routes as valid to the specified destination endpoint.
     *
     * @param destination destination route end point.
     * @param routesFrom  a list of routes to be associated with specified destination to be registered as valid
     *                    routes.
     */
    public static void registerRoute(Destination destination, List<Destination> routesFrom) {
        mRouteMap.put(destination, routesFrom);
    }

    /**
     * Builder class for providing list of valid origins for a destination.
     */
    public static class RouteBuilder {
        private List<Destination> mRouteList = new ArrayList<>();

        public RouteBuilder addRouteFrom(Destination destination) {
            mRouteList.add(destination);
            return this;
        }

        public List<Destination> build() {
            return mRouteList;
        }
    }


    /**
     * Validates if current location is specified under list possible origins for specified destination
     *
     * @param destination Destination enum controller
     * @return true if origin is registered as a valid route to destination, false otherwise
     */
    private static boolean validateRouteOrigin(Router router, Destination destination) {
        List<Destination> validOrigins = mRouteMap.get(destination);

        if (validOrigins == null || !validOrigins.contains(sCURRENT_LOCATION.get(router))) {
            //throw RouteNotValidException orrr?
            return false;
        }

        return true;
    }

    /**
     * Router push operation given specified destination. Uses given pop/push changehandler.
     * sets CURRENT_LOCATION to specified destination.
     *
     * @param router            Conductor router
     * @param destination       Destination enum controller
     * @param pushChangeHandler your custom pushChangeHandler
     * @param popChangeHandler  your custom popChangehandler
     */
    public static void push(Router router, Destination destination, ControllerChangeHandler pushChangeHandler, ControllerChangeHandler popChangeHandler) {
//        if(validateRouteOrigin(destination)){
        router.pushController(RouterTransaction.with(ControllerFactory.getInstance(destination)).pushChangeHandler(pushChangeHandler).popChangeHandler(popChangeHandler));
        sCURRENT_LOCATION.put(router, destination);
//        } else {
//            Controller currentController = getCurrentControllerOnRouter(router);
//            if(currentController != null) {
////                ((BaseController) currentController).showInvalidRoute();
//            }
//        }
    }

    /**
     * Router push operation given specified destination. Uses given pop/push changehandler.
     * sets CURRENT_LOCATION to specified destination.
     *
     * @param router            Conductor router
     * @param destination       Destination enum controller
     * @param pushChangeHandler your custom pushChangeHandler
     * @param popChangeHandler  your custom popChangehandler
     */
    public static void push(Router router, Destination destination, Bundle bundle, ControllerChangeHandler pushChangeHandler, ControllerChangeHandler popChangeHandler) {
//        if(validateRouteOrigin(destination)){
        router.pushController(RouterTransaction.with(ControllerFactory.getInstance(destination, bundle)).pushChangeHandler(pushChangeHandler).popChangeHandler(popChangeHandler));
        sCURRENT_LOCATION.put(router, destination);
//        } else {
//            Controller currentController = getCurrentControllerOnRouter(router);
//            if(currentController != null) {
////                ((BaseController) currentController).showInvalidRoute();
//            }
//        }
    }

    /**
     * Gets top of the backstack controller given specified router
     *
     * @param router Conductor router
     * @return topmost controller
     */
    public static Controller getCurrentControllerOnRouter(Router router) {
        int topIndex = router.getBackstackSize() - 1;
        if (topIndex >= 0) {
            return router.getBackstack().get(topIndex).controller();
        }

        return null;
    }

    /**
     * Router push operation given specified destination. Uses default pop/push changehandler(null)
     *
     * @param router      conductor router
     * @param destination destination enum
     */
    public static void push(Router router, Destination destination) {
        push(router, destination, null, null);
    }

    /**
     * Router push operation given specified destination. Uses default pop/push changehandler(null)
     *
     * @param router      conductor router
     * @param destination destination enum
     */
    public static void push(Router router, Destination destination, Bundle bundle) {
        push(router, destination, bundle, null, null);
    }

    /**
     * sets root controller of the specified router
     *
     * @param router            conductor router
     * @param destination       destination enum
     * @param routerTransaction your custom router transaction
     */
    public static void setRoot(Router router, Destination destination, RouterTransaction routerTransaction) {
        router.setRoot(routerTransaction);
        sCURRENT_LOCATION.put(router, destination);
    }

    public static Destination getCurrentLocation(Router router) {
        return sCURRENT_LOCATION.get(router);
    }

    public static void updateCurrentLocation(Router router) {
        Destination destination = ControllerFactory.mapControllerToDestination(getCurrentControllerOnRouter(router));
        sCURRENT_LOCATION.put(router, destination);
    }

}

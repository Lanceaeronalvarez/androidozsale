package au.com.dealsdirect.utils.module;

import android.os.Bundle;

import com.bluelinelabs.conductor.Controller;

import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.details.DetailsController;
import au.com.dealsdirect.ui.controller.login.LoginController;
import au.com.dealsdirect.ui.controller.main.MainController;

/**
 * Created by smartwave on 16/10/2017.
 */

//register here controllers and how to create instances of them
public class ControllerFactory {
    public static Controller getInstance(GateKeeper.Destination destination) {
        return getInstance(destination, new Bundle());
    }

    public static Controller getInstance(GateKeeper.Destination destination, Bundle bundle) {
        return determineConstructionType(destination, bundle);
    }

    private static Controller determineConstructionType(GateKeeper.Destination destination, Bundle bundle) {
        return bundle == null || bundle.isEmpty() ? simpleConstruction(destination) : buildWithParameters(destination,bundle);
    }

    private static Controller simpleConstruction(GateKeeper.Destination destination) {
        switch (destination) {
            case MAIN:
                return MainController.newInstance();
            case DETAILS:
                return DetailsController.newInstance();
            case LOGIN:
                return LoginController.newInstance();
            case CATEGORIES:
                return CategoriesController.newInstance();
            default:
                return null;
        }
    }

    private static Controller buildWithParameters(GateKeeper.Destination destination, Bundle bundle){
        switch (destination) {
            case MAIN:
                return new MainController(bundle);
            case DETAILS:
                return new DetailsController(bundle);
            case LOGIN:
                return new LoginController(bundle);
            case CATEGORIES:
                return new CategoriesController(bundle);
            default:
                return null;
        }
    }
}

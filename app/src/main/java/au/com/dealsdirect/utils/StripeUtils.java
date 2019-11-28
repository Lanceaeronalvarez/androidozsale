package au.com.dealsdirect.utils;

import android.content.Context;

import com.stripe.android.Stripe;

/**
 * Created by MTC on 2019-10-10.
 */
public class StripeUtils {

    public static Stripe initializeStripe(Context context, String publicKey) {
        return new Stripe(context, publicKey);
    }


}

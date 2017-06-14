
package au.com.dealsdirect.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.androidnetworking.common.ANConstants;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.androidnetworking.utils.Utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public final class NetworkUtils {

    private static int CACHE_EXPIRATION = 21; //hours

    private NetworkUtils() {
        // This utility class is not publicly instantiable
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static OkHttpClient provideOkHttpClientResponseCaching(Context ctx){
        return provideOkHttpClientResponseCaching(ctx,null);
    }

    public static OkHttpClient provideOkHttpClientResponseCaching(Context ctx, HttpLoggingInterceptor.Level level){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(Utils.getCache(ctx, ANConstants.MAX_CACHE_SIZE, ANConstants.CACHE_DIR_NAME))
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

        if(level != null) {
            builder.addInterceptor(new HttpLoggingInterceptor().setLevel(level));
        }

        return builder.addInterceptor(provideOfflineCacheInterceptor(ctx, CACHE_EXPIRATION, TimeUnit.HOURS))
            .addNetworkInterceptor(provideCacheInterceptor())
            .build();
    }

    private static Interceptor provideOfflineCacheInterceptor(final Context ctx, final int maxStale, final TimeUnit timeUnit) {
        return new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {

                Request request = chain.request();
                if (!NetworkUtils.isNetworkConnected(ctx)) {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(maxStale, timeUnit)
                            .build();

                    request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .build();
                }

                return chain.proceed(request);
            }
        };
    }

    private static Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                okhttp3.Response response = chain.proceed(request);

                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(60, TimeUnit.SECONDS)
                        .build();

                return response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", cacheControl.toString())
                        .build();
            }
        };
    }
}

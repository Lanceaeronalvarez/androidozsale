
package au.com.dealsdirect.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.androidnetworking.common.ANConstants;
import com.androidnetworking.interceptors.GzipRequestInterceptor;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.androidnetworking.utils.Utils;
import com.mysale.genie.utility.Prefs;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public final class NetworkUtils {
    public static final String NET_WIFI_STATE_CHANGE = "android.net.wifi.STATE_CHANGE";
    public static final String NET_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

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

    public static OkHttpClient provideOkHttpClientResponseCaching(Context ctx) {
        return provideOkHttpClientResponseCaching(ctx, null);
    }

    public static OkHttpClient provideOkHttpClientResponseCaching(Context ctx, HttpLoggingInterceptor.Level level) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(Utils.getCache(ctx, ANConstants.MAX_CACHE_SIZE, ANConstants.CACHE_DIR_NAME))
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .cookieJar(CookieUtils.getInstance());

        if (level != null) {
            builder.addInterceptor(new HttpLoggingInterceptor().setLevel(level));
        }

        return builder.addInterceptor(provideOfflineCacheInterceptor(ctx, CACHE_EXPIRATION, TimeUnit.HOURS))
                .addNetworkInterceptor(provideCacheInterceptor())
                .addInterceptor(provideReceivedCookiesInterceptor())
                .build();
    }

    public static OkHttpClient provideDebugOkHttpClientResponseCaching(Context ctx, HttpLoggingInterceptor.Level level) {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .cache(Utils.getCache(ctx, ANConstants.MAX_CACHE_SIZE, ANConstants.CACHE_DIR_NAME))
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .cookieJar(CookieUtils.getInstance());

            if (level != null) {
                builder.addInterceptor(new HttpLoggingInterceptor().setLevel(level));
            }

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Interceptor provideOfflineCacheInterceptor(final Context ctx, final int maxStale, final TimeUnit timeUnit) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

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

    public static HashSet<String> getCookies() {
        return (HashSet<String>) Prefs.getStringSet("Cookies", new HashSet<>());
    }

    private static Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);

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

    private static Interceptor provideReceivedCookiesInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());

                if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                    HashSet<String> cookies = new HashSet<>();

                    for (String header : originalResponse.headers("Set-Cookie")) {
                        cookies.add(header);
                    }

                    Prefs.putStringSet("network_cookies", cookies);
                }

                return originalResponse;
            }
        };
    }

    public static Map<String, String> getQueryParams(String url) {
        try {
            Map<String, String> params = new HashMap<>();
            String[] urlParts = url.split("\\?");
            if (urlParts.length > 1) {
                String query = urlParts[1];
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    String key = URLDecoder.decode(pair[0], "UTF-8");
                    String value = "";
                    if (pair.length > 1) {
                        value = URLDecoder.decode(pair[1], "UTF-8");
                    }

                    params.put(key, value);
                }
            }

            return params;
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        }
    }
}

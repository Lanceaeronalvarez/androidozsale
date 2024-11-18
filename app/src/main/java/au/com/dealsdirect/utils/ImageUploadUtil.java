package au.com.dealsdirect.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import au.com.dealsdirect.BuildConfig;
import au.com.dealsdirect.data.network.ApiEndPoint;
import au.com.dealsdirect.service.fcm.GNotification;
import okhttp3.Cookie;

/**
 * Created by MTC on 2019-08-23.
 */
public class ImageUploadUtil {

    public static final int MAX_SIZE_IN_BYTES = 1000000;
    private static final int MAX_ITERATIONS = 5;
    private static final double PERCENT_IMAGE_RESIZE = 0.85;
    public static final int JPEG_QUALITY_FACTOR_FOR_UPLOAD = 100;

    private static int getMaxIterations() {
        return MAX_ITERATIONS;
    }

    public static String convertStringUrltoJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int convertImageLimitToBytes(int sizeInMb) {
        return (sizeInMb * 1000000);
    }

    private static void checkSSL() {
        if (BuildConfig.IS_TEST || BuildConfig.DEBUG) {
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                                return myTrustedAnchors;
                            }

                            @Override
                            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            }
                        }
                };

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    public static String uploadImage(String attachmentID, File file, String userAgent, String fileName) {

        String responseString = null;
        String uploadUrl = ApiEndPoint.uploadImage() + attachmentID;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        try {

            checkSSL();

            // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(file);
            URL url = new URL(uploadUrl);

            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);
            conn.setRequestProperty("cookie", getUserCookie());
            conn.setRequestProperty("User-Agent", userAgent);


            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"" +
                    "File" + "\";filename=\"" +
                    fileName + "\"" + lineEnd);


            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necessary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            //Parse response from api
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();

            responseString = sb.toString();
            //close streams //
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {
            AppLogger.d("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            AppLogger.d("Upload file exception", "Exception : "
                    + e.getMessage(), e);
        }

        return responseString;
    }

    public static String getUserCookie() {
        StringBuilder output = new StringBuilder();
        HashMap<String, String> cookieHash = new HashMap<>();

        for (Cookie cookie : CookieUtils.getInstance().getCookieSet()) {

            if (!cookieHash.containsKey(cookie.name())) {
                cookieHash.put(cookie.name(), cookie.toString());
                output.append(cookie);
            }
        }

        String cookieValue = output.toString();
        String removePath = cookieValue.replace("path=/", "");
        return removePath.replace("; httponly", "");
    }


    public static class UploadFileToServer extends AsyncTask<Object, Integer, String> {

        public AsyncResponse delegate = null;
        int imageCount = 0;
        private ProgressDialog progressDialog;
        private final Context mContext;
        private boolean showProgressDialog = true;

        public UploadFileToServer(final Context context, boolean showLoading) {
            mContext = context;
            showProgressDialog = showLoading;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (showProgressDialog) {
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected String doInBackground(Object... params) {

            String attachmentId = (String) params[0];
            File file = (File) params[1];
            String userAgent = (String) params[2];
            imageCount = (int) params[3];
            String fileName = (String) params[4];

            return ImageUploadUtil.uploadImage(attachmentId, file,
                    userAgent, fileName);

        }

        @Override
        protected void onPostExecute(String result) {
            // view response from server
            AppLogger.d("Response from server: " + result);
            if (showProgressDialog) {
                progressDialog.dismiss();
            }
            delegate.asyncExecutionFinished(result, imageCount);
            super.onPostExecute(result);
        }
    }

    public static Bitmap imageProcessIteration(Bitmap image, double fileSize) {

        Bitmap newBitmap = image;
        double sizeInBytes = fileSize;

        for (int i = 0; i < getMaxIterations() && getFileSizeInMb(sizeInBytes) >= 1; i++) {
            newBitmap = changeImageSize(newBitmap, PERCENT_IMAGE_RESIZE);
            sizeInBytes = getFileSize(newBitmap);
        }

        if (getFileSizeInMb(sizeInBytes) > 1) {
            return null;
        }

        return newBitmap;
    }

    public static Bitmap imageResizeConversion(Bitmap image, int maxSize, double fileSize) throws OutOfMemoryError {
        double percent = maxSize / fileSize;
        Bitmap newImage = null;
        if (percent < 1) {
            newImage = changeImageSize(image, percent);
            double sizeInBytes = getFileSize(newImage);
            if (getFileSizeInMb(sizeInBytes) >= 1) {
                return imageProcessIteration(newImage, sizeInBytes);
            } else {
                return newImage;
            }
        } else {
            return image;
        }
    }

    private static Bitmap changeImageSize(Bitmap image, double percent) throws OutOfMemoryError {
        double sqrtPercent = Math.sqrt(percent);
        int width = (int) (image.getWidth() * sqrtPercent);
        int height = (int) (image.getHeight() * sqrtPercent);
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static double getFileSize(Bitmap bitmap) {
        double fileSizeInBytes = 0;

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageInByte = stream.toByteArray();
            fileSizeInBytes = imageInByte.length;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return fileSizeInBytes;
    }

    public static double getFileSizeInMb(double bytesSize) {
        double fileSizeInKB = (bytesSize / 1024);
        return (fileSizeInKB / 1024);
    }

    public static Bitmap resizeBitmapToFitSize(Bitmap originalImage, int maxWidth, int maxHeight) {

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        if (width < maxWidth && height < maxHeight) {
            return originalImage;
        } else if (width > height) {
            // landscape
            int ratio = width / maxWidth;
            width = maxWidth;
            height = height / ratio;
        } else if (height > width) {
            // portrait
            int ratio = height / maxHeight;
            height = maxHeight;
            width = width / ratio;
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }

        originalImage = Bitmap.createScaledBitmap(originalImage, width, height, true);
        return originalImage;
    }


    public static File getFileForUpload(Activity activity, int position, Bitmap bitmap) {

        try {
            File file = new File(activity.getCacheDir(), GNotification.getDeviceID(activity) + position + ".jpg");

            file.createNewFile();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY_FACTOR_FOR_UPLOAD, byteArrayOutputStream);
            byte[] bitmapdata = byteArrayOutputStream.toByteArray();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}

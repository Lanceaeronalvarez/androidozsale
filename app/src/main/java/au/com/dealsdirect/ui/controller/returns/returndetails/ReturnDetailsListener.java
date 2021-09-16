package au.com.dealsdirect.ui.controller.returns.returndetails;

import android.graphics.Bitmap;

/**
 * Created by MTC on 2019-08-20.
 */
public interface ReturnDetailsListener {

    void getImageFromDirectory(boolean uploadImage);

    void removeImage(Bitmap image, int position, boolean uploadImage, boolean isAddImageAdapter);

    void addItemFromLink(String url,int position, Bitmap bitmap);

}

package au.com.dealsdirect.utils.GlideImage;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

/**
 * Created by MTC on 2019-11-29.
 */
public class GlideSetup implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) { /* no customization */ }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.replace(String.class, InputStream.class, new HeaderLoader.Factory());
    }
}
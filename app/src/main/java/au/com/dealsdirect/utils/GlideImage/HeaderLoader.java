package au.com.dealsdirect.utils.GlideImage;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

import java.io.InputStream;

/**
 * Created by MTC on 2019-11-29.
 */
public class HeaderLoader extends BaseGlideUrlLoader<String> {
    public static class Factory implements ModelLoaderFactory<String, InputStream> {

        @Override
        public ModelLoader<String, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new HeaderLoader(multiFactory.build(GlideUrl.class, InputStream.class));
        }

        @Override
        public void teardown() {
        }

    }

    public HeaderLoader(ModelLoader<GlideUrl, InputStream> urlLoader) {
        super(urlLoader);
    }

    @Override
    public boolean handles(String model) {
        return true;
    }


    @Override
    protected String getUrl(String model, int width, int height, Options options) {
        return model;
    }

    @Nullable
    @Override
    protected Headers getHeaders(String s, int width, int height, Options options) {
        LazyHeaders.Builder headersBuilder = new LazyHeaders.Builder();
        headersBuilder.addHeader("Accept", "image/webp");
        headersBuilder.addHeader("Accept-Encoding", "gzip");
        return headersBuilder.build();
    }
}
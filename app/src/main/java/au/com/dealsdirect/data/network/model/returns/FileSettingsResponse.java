package au.com.dealsdirect.data.network.model.returns;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by MTC on 2019-09-17.
 */
public class FileSettingsResponse {

    @SerializedName("file_size_limit")
    @Expose
    private int fileSizeLimit;

    public int getFileSizeLimit() {
        return fileSizeLimit;
    }

    public void setFileSizeLimit(int fileSizeLimit) {
        this.fileSizeLimit = fileSizeLimit;
    }
}


package au.com.dealsdirect.data.network.model.banner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetTopBrandsResponse implements Comparable<GetTopBrandsResponse> {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("grade")
    @Expose
    private int grade;

    @SerializedName("attributes")
    @Expose
    private Attributes attributes;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public int getGrade() {
        return grade;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public int compareTo(GetTopBrandsResponse getTopBrandsResponse) {
        return getName().compareToIgnoreCase(getTopBrandsResponse.getName());
    }

    private static class Attributes {
        @SerializedName("isStoreAvailable")
        @Expose
        private boolean isStoreAvailable;

        @SerializedName("categories")
        @Expose
        private List<String> categories;

        public boolean isStoreAvailable() {
            return isStoreAvailable;
        }

        public List<String> getCategories() {
            return categories;
        }
    }
}

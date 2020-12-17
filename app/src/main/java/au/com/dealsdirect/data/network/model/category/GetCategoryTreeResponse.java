package au.com.dealsdirect.data.network.model.category;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * dp Created by Admin on 1/3/17.
 */
public class GetCategoryTreeResponse {

    @SerializedName("payload")
    @Expose
    private String payload;
    @SerializedName("nodeType")
    @Expose
    private String nodeType;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("isSelected")
    @Expose
    private boolean isSelected;
    @SerializedName("children")
    @Expose
    private List<GetCategoryTreeResponse> children;
    @SerializedName("linkOptions")
    @Expose
    private LinkOptions linkOptions;
    @SerializedName("brands")
    @Expose
    private List<Brand> brands;

    public GetCategoryTreeResponse() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<GetCategoryTreeResponse> getChildren() {
        return children;
    }

    public void setChildren(List<GetCategoryTreeResponse> children) {
        this.children = children;
    }

    public LinkOptions getLinkOptions() {
        return linkOptions;
    }

    public void setLinkOptions(LinkOptions linkOptions) {
        this.linkOptions = linkOptions;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void traverseTree(TreeTraversalBlock block, Object option) {
        if (block.execute(this, option)) {
            for (GetCategoryTreeResponse child : children) {
                child.traverseTree(block, block.transformOption(this, option));
            }
        }
    }

    public interface TreeTraversalBlock {
        /**
         * @param parent
         * @return True to continue traversal.
         */
        boolean execute(GetCategoryTreeResponse parent, Object option);

        Object transformOption(GetCategoryTreeResponse parent, Object option);
    }

    public static class LinkOptions {
        @SerializedName("category")
        public Category category;
        @SerializedName("facets")
        public Facets facets;

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public Facets getFacets() {
            return facets;
        }

        public void setFacets(Facets facets) {
            this.facets = facets;
        }

        public class Category {
            @SerializedName("id")
            public String id;
            @SerializedName("name")
            public String name;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public class Facets {
            @SerializedName("newArrivals")
            public List<String> newArrivals;

            public List<String> getNewArrivals() {
                return newArrivals;
            }

            public void setNewArrivals(List<String> newArrivals) {
                this.newArrivals = newArrivals;
            }
        }

    }

    public static class Brand {
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("value")
        @Expose
        private Integer value;

        public String getName() {
            return name;
        }

        public Integer getValue() {
            return value;
        }
    }
}

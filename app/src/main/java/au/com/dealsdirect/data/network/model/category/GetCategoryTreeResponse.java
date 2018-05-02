package au.com.dealsdirect.data.network.model.category;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * dp Created by Admin on 1/3/17.
 */
public class GetCategoryTreeResponse implements Parcelable {

    String payload;
    String nodeType;
    String id;
    String name;
    String key;
    int count;
    boolean isSelected;
    List<GetCategoryTreeResponse> children;
    String parentKey;

    public GetCategoryTreeResponse() {

    }

    protected GetCategoryTreeResponse(Parcel in) {
        payload = in.readString();
        nodeType = in.readString();
        id = in.readString();
        name = in.readString();
        key = in.readString();
        count = in.readInt();
        isSelected = in.readByte() != 0;
        children = in.createTypedArrayList(GetCategoryTreeResponse.CREATOR);
        parentKey = in.readString();
    }

    public static final Creator<GetCategoryTreeResponse> CREATOR = new Creator<GetCategoryTreeResponse>() {
        @Override
        public GetCategoryTreeResponse createFromParcel(Parcel in) {
            return new GetCategoryTreeResponse(in);
        }

        @Override
        public GetCategoryTreeResponse[] newArray(int size) {
            return new GetCategoryTreeResponse[size];
        }
    };

    public String getId() {
        return id;
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

    public void setId(String id) {
        this.id = id;
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

    public void setChildren(ArrayList<GetCategoryTreeResponse> children) {
        this.children = children;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(payload);
        parcel.writeString(nodeType);
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(key);
        parcel.writeInt(count);
        parcel.writeByte((byte) (isSelected ? 1 : 0));
        parcel.writeTypedList(children);
        parcel.writeString(parentKey);
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

    public static Creator<GetCategoryTreeResponse> getCREATOR() {
        return CREATOR;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean getSelected() {
        return isSelected;
    }
}

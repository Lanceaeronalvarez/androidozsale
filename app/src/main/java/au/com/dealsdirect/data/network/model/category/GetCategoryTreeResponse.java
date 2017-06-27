package au.com.dealsdirect.data.network.model.category;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * dp Created by Admin on 1/3/17.
 */
public class GetCategoryTreeResponse implements Parcelable{

    String id;
    String name;
    String key;
    int count;
    ArrayList<GetCategoryTreeResponse> children;

    public GetCategoryTreeResponse(){

    }

    protected GetCategoryTreeResponse(Parcel in) {
        id = in.readString();
        name = in.readString();
        key = in.readString();
        count = in.readInt();
        children = in.createTypedArrayList(GetCategoryTreeResponse.CREATOR);
    }

    public static final Creator<GetCategoryTreeResponse> CREATOR = new Creator<GetCategoryTreeResponse>() {
        @Override public GetCategoryTreeResponse createFromParcel(Parcel in) {
            return new GetCategoryTreeResponse(in);
        }

        @Override public GetCategoryTreeResponse[] newArray(int size) {
            return new GetCategoryTreeResponse[size];
        }
    };

    public String getId() { return id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setId(String id) { this.id = id; }
    public void setKey(String key) {
        this.key = key;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<GetCategoryTreeResponse> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<GetCategoryTreeResponse> children) {
        this.children = children;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(key);
        parcel.writeInt(count);
        parcel.writeTypedList(children);
    }
}

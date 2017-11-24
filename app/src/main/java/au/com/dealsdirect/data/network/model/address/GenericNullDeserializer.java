package au.com.dealsdirect.data.network.model.address;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by smartwave on 12/01/2017.
 */

public class GenericNullDeserializer<T> implements JsonDeserializer<T> {
    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        T pojo = new Gson().fromJson(json, typeOfT);

        if(pojo instanceof DecorationInfoList){
            DecorationInfoList decorationInfoList = (DecorationInfoList) pojo;
            JsonObject jsonObject = json.getAsJsonObject();

            JsonElement regexp = jsonObject.get("Regexp");
            if(regexp.isJsonNull()){
                decorationInfoList.setRegexp("");
            }
            JsonElement options = jsonObject.get("Options");
            if(options.isJsonNull()){
                decorationInfoList.setOptions(new ArrayList<>());
            }
            JsonElement minLength = jsonObject.get("MinLength");
            if(minLength.isJsonNull()){
                decorationInfoList.setMinLength(0);
            }

            JsonElement value = jsonObject.get("ScheduledPlan");
            if(value.isJsonNull()){
                decorationInfoList.setValue("");
            }
            JsonElement dataType = jsonObject.get("DataType");
            if(dataType.isJsonNull()){
                decorationInfoList.setDataType("");
            }
        }

        if(pojo instanceof ApplyAddressResponse.Items){
            ApplyAddressResponse.Items item = (ApplyAddressResponse.Items) pojo;
            JsonObject jsonObject = json.getAsJsonObject();
            if(jsonObject.get("Size").isJsonNull()){
                item.setSize("");
            }
        }

        return pojo;
    }
}

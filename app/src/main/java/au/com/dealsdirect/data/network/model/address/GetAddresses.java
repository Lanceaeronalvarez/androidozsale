package au.com.dealsdirect.data.network.model.address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import java.util.ArrayList;
import java.util.List;

/**
 * dp Created by Admin on 11/9/16.
 */
public class GetAddresses {

    public static final class RequestValues {

        public String languageID;

        public String getLanguageID() {
            return languageID;
        }

        public RequestValues(String languageID) {
            this.languageID = languageID;
        }
    }


    public static final class ResponseValue {

        @Expose
        @SerializedName("addresses")
        private List<AddressesItem> AddressesList;
        @Expose
        @SerializedName("address_decorations")
        private List<DecorationInfoList> DecorationInfoList;

        public List<AddressesItem> getAddressesList() {
                return AddressesList;
            }
        public List<DecorationInfoList> getDecorationInfoList() {
                return DecorationInfoList;
            }

    }

}

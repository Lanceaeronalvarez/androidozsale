package au.com.dealsdirect.data.network.model.address;

import com.mysale.genie.utility.LegacyBaseResponseValue;
import java.util.ArrayList;

/**
 * dp Created by Admin on 11/9/16.
 */
public class GetAddresses {

    public static final class RequestValues {

        public String languageID;

        public String getLanguageID(){
            return languageID;
        }

        public RequestValues(String languageID) {
            this.languageID = languageID;
        }
    }


    public static final class ResponseValue {

        public Response d;

        public static class Response extends LegacyBaseResponseValue{
            public Value Value;
        }

        public static class Value {
            public ArrayList<AddressesItem> AddressesList;
            public ArrayList<DecorationInfoList> DecorationInfoList;
        }
    }

}

package au.com.dealsdirect.data.network.model.address;

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

        public Response getD() {
            return d;
        }

        private Response d;

        public static class Response extends LegacyBaseResponseValue {
            private Value Value;

            public ResponseValue.Value getValue() {
                return Value;
            }
        }

        public static class Value {
            public List<AddressesItem> getAddressesList() {
                return AddressesList;
            }

            public List<DecorationInfoList> getDecorationInfoList() {
                return DecorationInfoList;
            }

            private List<AddressesItem> AddressesList;
            private List<DecorationInfoList> DecorationInfoList;
        }
    }

}

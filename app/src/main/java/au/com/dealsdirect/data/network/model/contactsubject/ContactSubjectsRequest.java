package au.com.dealsdirect.data.network.model.contactsubject;

/**
 * dp  Created by Admin on 1/7/17.
 */
public class ContactSubjectsRequest {

    public String countryID;

    public String languageID;

    public ContactSubjectsRequest(String countryID, String languageID) {
        this.countryID = countryID;
        this.languageID = languageID;
    }
}

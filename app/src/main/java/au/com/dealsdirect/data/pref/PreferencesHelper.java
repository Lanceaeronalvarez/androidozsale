package au.com.dealsdirect.data.pref;


import com.mysale.genie.utility.config.model.getserversettings.Language;

import java.util.List;

 public interface PreferencesHelper {

    
    //Elv - add exposed methods here for AppPreferenceHelper
     int getCurrentUserLoggedInMode();
     String getCountryId();

     String getLanguageId();

     List<Language> getLanguages();
     String getCurrency();

     String getCurrencySign();

     String getFollowUsFbLink();

     String getFollowUsTwitterLink();

     boolean isPaypalEnabled();

     boolean isAmexEnabled();

     boolean isMasterpassEnabled();

     boolean isKountEnabled();

     String getKountMerchantId();

     boolean isDebugMode();

     String getFbSecret();

}

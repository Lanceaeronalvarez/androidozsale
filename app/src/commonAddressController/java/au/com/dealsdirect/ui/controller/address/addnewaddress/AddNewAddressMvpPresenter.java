package au.com.dealsdirect.ui.controller.address.addnewaddress;

import android.view.View;

import java.util.HashMap;

import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 20/06/2017.
 */

public interface AddNewAddressMvpPresenter <V extends MvpView> extends MvpPresenter<V>  {

    void addNewAddress(HashMap<DecorationInfoList,View> viewMap);
}

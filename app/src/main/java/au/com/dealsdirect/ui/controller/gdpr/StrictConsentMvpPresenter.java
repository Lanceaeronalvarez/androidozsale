package au.com.dealsdirect.ui.controller.gdpr;

import au.com.dealsdirect.ui.base.MvpPresenter;

public interface StrictConsentMvpPresenter<V extends StrictConsentMvpView> extends MvpPresenter<V> {
    String getConsentContinueText();

    String getConsentFullText();
}

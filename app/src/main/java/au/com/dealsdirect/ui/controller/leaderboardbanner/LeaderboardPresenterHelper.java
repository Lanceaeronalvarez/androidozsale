package au.com.dealsdirect.ui.controller.leaderboardbanner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.banner.GetLeaderboardBannerRequest;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class LeaderboardPresenterHelper {

    private LeaderboardPresenterHelper() {
    }

    public static void loadLeaderboardBanner(GetLeaderboardBannerRequest request,
                                             DataManager dataManager,
                                             CompositeDisposable compositeDisposable,
                                             SchedulerProvider schedulerProvider,
                                             LeaderboardHelperListener listener) {
        dataManager.pruneCachedResponse(request);
        GetBannerResponse response = dataManager.getCachedResponse(request, GetBannerResponse.class);
        if (response != null && listener != null) {
            listener.receiveResponse(response);
        }

        compositeDisposable.add(dataManager
                .callGetLeaderboardBanner(request)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                        getBannerResponse -> {
                            dataManager.setCachedResponse(request, getBannerResponse);
                            if (listener != null) {
                                listener.receiveResponse(getBannerResponse);
                            }
                        },
                        throwable -> {
                            if (listener != null) {
                                listener.receiveError(throwable);
                            }
                        }
                ));
    }

    public static interface LeaderboardHelperListener {
        void receiveResponse(GetBannerResponse response);

        void receiveError(Throwable throwable);
    }
}

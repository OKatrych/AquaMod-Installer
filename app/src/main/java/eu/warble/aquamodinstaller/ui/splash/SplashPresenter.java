package eu.warble.aquamodinstaller.ui.splash;


import android.os.Bundle;
import android.support.annotation.Nullable;

import eu.warble.aquamodinstaller.R;
import eu.warble.aquamodinstaller.data.AquaRemoteDataSource;
import eu.warble.aquamodinstaller.ui.base.BaseActivityPresenter;
import eu.warble.aquamodinstaller.utils.Constants;
import eu.warble.aquamodinstaller.utils.Tools;

public class SplashPresenter extends BaseActivityPresenter<SplashActivity> {

    public SplashPresenter(SplashActivity activity) {
        super(activity);
    }

    @Override
    protected void initPresenter(@Nullable Bundle savedInstanceState) {}

    void checkIMEI(){
        AquaRemoteDataSource.getInstance().checkIMEI(activity.getApplicationContext(), new AquaRemoteDataSource.CheckIMEICallback() {
            @Override
            public void onImeiCorrect() {
                activity.showToast("onImeiCorrect");
                checkRoot();
            }

            @Override
            public void onImeiIncorrect() {
                activity.setLoadingViewsState(false);
                activity.showError(activity.getString(R.string.imei_Error));
            }

            @Override
            public void onError(String error) {
                activity.setLoadingViewsState(false);
                switch (error){
                    case Constants.PERMISSIONS_ERROR :
                        activity.showError(Constants.PERMISSIONS_ERROR);
                        break;
                    case Constants.CONNECTION_ERROR:
                        activity.showError(Constants.CONNECTION_ERROR);
                    default:
                        activity.showError("Unknown error");
                }
            }
        });
    }

    void checkRoot(){
        activity.actionText.setText(R.string.checking_root);
        if (Tools.isRooted())
            checkBusyBoxInstalled();
        else {
            activity.setLoadingViewsState(false);
            activity.showError(activity.getString(R.string.root_error));
        }
    }

    void checkBusyBoxInstalled(){
        activity.actionText.setText(R.string.checking_busybox);
        if (Tools.isAppInstalled("stericson.busybox", activity.getApplicationContext())){
            activity.startMainActivity();
        }else {
            activity.setLoadingViewsState(false);
            activity.showError(activity.getString(R.string.busybox_error));
        }
    }
}

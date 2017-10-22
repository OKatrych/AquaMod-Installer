package eu.warble.aquamodinstaller.ui.main.search_mod;


import android.os.Bundle;
import android.support.annotation.Nullable;

import eu.warble.aquamodinstaller.R;
import eu.warble.aquamodinstaller.data.AquaLocalDataSource;
import eu.warble.aquamodinstaller.data.AquaRemoteDataSource;
import eu.warble.aquamodinstaller.data.model.VersionFile;
import eu.warble.aquamodinstaller.ui.base.BaseFragmentPresenter;
import eu.warble.aquamodinstaller.utils.Constants;
import eu.warble.aquamodinstaller.utils.Tools;

public class SearchModPresenter extends BaseFragmentPresenter<SearchModFragment> {


    SearchModPresenter(SearchModFragment fragment) {
        super(fragment);
    }

    @Override
    protected void initPresenter(@Nullable Bundle savedInstanceState) {}

    void checkModAvailable() {
        AquaRemoteDataSource.getInstance().checkUpdate(new AquaRemoteDataSource.CheckUpdateCallback() {
            @Override
            public void onUpdateAvailable(VersionFile versionFile) {
                AquaLocalDataSource.saveVersionFile(versionFile, fragment.context.getApplicationContext());
                fragment.onModDownloadFound();
            }

            @Override
            public void onUpdateNotAvailable() {
                fragment.setLoadingViewsState(false);
            }

            @Override
            public void onError(String error) {
                fragment.setLoadingViewsState(false);
                switch (error){
                    case Constants.CONNECTION_ERROR:
                        fragment.showError(Constants.CONNECTION_ERROR);
                        break;
                    case Constants.DEVICE_MODEL_ERROR:
                        fragment.showError(Constants.DEVICE_MODEL_ERROR);
                        break;
                    default:
                        fragment.showError("Unknown error");
                }
            }
        });
    }
}

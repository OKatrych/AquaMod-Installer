package eu.warble.aquamodinstaller.ui.main.install_mod;


import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;

import java.io.File;
import java.util.Locale;

import eu.warble.aquamodinstaller.R;
import eu.warble.aquamodinstaller.data.AquaLocalDataSource;
import eu.warble.aquamodinstaller.data.model.VersionFile;
import eu.warble.aquamodinstaller.ui.base.BaseFragmentPresenter;
import eu.warble.aquamodinstaller.utils.AppExecutors;
import eu.warble.aquamodinstaller.utils.Constants;
import eu.warble.aquamodinstaller.utils.Installer;
import eu.warble.aquamodinstaller.utils.Tools;

public class InstallModPresenter extends BaseFragmentPresenter<InstallModFragment> {

    long downloadId;
    private String fileLocation;
    private VersionFile versionFile;
    private AppExecutors executors;
    private String fileName;
    private String newFolderLocation;

    InstallModPresenter(InstallModFragment fragment) {
        super(fragment);
    }

    @Override
    protected void initPresenter(@Nullable Bundle savedInstanceState) {
        executors = new AppExecutors();
        versionFile = AquaLocalDataSource.getVersionFile(fragment.context);

        fileName = "aqua_" + versionFile.getVersion().toLowerCase(Locale.getDefault()).replaceAll(" ", "_") + ".zip";
        fileLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + fileName;
        newFolderLocation = Environment.getExternalStorageDirectory().getPath() + "/Aqua/update/";

        if (Tools.fileHasAlreadyDownloaded(fileLocation))
            fragment.showInstallButton();
        else
            fragment.showDownloadButton();
    }

    void downloadUpdate() {
        String phoneModel = Tools.getProperty("ro.product.device").replace(" ", "");
        String downloadURL = Constants.UPDATE_FILE_URL.replace("PHONE_MODEL", phoneModel);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadURL));
        request.setDescription(fragment.getString(R.string.update_downloading));
        request.setTitle(fileName);
        request.allowScanningByMediaScanner();
        request.setVisibleInDownloadsUi(false);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        DownloadManager manager = (DownloadManager) fragment.getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = manager != null ? manager.enqueue(request) : -1;
    }

    void installUpdate() {
        Installer.install(fileLocation, newFolderLocation, executors, new Installer.InstallProgressListener() {
            @Override
            public void onChangeProgress(String message) {
                fragment.output.setText(String.format("%s%s\n", fragment.output.getText(), message));
            }

            @Override
            public void onError(String error) {
                fragment.output.setText(String.format("%s%s\n", fragment.output.getText(), error));
                fragment.header.setText(R.string.error);
                fragment.progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Called when download completed
     */
    void checkMD5Sum() {
        Tools.checkMD5Sum(fileLocation, versionFile.getMd5(), executors, new Tools.CheckSumCallback() {
            @Override
            public void onSumCorrect() {
                fragment.showInstallButton();
            }

            @Override
            public void onSumIncorrect() {
                Tools.removeDirectory(new File(fileLocation));
                fragment.showCheckSumError();
            }
        });
    }
}

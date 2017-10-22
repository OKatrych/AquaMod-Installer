package eu.warble.aquamodinstaller.data;


import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;

import eu.warble.aquamodinstaller.data.model.VersionFile;
import eu.warble.aquamodinstaller.utils.AppExecutors;
import eu.warble.aquamodinstaller.utils.Constants;
import eu.warble.aquamodinstaller.utils.Tools;

public class AquaRemoteDataSource {

    private AppExecutors executors;

    private static class SingletonHelper{
        private static final AquaRemoteDataSource INSTANCE = new AquaRemoteDataSource();
    }

    private AquaRemoteDataSource(){
        executors = new AppExecutors();
    }

    public static AquaRemoteDataSource getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public void checkUpdate(CheckUpdateCallback callback){
        String deviseModel = Tools.getProperty("ro.product.device").replace(" ", "");
        String currentVersion = Tools.getProperty("ro.aqua.version");
        String url = Constants.VERSION_FILE_URL.replace("PHONE_MODEL", deviseModel);
        executors.networkIO().execute(() ->{
            StringBuilder sb = new StringBuilder();
            try(BufferedReader in = new BufferedReader(new InputStreamReader(
                    new URL(url).openStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    sb.append(inputLine).append('\n');
            }catch (IOException e){
                if (e instanceof UnknownHostException)
                    executors.mainThread().execute(() -> callback.onError(Constants.CONNECTION_ERROR));
                else if (e instanceof FileNotFoundException)
                    executors.mainThread().execute(() -> callback.onError(Constants.DEVICE_MODEL_ERROR));
                else
                    executors.mainThread().execute(() -> callback.onError(e.getMessage()));
                e.printStackTrace();
                return;
            }
            String file = sb.toString();
            VersionFile versionFile = new VersionFile(file);
            if (currentVersion.equals(versionFile.getVersion()))
                executors.mainThread().execute(callback::onUpdateNotAvailable);
            else
                executors.mainThread().execute(() -> callback.onUpdateAvailable(versionFile));
        });
    }

    public void checkIMEI(Context context, CheckIMEICallback callback){
        String deviseModel = Tools.getProperty("ro.product.device").replace(" ", "");
        String url = Constants.IMEI_LIST_URL.replace("PHONE_MODEL", deviseModel);
        executors.networkIO().execute(()-> {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED && telephonyManager != null) {
                String imei = telephonyManager.getDeviceId();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(
                        new URL(Constants.IMEI_LIST_URL).openStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        if (inputLine.equals(imei))
                            executors.mainThread().execute(callback::onImeiCorrect);
                    }
                    executors.mainThread().execute(callback::onImeiIncorrect);
                } catch (IOException e) {
                    if (e instanceof UnknownHostException)
                        executors.mainThread().execute(() -> callback.onError(Constants.CONNECTION_ERROR));
                    else
                        executors.mainThread().execute(() -> callback.onError(e.getMessage()));
                    e.printStackTrace();
                    return;
                }
            } else
                executors.mainThread().execute(() -> callback.onError(Constants.PERMISSIONS_ERROR));
        });
    }

    public interface CheckUpdateCallback{
        void onUpdateAvailable(VersionFile versionFile);
        void onUpdateNotAvailable();
        void onError(String error);
    }

    public interface CheckIMEICallback{
        void onImeiCorrect();
        void onImeiIncorrect();
        void onError(String error);
    }
}

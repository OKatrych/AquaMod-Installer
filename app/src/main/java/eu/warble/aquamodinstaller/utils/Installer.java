package eu.warble.aquamodinstaller.utils;


import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;

public class Installer {

    public static void install(String fileLocation, String newFolderLocation,
                               AppExecutors executors, InstallProgressListener listener){
        if (Tools.fileHasAlreadyDownloaded(fileLocation)){
            File newFolder = new File(newFolderLocation);
            if (newFolder.exists())
                Tools.removeDirectory(newFolder);
            if (!newFolder.mkdirs()) {
                Log.e("startInstalling()", "Cannot create folder");
                return;
            }
            executors.diskIO().execute(() -> {
                executors.mainThread().execute(() -> listener.onChangeProgress("Unzipping update file"));
                ZipUtils.extract(new File(fileLocation), newFolder);
                Tools.removeDirectory(new File(fileLocation));
                executeScript(newFolderLocation + "aqua/", executors, listener);
            });
        }else {
            executors.mainThread().execute(() -> listener.onError("Error: downloaded file not exist"));
        }
    }

    private static void executeScript(String scriptLocation, AppExecutors executors, InstallProgressListener listener) {
        try {
            executors.mainThread().execute(() -> listener.onChangeProgress("Running install script"));
            Process p = Runtime.getRuntime().exec("su");
            Thread.sleep(3000);
            try (DataOutputStream outputStream = new DataOutputStream(p.getOutputStream());
                 BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                executors.mainThread().execute(() -> listener.onChangeProgress("Please wait..."));
                outputStream.writeBytes("cd " + scriptLocation + "\n");
                outputStream.writeBytes("sh " + "script.sh" + ";\n");
                outputStream.flush();
                String out;
                while ((out = in.readLine()) != null){
                    final String tmp = out;
                    executors.mainThread().execute(() -> listener.onChangeProgress(tmp));
                }
                p.waitFor();
            }
            Log.i("UpdateFragment", "executeScriptEnd()");
        }catch (Exception ex){
            executors.mainThread().execute(() -> listener.onError("Error: \n" + ex.getMessage()));
            ex.printStackTrace();
        }
    }

    public interface InstallProgressListener{
        void onChangeProgress(String message);
        void onError(String error);
    }
}

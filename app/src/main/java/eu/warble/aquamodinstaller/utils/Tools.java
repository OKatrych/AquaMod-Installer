package eu.warble.aquamodinstaller.utils;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.warble.aquamodinstaller.data.model.VersionFile;

public class Tools {
    /**
     * No root access required
     * @param property some property from build.prop file
     * @return build.prop property
     */
    public static String getProperty(@NonNull String property){
        Process p;
        String board_platform = "";
        try {
            p = new ProcessBuilder("/system/bin/getprop", property).redirectErrorStream(true).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = br.readLine()) != null){
                board_platform = line;
            }
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return board_platform;
    }

    public static void checkMD5Sum(String fileLocation, String md5, AppExecutors executors, CheckSumCallback callback) {
        executors.diskIO().execute(() -> {
            String checkSum1 = createChecksum(fileLocation);
            //if sum correct
            if (checkSum1 != null && md5 != null && checkSum1.equals(md5))
                executors.mainThread().execute(callback::onSumCorrect);
            else
                executors.mainThread().execute(callback::onSumIncorrect);
        });
    }

    public static String getParsedVersion(String s){
        int start = s.indexOf("-version")+9;
        int end = s.indexOf("-flymevers", start)-1;
        return s.substring(start, end);
    }

    public static String getParsedTitle1(String s){
        int start = s.indexOf("-title1")+8;
        int end = s.indexOf("-title2", start)-1;
        return s.substring(start, end);
    }

    public static String getParsedTitle2(String s){
        int start = s.indexOf("-title2")+8;
        int end = s.indexOf("-changelog", start)-1;
        return s.substring(start, end);
    }

    public static String getParsedFlymeVer(String s){
        int start = s.indexOf("-flymevers")+11;
        int end = s.indexOf("-nextupdate", start)-1;
        return s.substring(start, end);
    }

    public static String getParsedNextUpdate(String s){
        int start = s.indexOf("-nextupdate")+12;
        int end = s.indexOf("-title1", start)-1;
        return s.substring(start, end);
    }

    public static String getParsedChangelog(String s){
        int start = s.indexOf("-changelog")+11;
        int end = s.indexOf("-filesize", start)-1;
        return s.substring(start, end);
    }

    public static String getParsedFileSize(String s){
        int start = s.indexOf("-filesize")+10;
        int end = s.indexOf("-md5", start)-1;
        return s.substring(start, end);
    }

    public static String getParsedMD5(String s){
        int start = s.indexOf("-md5")+5;
        int end = s.length()-1;
        return s.substring(start, end);
    }

    public static void removeDirectory(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                removeDirectory(f);
            }
        }
        file.delete();
    }

    private static String createChecksum(String fileName){
        try(InputStream fis = new FileInputStream(fileName)){
            byte[] buffer = new byte[1024];
            MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead;
            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);
            byte[] b = complete.digest();
            StringBuilder result = new StringBuilder();
            for (byte aB : b) {
                result.append(Integer.toString((aB & 0xff) + 0x100, 16).substring(1));
            }
            return result.toString().toUpperCase();
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public static boolean fileHasAlreadyDownloaded(String fileLocation) {
        return new File(fileLocation).exists();
    }

    public static Animation getRotateAnimation(){
        RotateAnimation rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1500);
        rotate.setRepeatCount(Animation.INFINITE);
        return rotate;
    }

    /**
     * Checks if the device is rooted.
     *
     * @return <code>true</code> if the device is rooted, <code>false</code> otherwise.
     */
    public static boolean isRooted() {

        // get from build info
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys"))
            return true;

        if(findBinary("su"))
            return true;

        // try executing commands
        return canExecuteCommand("/system/xbin/which su")
                || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su");
    }

    // executes a command on the system
    private static boolean canExecuteCommand(String command) {
        boolean executedSuccesfully;
        try {
            Runtime.getRuntime().exec(command);
            executedSuccesfully = true;
        } catch (Exception e) {
            executedSuccesfully = false;
        }

        return executedSuccesfully;
    }

    private static boolean findBinary(String binaryName) {
        boolean found = false;
        String[] places = {"/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/",
                "/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"};
        for (String where : places) {
            if ( new File( where + binaryName ).exists() ) {
                found = true;
                break;
            }
        }
        return found;
    }

    public static boolean isAppInstalled (String packageName, Context context){
        PackageManager pm = context.getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
        return launchIntent != null;
    }

    public interface CheckSumCallback{
        void onSumCorrect();
        void onSumIncorrect();
    }

}

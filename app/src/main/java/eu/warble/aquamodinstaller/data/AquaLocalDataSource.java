package eu.warble.aquamodinstaller.data;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import eu.warble.aquamodinstaller.data.model.VersionFile;

public class AquaLocalDataSource {
    private static final String AQUA_PREFS = "Aqua-prefs";

    public static VersionFile getVersionFile(@NonNull Context context){
        SharedPreferences preferences = context.getSharedPreferences(AQUA_PREFS, Activity.MODE_PRIVATE);
        VersionFile file = new VersionFile();
        file.setChangelog(preferences.getString("changelog", ""));
        file.setTitle1(preferences.getString("title1", ""));
        file.setTitle2(preferences.getString("title2", ""));
        file.setFileSize(preferences.getString("fileSize", ""));
        file.setFlymeVersion(preferences.getString("flymeVersion", ""));
        file.setMd5(preferences.getString("md5", ""));
        file.setNextUpdate(preferences.getString("nextUpdate", ""));
        file.setVersion(preferences.getString("version", ""));
        return file;
    }

    public static void saveVersionFile(@NonNull VersionFile file, @NonNull Context context){
        SharedPreferences preferences = context.getSharedPreferences(AQUA_PREFS, Activity.MODE_PRIVATE);
        preferences.edit()
                .putString("changelog", file.getChangelog())
                .putString("title1", file.getTitle1())
                .putString("title2", file.getTitle2())
                .putString("fileSize", file.getFileSize())
                .putString("flymeVersion", file.getFlymeVersion())
                .putString("md5", file.getMd5())
                .putString("nextUpdate", file.getNextUpdate())
                .putString("version", file.getVersion())
                .apply();
    }
}

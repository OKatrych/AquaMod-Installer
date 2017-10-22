package eu.warble.aquamodinstaller.data.model;


import eu.warble.aquamodinstaller.utils.Tools;

public class VersionFile {
    private String version;
    private String flymeVersion;
    private String title1;
    private String title2;
    private String nextUpdate;
    private String fileSize;
    private String changelog;
    private String md5;

    public VersionFile (String fullFile){
        version = Tools.getParsedVersion(fullFile);
        flymeVersion = Tools.getParsedFlymeVer(fullFile);
        title1 = Tools.getParsedTitle1(fullFile);
        title2 = Tools.getParsedTitle2(fullFile);
        nextUpdate = Tools.getParsedNextUpdate(fullFile);
        fileSize = Tools.getParsedFileSize(fullFile);
        changelog = Tools.getParsedChangelog(fullFile);
        md5 = Tools.getParsedMD5(fullFile);
    }

    public VersionFile(){}

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFlymeVersion() {
        return flymeVersion;
    }

    public void setFlymeVersion(String flymeVersion) {
        this.flymeVersion = flymeVersion;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getNextUpdate() {
        return nextUpdate;
    }

    public void setNextUpdate(String nextUpdate) {
        this.nextUpdate = nextUpdate;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
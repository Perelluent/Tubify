/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.perelluent.tubify.dto;

import es.perelluent.mediapollingbean.dto.Media;

/**
 *
 * @author Perelluent
 */
public class LibraryItem {

    private Media cloudMedia;
    private DownloadedFile localFile;

    public LibraryItem() {
    }

    public LibraryItem(Media cloudMedia, DownloadedFile localFile) {
        this.cloudMedia = cloudMedia;
        this.localFile = localFile;
    }

    public Media getCloudMedia() {
        return cloudMedia;
    }

    public void setCloudMedia(Media cloudMedia) {
        this.cloudMedia = cloudMedia;
    }

    public DownloadedFile getLocalFile() {
        return localFile;
    }

    public void setLocalFile(DownloadedFile localFile) {
        this.localFile = localFile;
    }

    public String getMediaName() {
        if (cloudMedia != null) {
            return cloudMedia.mediaFileName;
        }
        if (localFile != null) {
            return localFile.getFileName();
        } else {
            return "Unknown";
        }
    }

    public String getMediaId() {
        if (cloudMedia != null) {
            return cloudMedia.toString();
        }
        if (localFile != null) {
            return null;
        } else {
            return "Unknown";
        }
    }

    public String getMediaMimeType() {
        if (cloudMedia != null) {
            return cloudMedia.mediaMimeType;
        }
        if (localFile != null) {
            return localFile.getMimeType();
        } else {
            return "Unknown";
        }
    }

    public String getMediaDownloadedUrl() {
        if (cloudMedia != null) {
            return cloudMedia.downloadedFromUrl;
        }
        if (localFile != null) {
            return localFile.getFilePath();
        } else {
            return "Unknown";
        }
    }

    public String getStatus() {
        if (cloudMedia != null && localFile != null) {
            return "Downloaded & Local";
        } else if (cloudMedia != null && localFile == null) {
            return "Cloud Only";
        } else if (cloudMedia == null && localFile != null) {
            return "Local Only";
        }
        return "Error";
    }

    public String getSize() {
        if (localFile != null) {
            return localFile.getFileSize();
        } else {
            return "Unknown";
        }
    }

}

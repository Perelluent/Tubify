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
public class MediaFile {

    private Media mediaCloud;
    private DownloadedFile localFile;

    public MediaFile() {
    }

    public Media getMediaCloud() {
        return mediaCloud;
    }

    public void setMediaCloud(Media mediaCloud) {
        this.mediaCloud = mediaCloud;
    }

    public DownloadedFile getLocalFile() {
        return localFile;
    }

    public void setLocalFile(DownloadedFile localFile) {
        this.localFile = localFile;
    }

    public String getMediaName() {
        if (mediaCloud != null) {
            return mediaCloud.mediaFileName;
        }
        if (localFile != null) {
            return localFile.getFileName();
        } else {
            return "Unknown";
        }
    }
    
    public String getMediaId() {
        if (mediaCloud != null) {
            return mediaCloud.toString();
        }
        if (localFile != null) {
            return null;
        } else {
            return "Unknown";
        }
    }
    
    public String getMediaMimeType() {
        if (mediaCloud != null) {
            return mediaCloud.mediaMimeType;
        }
        if (localFile != null) {
            return localFile.getMimeType();
        } else {
            return "Unknown";
        }
    } 
    public String getMediaDownloadedUrl() {
        if (mediaCloud != null) {
            return mediaCloud.downloadedFromUrl;
        }
        if (localFile != null) {
            return localFile.getFilePath();
        } else {
            return "Unknown";
        }
    }

}

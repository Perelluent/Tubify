/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.perelluent.tubify.dto;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Perelluent
 */
public class DownloadedFile {

    private String fileName;
    private String filePath;
    private long fileSize;
    private String mimeType;
    private LocalDateTime downloadDate;

    public DownloadedFile(String fileName, String filePath, long fileSize, String mimeType, LocalDateTime downloadDate) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.downloadDate = downloadDate;
    }

    public DownloadedFile(File file) {
        this.fileName = file.getName();
        this.filePath = file.getAbsolutePath();
        this.fileSize = file.length();
        this.downloadDate = LocalDateTime.now();
    }

    public DownloadedFile() {
    }
    

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public LocalDateTime getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(LocalDateTime downloadDate) {
        this.downloadDate = downloadDate;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dateFormat = downloadDate.format(formatter);
        return fileName + " - " + dateFormat;
    }  
}

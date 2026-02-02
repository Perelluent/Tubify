/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.perelluent.tubify.dto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public DownloadedFile(File file){
        this.fileName = file.getName();
        this.filePath = file.getAbsolutePath();
        this.fileSize = file.length() / (1024 * 1024);
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

    public String getFileSize() {
        return fileSize + " MB";
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

    public String getDownloadDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
        String dateFormat = downloadDate.format(formatter);
        return dateFormat;
    }

    public void setDownloadDate(LocalDateTime downloadDate) {
        this.downloadDate = downloadDate;
    }
    
   public boolean deleteFromDisk() {
    try {
        File f = new File(this.filePath);
        return f.delete();
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
        return false;
    }
}

    @Override
    public String toString() {
        return fileName;
    }  
}

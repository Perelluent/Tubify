/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package es.perelluent.tubify.dto;

import es.perelluent.mediapollingbean.dto.Media;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Perelluent
 */
public class LibraryTableModel extends AbstractTableModel {

    private final String[] columnNames = {"ID", "File Name", "Type", "Origin"};
    private List<Media> mediaList;

    public LibraryTableModel() {
        this.mediaList = new ArrayList<>();
    }

    public LibraryTableModel(List<Media> mediaList) {
        this.mediaList = mediaList;
    }

    public void setMediaList(List<Media> mediaList) {
        this.mediaList = mediaList;
        fireTableDataChanged();
    }

    public void addMedia(Media media) {
        this.mediaList.add(media);
        fireTableRowsInserted(mediaList.size() - 1, mediaList.size() - 1);
    }

    public void removeMedia(int rowIndex) {
        this.mediaList.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public Media getMediaAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < mediaList.size()) {
            return mediaList.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return mediaList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Media m = mediaList.get(rowIndex);
        switch (columnIndex) {
            case 0: return m.id;
            case 1: return m.mediaFileName;
            case 2: return m.mediaMimeType;
            case 3: return m.downloadedFromUrl;
            default: return null;
        }
    }
}
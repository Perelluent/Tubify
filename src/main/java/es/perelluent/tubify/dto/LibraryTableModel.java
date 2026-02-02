/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package es.perelluent.tubify.dto;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Perelluent
 */
public class LibraryTableModel extends AbstractTableModel {

    private final String[] columnNames = {"File Name", "Type", "Size", "Origin","Status"};
    private List<LibraryItem> items;

    public LibraryTableModel() {
        this.items = new ArrayList<>();
    }

    public void setItems(List<LibraryItem> items) {
        this.items = items;
        fireTableDataChanged();
    }
    
    public LibraryItem getItemAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < items.size()) return items.get(rowIndex);
        return null;
    }
    
    public void removeItem(int rowIndex) {
        this.items.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    @Override
    public int getRowCount() { return items.size(); }
    @Override
    public int getColumnCount() { return columnNames.length; }
    @Override
    public String getColumnName(int column) { return columnNames[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LibraryItem item = items.get(rowIndex);
        
        switch (columnIndex) {
            case 0: if (item.getCloudMedia() != null) return item.getCloudMedia().id;
                else return "Local File";
            default: return null;
            case 1: return item.getMediaName();
            case 2: return item.getMediaMimeType();
//            case 3: return item.getSize();
//            case 4: return item.getMediaDownloadedUrl();
//            case 5: return item.getStatus();
        }
    }
}
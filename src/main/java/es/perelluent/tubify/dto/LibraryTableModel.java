/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package es.perelluent.tubify.dto;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Table model used to display a list of {@link LibraryItem} objects inside the
 * LibraryPanel JTable.
 * @author Perelluent
 */
public class LibraryTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Type", "Origin", "File Name"};
    private List<LibraryItem> items;

    public LibraryTableModel() {
        this.items = new ArrayList<>();
    }
    // ------------------------- 
    // Getters & Setters 
    // -------------------------
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

    // ----------------------------
    // AbstractTableModel overrides 
    // ----------------------------
    
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
            case 0: return item.getMediaMimeType();
            case 1: return item.getStatus();
            case 2: return item.getMediaName();
            default: return null;
        }
    }
}
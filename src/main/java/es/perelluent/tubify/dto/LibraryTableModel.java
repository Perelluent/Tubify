/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package es.perelluent.tubify.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Perelluent
 */
public class LibraryTableModel extends AbstractTableModel implements ListDataListener{
    private final DefaultListModel<DownloadedFile> listDownloadedFilesModel;
    private final String[] columnNames = {"File Name","Size","Type","Download Date"};
    private List<DownloadedFile> filteredList;
    private String currentFilter = "";
    
    public LibraryTableModel(DefaultListModel<DownloadedFile> listDownloadedFile) {
        this.listDownloadedFilesModel = listDownloadedFile;
        this.listDownloadedFilesModel.addListDataListener(this);
        this.filteredList = new ArrayList<>(Collections.list(listDownloadedFile.elements()));
        
    }
    
    public DownloadedFile getFileAt(int rowIndex) {
        if(rowIndex < 0 || rowIndex >= filteredList.size()){
            return null;
        }
        return filteredList.get(rowIndex);
    }
    public void removeFile(DownloadedFile file) {
        listDownloadedFilesModel.removeElement(file);
    }
    
    public void filterByType(String type) {
        currentFilter = type;
        filteredList.clear();
        
        for (int i = 0; i < listDownloadedFilesModel.getSize(); i++) {
            DownloadedFile file = listDownloadedFilesModel.getElementAt(i);
            String mime = file.getMimeType() != null ? file.getMimeType() : "";
            
            if (type.isEmpty() || mime.startsWith(type)) {
                filteredList.add(file);
            }
        }
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return filteredList.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        DownloadedFile file = filteredList.get(rowIndex);
        
        switch (columnIndex) {
            case 0:
                return file.getFileName();
            case 1:
                return file.getFileSize();
            case 2: 
                return file.getMimeType();
            case 3: 
                return file.getDownloadDate();
            default:
                throw new AssertionError();
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        filterByType(currentFilter);
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        filterByType(currentFilter);
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        filterByType(currentFilter);
    }
    
}
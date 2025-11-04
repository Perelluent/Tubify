/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package es.perelluent.tubify.dto;

import javax.swing.DefaultListModel;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Perelluent
 */
public class LibraryTableModel extends AbstractTableModel{
    private final DefaultListModel<DownloadedFile> listDownloadedFilesModel;
    private final String[] columnNames = {"File Name","Size","Type","Download Date"};
    
    public LibraryTableModel(DefaultListModel<DownloadedFile> listDownloadedFile) {
        this.listDownloadedFilesModel = listDownloadedFile;
    }

    @Override
    public int getRowCount() {
        return listDownloadedFilesModel.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        switch (columnIndex) {
            case 0:
                return listDownloadedFilesModel.get(rowIndex).getFileName();
            case 1:
                return listDownloadedFilesModel.get(rowIndex).getFileSize();
            case 2: 
                return listDownloadedFilesModel.get(rowIndex).getMimeType();
            case 3: 
                return listDownloadedFilesModel.get(rowIndex).getDownloadDate();
            default:
                throw new AssertionError();
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}
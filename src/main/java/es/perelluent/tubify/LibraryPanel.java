/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package es.perelluent.tubify;

import es.perelluent.mediapollingbean.dto.Media;
import es.perelluent.tubify.dto.DownloadedFile;
import es.perelluent.tubify.dto.LibraryItem;
import es.perelluent.tubify.dto.LibraryTableModel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author morda
 */
public class LibraryPanel extends javax.swing.JPanel {

    private final MainWindow main;
    private LibraryTableModel libraryModel;
    private TableRowSorter<LibraryTableModel> sorter;

    /**
     * Creates new form LibraryPanel
     *
     * @param main
     */
    public LibraryPanel(MainWindow main) {
        this.main = main;

        this.setLayout(new BorderLayout());

        initComponents();

        libraryModel = new LibraryTableModel();
        tblLibrary.setModel(libraryModel);

        this.add(jScrollPane1, BorderLayout.CENTER);

        sorter = new TableRowSorter<>(libraryModel);
        tblLibrary.setRowSorter(sorter);

        cmbFilter.addItem(new FilterCategory("Show All", ""));
        cmbFilter.addItem(new FilterCategory("Videos Only", "video"));
        cmbFilter.addItem(new FilterCategory("Audio Only", "audio"));

        cmbFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FilterCategory selected = (FilterCategory) cmbFilter.getSelectedItem();
                filterTable(selected.getFilterValue());
            }

        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnDeleteActionPerformed(e);
            }

        });
    }

    public void loadMedia() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Obtener la lista de la nube
                    List<Media> cloudList = null;
                    // Cargamos si el token no es null
                    if (main.getMediaPollingBean().getToken() != null) {
                        try {
                            cloudList = main.getMediaPollingBean().getAllMedia(); // cojemos todos los archivos
                        } catch (Exception e) {
                            System.err.println("Error cargando nube: " + e.getMessage());
                        }
                    }

                    // Obtener la lista local
                    List<DownloadedFile> localList = new ArrayList<>();
                    // Leemos las preferencias directamente desde el archivo properties para asegurar la ruta
                    String userHome = System.getProperty("user.home");
                    String propsPath = userHome + File.separator + "TubifySettings.properties";
                    java.util.Properties props = new java.util.Properties();

                    File propsFile = new File(propsPath);
                    if (propsFile.exists()) {
                        try (FileInputStream in = new java.io.FileInputStream(propsFile)) {
                            props.load(in);
                            String folderPath = props.getProperty("libraryPath");

                            if (folderPath != null) {
                                File folder = new File(folderPath);
                                if (folder.exists() && folder.isDirectory()) {
                                    File[] files = folder.listFiles();
                                    if (files != null) {
                                        for (File f : files) {
                                            if (f.isFile() && !f.getName().startsWith(".")) {
                                                localList.add(new DownloadedFile(f));
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    // Lista para final de las dos listas fusionadas
                    final List<LibraryItem> mergedList = mergeLists(cloudList, localList);

                    // Actualizamos la tabla.
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            libraryModel.setItems(mergedList);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private List<LibraryItem> mergeLists(List<Media> cloud, List<DownloadedFile> local) {
        Map<String, LibraryItem> map = new HashMap<>();
        // mapeamos los archivos que hay en la nube
        if (cloud != null) {
            for (Media m : cloud) {
                map.put(m.mediaFileName, new LibraryItem(m, null));
            }
        }
        // mapeamos los archivos locales
        if (local != null) {
            for (DownloadedFile df : local) {
                String name = df.getFileName();
                if (map.containsKey(name)) {
                    map.get(name).setLocalFile(df);
                } else {
                    map.put(name, new LibraryItem(null, df));
                }
            }
        }
        return new ArrayList<>(map.values());
    }

    private void filterTable(String query) {
        if (query.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query, 2));
        }
    }

    class FilterCategory {

        private final String displayName;
        private final String FilterValue;

        public FilterCategory(String displayName, String FilterValue) {
            this.displayName = displayName;
            this.FilterValue = FilterValue;
        }

        public String getFilterValue() {
            return FilterValue;
        }

        @Override
        public String toString() {
            return displayName;
        }

    }

    public void showNewMediaList(List<LibraryItem> newFiles) {
        if (newFiles == null || newFiles.isEmpty()) {
            return;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblLibrary = new javax.swing.JTable();
        btnDelete = new javax.swing.JButton();
        lblFilter = new javax.swing.JLabel();
        lblMediaLibrary = new javax.swing.JLabel();
        cmbFilter = new javax.swing.JComboBox<>();
        btnPlay = new javax.swing.JButton();
        btnUpload = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(1100, 700));
        setLayout(null);

        tblLibrary.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblLibrary);

        add(jScrollPane1);
        jScrollPane1.setBounds(10, 90, 790, 450);

        btnDelete.setText("Delete File");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        add(btnDelete);
        btnDelete.setBounds(10, 560, 110, 23);

        lblFilter.setText("Filter");
        add(lblFilter);
        lblFilter.setBounds(20, 20, 41, 16);

        lblMediaLibrary.setFont(new java.awt.Font("sansserif", 0, 30)); // NOI18N
        lblMediaLibrary.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMediaLibrary.setText("MEDIA LIBRARY");
        add(lblMediaLibrary);
        lblMediaLibrary.setBounds(260, 30, 290, 40);

        add(cmbFilter);
        cmbFilter.setBounds(20, 50, 170, 22);

        btnPlay.setText("PLAY");
        btnPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayActionPerformed(evt);
            }
        });
        add(btnPlay);
        btnPlay.setBounds(720, 560, 75, 23);

        btnUpload.setText("UPLOAD");
        btnUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadActionPerformed(evt);
            }
        });
        add(btnUpload);
        btnUpload.setBounds(290, 560, 260, 23);
    }// </editor-fold>//GEN-END:initComponents

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
//    int viewRow = tblLibrary.getSelectedRow();
//        if (viewRow == -1) {
//            JOptionPane.showMessageDialog(this, "Select a file to delete.");
//            return;
//        }
//
//        int modelRow = tblLibrary.convertRowIndexToModel(viewRow);
//        LibraryItem fileToDelete = libraryModel.getMediaAt(modelRow);
//
//        int choice = JOptionPane.showConfirmDialog(this,
//                "Remove '" + fileToDelete.mediaFileName + "' from list?\n(Note: API delete not supported yet)",
//                "Confirm", JOptionPane.YES_NO_OPTION);
//
//        if (choice == JOptionPane.YES_OPTION) {
//            // Nota: El ApiClient proporcionado NO tiene método delete. 
//            // Solo lo quitamos de la vista visual.
//            libraryModel.removeMedia(modelRow);
//        }

    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayActionPerformed
        int viewRow = tblLibrary.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a file to play.");
            return;
        }
        int modelRow = tblLibrary.convertRowIndexToModel(viewRow);
        LibraryItem selectedMedia = libraryModel.getItemAt(modelRow);

        if (selectedMedia == null) {
            return;
        }

        // si está en local
        if (selectedMedia.getLocalFile() != null) {
            File file = new File(selectedMedia.getLocalFile().getFilePath());

            if (file.exists()) {
                try {
                    java.awt.Desktop.getDesktop().open(file);
                } catch (IOException ex) {
                    System.getLogger(LibraryPanel.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
                return;
            } else {
                // Si no existe la ruta
                javax.swing.JOptionPane.showMessageDialog(this,
                        "File missing from disk: " + file.getAbsolutePath(),
                        "Local File Error", javax.swing.JOptionPane.WARNING_MESSAGE);
            }
        }

        // Si está solo en la nube
        if (selectedMedia.getCloudMedia() != null) {
            playCloudMedia(selectedMedia.getCloudMedia());
        }
    }

    private void playCloudMedia(Media media) {
        // Usamos un SwingWorker para no congelar la pantalla mientras baja
        SwingWorker<File, Void> worker = new SwingWorker<>() {
            @Override
            protected File doInBackground() throws Exception {
                // Crear archivo temporal
                String ext = media.mediaFileName.contains(".")
                        ? media.mediaFileName.substring(media.mediaFileName.lastIndexOf(".")) : ".tmp";
                File tempFile = File.createTempFile("tubify_stream_", ext);
                tempFile.deleteOnExit();

                main.getMediaPollingBean().download(media.id, tempFile); //

                return tempFile;
            }

            @Override
            protected void done() {
                try {
                    File tempFile = get();
                    java.awt.Desktop.getDesktop().open(tempFile);
                } catch (Exception ex) {
                    javax.swing.JOptionPane.showMessageDialog(LibraryPanel.this,
                            "Error streaming file: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        };

        // Mostrar cursor de espera
        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        worker.addPropertyChangeListener(evt -> {
            if (javax.swing.SwingWorker.StateValue.DONE == evt.getNewValue()) {
                this.setCursor(java.awt.Cursor.getDefaultCursor());
            }
        });
        worker.execute();
    }//GEN-LAST:event_btnPlayActionPerformed

    private void btnUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadActionPerformed
        int viewRow = tblLibrary.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a local file to upload.");
            return;
        }

        int modelRow = tblLibrary.convertRowIndexToModel(viewRow);
        LibraryItem item = libraryModel.getItemAt(modelRow);
        // Si no está en local
        if (item.getLocalFile() == null) {
            JOptionPane.showMessageDialog(this, 
                "This file is only in the Cloud. You cannot upload it again.", 
                "Upload Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Si ya está subido
        if (item.getCloudMedia() != null) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "This file is already synchronized with the Cloud.", 
                "Info", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Subida
            SwingWorker<Void, Void> worker = new javax.swing.SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {

                File fileToUpload = new File(item.getLocalFile().getFilePath());
                
                if (!fileToUpload.exists()) {
                    throw new FileNotFoundException("File not found: " + fileToUpload.getAbsolutePath());
                }

                main.getMediaPollingBean().uploadFileMultipart(fileToUpload,""); 
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(LibraryPanel.this, 
                        "Upload Successful!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Recargar la lista de items
                    loadMedia(); 
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(LibraryPanel.this, 
                        "Upload Failed: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_btnUploadActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnPlay;
    private javax.swing.JButton btnUpload;
    private javax.swing.JComboBox<FilterCategory> cmbFilter;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblFilter;
    private javax.swing.JLabel lblMediaLibrary;
    private javax.swing.JTable tblLibrary;
    // End of variables declaration//GEN-END:variables
}

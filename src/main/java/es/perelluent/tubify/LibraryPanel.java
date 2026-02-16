/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package es.perelluent.tubify;

import es.perelluent.mediapollingbean.dto.Media;
import es.perelluent.tubify.dto.DownloadedFile;
import es.perelluent.tubify.dto.LibraryIconRenderer;
import es.perelluent.tubify.dto.LibraryItem;
import es.perelluent.tubify.dto.LibraryTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.TableRowSorter;
import net.miginfocom.swing.MigLayout;

public class LibraryPanel extends JPanel {

    private final MainWindow main;
    private LibraryTableModel libraryModel;
    private JTable tblLibrary;
    private JScrollPane scrollPane;
    private JTextField txtSearch;
    private String token = null;
    private final LoginPanel loginPanel;

    // Botones
    private JButton btnPlay, btnUpload, btnDelete, btnLogout, btnPrefences;

    public LibraryPanel(MainWindow main) {
        this.main = main;
        this.token = main.getToken();
        this.loginPanel = main.getLoginPanel();
        setLayout(new MigLayout("fill, insets 10 0 10 10", "[grow, fill]", "[][grow][]"));
        initComponents();

        loadMedia();
    }

    private void initComponents() {

        JPanel pnlHeader = new JPanel(new MigLayout("fillx, insets 0", "[][grow, center][right]"));
        pnlHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("LIBRARY");
        lblTitle.setFont(new Font("Montserrat", Font.BOLD, 22));

        JLabel lblLogo = new JLabel();
        java.net.URL imageUrl = getClass().getResource("/images/logo.png");
        if (imageUrl != null) {
            lblLogo.setIcon(MainWindow.UpscaleIcon(new ImageIcon(imageUrl), 200, 80));
        }
        ImageIcon logoutImg = null;
        java.net.URL logoutUrl = getClass().getResource("/images/logout.png");
        if (logoutUrl != null) {
            logoutImg = MainWindow.UpscaleIcon(new ImageIcon(logoutUrl), 80, 80);
        }

        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Search media...");
        txtSearch.putClientProperty("FlatLaf.style", "arc: 12");
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filter(txtSearch.getText());
            }
        });

        libraryModel = new LibraryTableModel();
        tblLibrary = new JTable(libraryModel);
        tblLibrary.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLibrary.putClientProperty("Table.selectionBackground", Color.decode("#f39bab"));
        LibraryIconRenderer iconRenderer = new LibraryIconRenderer();
        tblLibrary.getColumnModel().getColumn(0).setCellRenderer(iconRenderer);
        tblLibrary.getColumnModel().getColumn(1).setCellRenderer(iconRenderer);

        tblLibrary.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblLibrary.getColumnModel().getColumn(0).setMaxWidth(60);

        tblLibrary.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblLibrary.getColumnModel().getColumn(1).setMaxWidth(60);

        tblLibrary.getColumnModel().getColumn(2).setPreferredWidth(800);
        tblLibrary.setRowHeight(40);
        tblLibrary.setShowVerticalLines(false);
        tblLibrary.getTableHeader().setReorderingAllowed(false);
        tblLibrary.setFont(new Font("Montserrat", Font.PLAIN, 13));

        scrollPane = new JScrollPane(tblLibrary);
        scrollPane.putClientProperty("FlatLaf.style", null);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JPanel pnlActions = new JPanel(new MigLayout("fillx, insets 10 0 0 0", "[]10[]push[]"));
        pnlActions.setOpaque(false);

        ImageIcon deleteIcon = null;
        URL deleteIconUrl = getClass().getResource("/images/rubbish.png");
        if (deleteIconUrl != null) {
            deleteIcon = MainWindow.UpscaleIcon(new ImageIcon(deleteIconUrl), 80, 80);
        }
        btnDelete = new JButton(deleteIcon);
        btnDelete.setToolTipText("Delete File");
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.putClientProperty("JButton.buttonType", "toolBarButton");
        btnDelete.setContentAreaFilled(false);
        btnDelete = createStyledButton("DELETE", null);

        ImageIcon uploadIcon = null;
        URL uploadIconUrl = getClass().getResource("/images/upload.png");
        if (uploadIconUrl != null) {
            uploadIcon = MainWindow.UpscaleIcon(new ImageIcon(uploadIconUrl), 80, 80);
        }
        btnUpload = new JButton(uploadIcon);
        btnUpload.setToolTipText("Upload File");
        btnUpload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpload.putClientProperty("JButton.buttonType", "toolBarButton");
        btnUpload.setContentAreaFilled(false);
        btnUpload = createStyledButton("UPLOAD", null);

        ImageIcon playIcon = null;
        java.net.URL playIconUrl = getClass().getResource("/images/play.png");
        if (playIconUrl != null) {
            playIcon = MainWindow.UpscaleIcon(new ImageIcon(playIconUrl), 60, 60);
        }
        btnPlay = createStyledButton("PLAY MEDIA", Color.decode("#fb3f62"));
        btnPlay.setForeground(Color.WHITE);

        btnLogout = new JButton(logoutImg);
        btnLogout.setToolTipText("Logout");
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR) {
        });
        btnLogout.setContentAreaFilled(false);

        // Listeners de botones
        btnPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnPlayActionPerformed(e);
            }
        });
        final ImageIcon iconPlay = playIcon;
        btnPlay.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnPlay.setContentAreaFilled(false);
                btnPlay.setText("");
                btnPlay.setToolTipText("play");
                btnPlay.setIcon(iconPlay);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnPlay.setContentAreaFilled(true);
                btnPlay.setIcon(null);
                btnPlay.setText("PLAY MEDIA");
            }
        });
        btnUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnUploadActionPerformed(e);
            }
        });
        final ImageIcon iconUpload = uploadIcon;
        btnUpload.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnUpload.setContentAreaFilled(false);
                btnUpload.setText("");
                btnUpload.setToolTipText("Upload");
                btnUpload.setIcon(iconUpload);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnUpload.setContentAreaFilled(true);
                btnUpload.setIcon(null);
                btnUpload.setText("UPLOAD");
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnDeleteActionPerformed(e);
            }
        });
        final ImageIcon iconDelete = deleteIcon;
        btnDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnDelete.setContentAreaFilled(false);
                btnDelete.setText("");
                btnDelete.setToolTipText("Delete");
                btnDelete.setIcon(iconDelete);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnDelete.setContentAreaFilled(true);
                btnDelete.setIcon(null);
                btnDelete.setText("DELETE");
            }
        });
        ImageIcon preferencesIcon = null;
        URL preferencesIconUrl = getClass().getResource("/images/preferences.png");
        if (preferencesIconUrl != null) {
            preferencesIcon = MainWindow.UpscaleIcon(new ImageIcon(preferencesIconUrl), 100, 100);
        }

        btnPrefences = new JButton(preferencesIcon);
        btnPrefences.setToolTipText("Preferences");
        btnPrefences.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrefences.putClientProperty("JButton.buttonType", "toolBarButton");
        btnPrefences.setContentAreaFilled(false);
        btnPrefences = createStyledButton("PREFERENCES", null);
        final ImageIcon iconPreferences = preferencesIcon;
        btnPrefences.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnPrefences.setContentAreaFilled(false);
                btnPrefences.setText("");
                btnPrefences.setToolTipText("Preferences");
                btnPrefences.setIcon(iconPreferences);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnPrefences.setContentAreaFilled(true);
                btnPrefences.setIcon(null);
                btnPrefences.setText("PREFERENCES");
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnLogoutActionPerformed(e);
            }

        });
        btnPrefences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.showPreferences();
            }

        });

        pnlHeader.add(lblLogo);
        pnlHeader.add(lblTitle, "gapleft 5");
        pnlHeader.add(txtSearch, "width 250!");
        pnlHeader.add(btnPrefences);

        pnlActions.add(btnDelete, "w 110!, h 40!");
        pnlActions.add(btnUpload, "w 110!, h 40!");
        pnlActions.add(btnPlay, "w 220!, h 50!, center");
        pnlActions.add(btnPrefences, "w 130!, h 40!, right");
        pnlActions.add(btnLogout, "w 40!, h 40!, right");

        // Añadimos al panel principal
        add(pnlHeader, "wrap, gapbottom 10");
        add(scrollPane, "grow, wrap");
        add(pnlActions, "growx");
    }

    // método que crea botones iguales.
    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Montserrat", Font.BOLD, 12));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (bg != null) {
            btn.setBackground(bg);
        }
        return btn;
    }

    // filtrar la tabla
    private void filter(String query) {
        TableRowSorter<LibraryTableModel> sorter = new TableRowSorter<>(libraryModel);
        tblLibrary.setRowSorter(sorter);
        if (query.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            try {
                String escapedQuery = java.util.regex.Pattern.quote(query);

                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + escapedQuery));
            } catch (java.util.regex.PatternSyntaxException e) {
                System.err.println("Error en el patrón de búsqueda: " + e.getMessage());
            }
        }
    }

    // Action Events de los botones
    private void btnPlayActionPerformed(java.awt.event.ActionEvent evt) {
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

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed

        int response = JOptionPane.showConfirmDialog(
                null,
                "¿Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION
        );
        if (response == JOptionPane.YES_OPTION) {

            try {
                main.getMediaPollingBean().setRunning(false);
                main.getMediaPollingBean().setToken(null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            this.token = null;
            loginPanel.clearTextAreas();
            main.showLoginPanel();
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
    }

    private void btnUploadActionPerformed(java.awt.event.ActionEvent evt) {
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

                main.getMediaPollingBean().uploadFileMultipart(fileToUpload, "");
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
    }

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tblLibrary.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(LibraryPanel.this, "Please. First select a File to delete.");
            return;
        }

        int modelRow = tblLibrary.convertRowIndexToModel(selectedRow);
        LibraryItem item = libraryModel.getItemAt(modelRow);

        if (item != null && item.getLocalFile() != null) {
            int confirm = JOptionPane.showConfirmDialog(
                    LibraryPanel.this,
                    "Are you sure you want to delete '" + item.getMediaName() + "' from your computer?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = item.getLocalFile().deleteFromDisk();

                if (deleted) {
                    JOptionPane.showMessageDialog(LibraryPanel.this, "File deleted from disk.");
                    loadMedia();
                } else {
                    JOptionPane.showMessageDialog(LibraryPanel.this,
                            "The file could not be deleted. It might be in use by another program.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(LibraryPanel.this,
                    "This file is only in the Cloud. Local deletion is not possible.");
        }
    }

    // cargar la tabla
    public void loadMedia(String fileToSelect) {
        // Definimos el hilo para la carga de datos (Nube + Local)
        Thread thread = new Thread(() -> {
            try {
                // 1. Cargar lista de la nube
                List<Media> cloudList = null;
                if (main.getMediaPollingBean().getToken() != null) {
                    try {
                        cloudList = main.getMediaPollingBean().getAllMedia();
                    } catch (Exception e) {
                        System.err.println("Error cargando nube: " + e.getMessage());
                    }
                }

                // 2. Cargar lista local desde las preferencias
                List<DownloadedFile> localList = new ArrayList<>();
                String userHome = System.getProperty("user.home");
                String propsPath = userHome + File.separator + "TubifySettings.properties";
                java.util.Properties props = new java.util.Properties();

                File propsFile = new File(propsPath);
                if (propsFile.exists()) {
                    try (FileInputStream in = new FileInputStream(propsFile)) {
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

                // 3. Mezclar listas
                final List<LibraryItem> mergedList = mergeLists(cloudList, localList);

                // 4. Actualizar la interfaz de usuario (UI)
                SwingUtilities.invokeLater(() -> {
                    // Actualizamos los datos del modelo
                    libraryModel.setItems(mergedList);

                    // Si hay un archivo específico para seleccionar
                    if (fileToSelect != null && !fileToSelect.isEmpty()) {
                        String searchName = fileToSelect.contains(".")
                                ? fileToSelect.substring(0, fileToSelect.lastIndexOf("."))
                                : fileToSelect;

                        // Timer de 100ms para asegurar que JTable ha renderizado las nuevas filas
                        javax.swing.Timer selectionTimer = new javax.swing.Timer(200, e -> {
                            String cleanTarget = fileToSelect.replaceAll("\\.f\\d+\\.[a-z0-9]+$", "") // Quita temporal yt-dlp
                                    .replaceAll("\\.[^.]+$", "") // Quita extensión
                                    .replaceAll("[^a-zA-Z0-9]", "") // Quita TODO lo que no sea letra/número
                                    .toLowerCase();

                            System.out.println("Buscando coincidencia simplificada para: " + cleanTarget);

                            for (int i = 0; i < tblLibrary.getRowCount(); i++) {
                                Object value = tblLibrary.getValueAt(i, 2);
                                if (value != null) {
                                    // 2. LIMPIEZA AGRESIVA de la fila de la tabla para comparar en igualdad de condiciones
                                    String nameInTable = value.toString().replaceAll("\\.[^.]+$", "") // Quita extensión
                                            .replaceAll("[^a-zA-Z0-9]", "") // Quita símbolos
                                            .toLowerCase();

                                    // 3. Comparación por proximidad
                                    if (!cleanTarget.isEmpty() && (nameInTable.contains(cleanTarget) || cleanTarget.contains(nameInTable))) {
                                        final int row = i;
                                        // Ejecutamos la selección en el hilo de UI
                                        tblLibrary.setRowSelectionInterval(row, row);

                                        // Asegurar scroll
                                        Rectangle rect = tblLibrary.getCellRect(row, 0, true);
                                        tblLibrary.scrollRectToVisible(rect);

                                        // Importante: Foco para resaltar
                                        tblLibrary.requestFocusInWindow();

                                        System.out.println("¡LOGRADO! Coincidencia encontrada en fila: " + row);
                                        return;
                                    }
                                }
                            }
                        });
                        selectionTimer.setRepeats(false);
                        selectionTimer.start();
                    }
                });

            } catch (Exception e) {
                System.err.println("Error en la carga de medios: " + e.getMessage());
            }
        });
        thread.start();
    }

    /**
     * Versión sobrecargada para recargar la lista sin seleccionar nada
     * específico.
     */
    public void loadMedia() {
        loadMedia(null);
    }

    // Combinar archivos en la nube y local
    private List<LibraryItem> mergeLists(List<Media> cloud, List<DownloadedFile> local) {
        Map<String, LibraryItem> map = new HashMap<>();
        if (cloud != null) {
            for (Media m : cloud) {
                map.put(m.mediaFileName, new LibraryItem(m, null));
            }
        }
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
}

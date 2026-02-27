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
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.table.TableRowSorter;
import net.miginfocom.swing.MigLayout;

/**
 * Panel that displays and manages the user's media library, combining both local
 * files stored on disk and cloud media synchronized through the application's
 * backend services.
 * This panel provides search, playback, upload, deletion, and logout actions,
 * along with automatic synchronization of media items. It uses a custom
 * {@link LibraryTableModel} to render media entries and integrates with
 * {@link MainWindow} to access shared components
 * 
 * Media loading is performed asynchronously, merging cloud and local sources.
 * The panel also supports automatic selection of newly downloaded files.
 * 
 * @author Perelluent
 * @version 1.0
 */

public class LibraryPanel extends JPanel {

    private final MainWindow main;
    private LibraryTableModel libraryModel;
    private JTable tblLibrary;
    private JScrollPane scrollPane;
    private JTextField txtSearch;
    private String token = null;
    private final LoginPanel loginPanel;

    private JButton btnPlay, btnUpload, btnDelete, btnLogout, btnPrefences;

    public LibraryPanel(MainWindow main) {
        this.main = main;
        this.token = main.getToken();
        this.loginPanel = main.getLoginPanel();
        setLayout(new MigLayout("fill, insets 10 0 10 10", "[grow, fill]", "[][grow][]"));
        initComponents();

        loadMedia();
    }

      /**
        * Initializes and configure all the components.
        *  The interface includes:
        * </p>
        * <ul>
        *   <li>A header with logo, title, search bar, and preferences button</li>
        *   <li>A table listing local and cloud media with icons and metadata</li>
        *   <li>Action buttons for playing, uploading, deleting, and logging out</li>
        * </ul>
        *
        * <p>
     */
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

        // Listeners
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

        // add to the main panel
        add(pnlHeader, "wrap, gapbottom 10");
        add(scrollPane, "grow, wrap");
        add(pnlActions, "growx");
    }

    /**
     * Creates a styled button with consistent appearance across the panel.
     * @param text that have the button.
     * @param bg optional background color.
     * @return a configured JButton.
     */
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

    /**
     * Filters the media table based on the given query.
     * @param query the text used to filter media names.
     */
    private void filter(String query) {
        TableRowSorter<LibraryTableModel> sorter = new TableRowSorter<>(libraryModel);
        tblLibrary.setRowSorter(sorter);
        if (query.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            try {
                String escapedQuery = Pattern.quote(query);

                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + escapedQuery));
            } catch (java.util.regex.PatternSyntaxException e) {
                System.err.println("Error en el patrón de búsqueda: " + e.getMessage());
            }
        }
    }

    /** 
     * Handles the play button action. It opens the media item in the default
     * media player. Streams cloud media by downloading it temprarily.
     * 
     * @see #playCloudMedia(es.perelluent.mediapollingbean.dto.Media) 
     * 
     * @param evt the action event triggered by the Play button.
     */
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

        if (selectedMedia.getLocalFile() != null) {
            File file = new File(selectedMedia.getLocalFile().getFilePath());

            if (file.exists()) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException ex) {
                    System.getLogger(LibraryPanel.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
                return;
            } else {
                JOptionPane.showMessageDialog(this,
                        "File missing from disk: " + file.getAbsolutePath(),
                        "Local File Error", JOptionPane.WARNING_MESSAGE);
            }
        }
        if (selectedMedia.getCloudMedia() != null) {
            playCloudMedia(selectedMedia.getCloudMedia());
        }
    }

    /**
     * Logs the user out of the application. Stops the media polling service, clears authentication tokens,
     * resets login fields, and returns the user to the login panel.
     * 
     * @param evt the action event triggered by the Logout button
     */
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

    /**
     * Handles the Upload button action that upload a file media to the cloud.
     * 
     * @param evt the action event triggeres by the Upload Button.
     */
    private void btnUploadActionPerformed(java.awt.event.ActionEvent evt) {
        int viewRow = tblLibrary.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a local file to upload.");
            return;
        }

        int modelRow = tblLibrary.convertRowIndexToModel(viewRow);
        LibraryItem item = libraryModel.getItemAt(modelRow);
        // If it's not in disk.
        if (item.getLocalFile() == null) {
            JOptionPane.showMessageDialog(this,
                    "This file is only in the Cloud. You cannot upload it again.",
                    "Upload Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // If it's already in the cloud.
        if (item.getCloudMedia() != null) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "This file is already synchronized with the Cloud.",
                    "Info", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
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

                    // Reload item list
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

    /**
     * Handles the Delete button action that deletes a local file from disk after user confirmation.
     * 
     * @param evt the action event triggered by the Delete button.
     */
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
    /**
     * Streams a cloud media file by downloading it temporarily and opening it.
     * The download is performed in a background {@link SwingWorker} to avoid
     * blocking the UI. A temporary file is created and deleted automatically
     * when the JVM exits.
     * 
     * @param media the cloud media metadata to stream
     */
    private void playCloudMedia(Media media) {

        SwingWorker<File, Void> worker = new SwingWorker<>() {
            @Override
            protected File doInBackground() throws Exception {
                // Creates the temporary file
                String ext = media.mediaFileName.contains(".")
                        ? media.mediaFileName.substring(media.mediaFileName.lastIndexOf(".")) : ".tmp";
                File tempFile = File.createTempFile("tubify_stream_", ext);
                tempFile.deleteOnExit();

                main.getMediaPollingBean().download(media.id, tempFile); 

                return tempFile;
            }

            @Override
            protected void done() {
                try {
                    File tempFile = get();
                    Desktop.getDesktop().open(tempFile);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LibraryPanel.this,
                            "Error streaming file: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        };

        // changes to wait cursor
        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        worker.addPropertyChangeListener(evt -> {
            if (javax.swing.SwingWorker.StateValue.DONE == evt.getNewValue()) {
                this.setCursor(java.awt.Cursor.getDefaultCursor());
            }
        });
        worker.execute();
    }

    /**
     * Loads media from both cloud and local sources, merges them, and updates the table.
     *
     * This method runs asynchronously to avoid blocking the UI. If a filename is
     * provided, the table attempts to automatically select and scroll to the
     * corresponding row after loading.
     * 
     * @param fileToSelect optional filename to highlight after loading
     */
    public void loadMedia(String fileToSelect) {

        Thread thread = new Thread(() -> {
            try {
                // Load cloud list
                List<Media> cloudList = null;
                if (main.getMediaPollingBean().getToken() != null) {
                    try {
                        cloudList = main.getMediaPollingBean().getAllMedia();
                    } catch (Exception e) {
                        System.err.println("Error cargando nube: " + e.getMessage());
                    }
                }

                // Load local list
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

                // Merge lists
                final List<LibraryItem> mergedList = mergeLists(cloudList, localList);

                // Update media list
                SwingUtilities.invokeLater(() -> {
                    libraryModel.setItems(mergedList);

                    // If there are an specific file to select
                    if (fileToSelect != null && !fileToSelect.isEmpty()) {
                        String searchName = fileToSelect.contains(".")
                                ? fileToSelect.substring(0, fileToSelect.lastIndexOf("."))
                                : fileToSelect;

                        // 100ms Timer to ensure the JTable has finished rendering the new rows
                        javax.swing.Timer selectionTimer = new javax.swing.Timer(200, e -> {
                            String cleanTarget = fileToSelect.replaceAll("\\.f\\d+\\.[a-z0-9]+$", "") // Remove temporal yt-dlp
                                    .replaceAll("\\.[^.]+$", "") // Remove extension
                                    .replaceAll("[^a-zA-Z0-9]", "") // Removes everything that is NOT a letter or a digit 
                                    .toLowerCase();

                            for (int i = 0; i < tblLibrary.getRowCount(); i++) {
                                Object value = tblLibrary.getValueAt(i, 2);
                                if (value != null) {
                                    String nameInTable = value.toString().replaceAll("\\.[^.]+$", "")
                                            .replaceAll("[^a-zA-Z0-9]", "") // Remove symbols
                                            .toLowerCase();

                                    // Proximity comparison
                                    if (!cleanTarget.isEmpty() && (nameInTable.contains(cleanTarget) || cleanTarget.contains(nameInTable))) {
                                        final int row = i;
                                        tblLibrary.setRowSelectionInterval(row, row);
                                        Rectangle rect = tblLibrary.getCellRect(row, 0, true);
                                        tblLibrary.scrollRectToVisible(rect);
                                        tblLibrary.requestFocusInWindow();

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
     * Reloads the media list without selecting any specific file.
     */
    public void loadMedia() {
        loadMedia(null);
    }

    /**
     * Merges cloud media and local files into a unified list of {@link LibraryItem}.
     *
     * Items are matched by filename. If a file exists both locally and in the cloud,
     * they are combined into a single entry.
     * 
     * @param cloud list of cloud media items
     * @param local list of local downloaded media items
     * @return a merged list of library items
     */
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

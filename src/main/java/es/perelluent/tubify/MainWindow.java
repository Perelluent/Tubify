/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package es.perelluent.tubify;

import es.perelluent.tubify.dto.DownloadedFile;
import java.awt.Desktop;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author Perelluent
 */

public class MainWindow extends javax.swing.JFrame {

    private final String YTDLP_PATH = System.getenv("LOCALAPPDATA") + "\\yt-dlp\\yt-dlp.exe";
    private final Preferences preferences = new Preferences(this);
    private final LibraryPanel libraryPanel = new LibraryPanel(this);
    private String lastDownloadedFilePath = null;
    private final Properties props = new Properties();
    private final String PROPERTIES_PATH = System.getProperty("user.home") + File.separator + "TubifySettings.properties";
    private final DefaultListModel<DownloadedFile> dlmDownloaded;

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainWindow.class.getName());

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {

        initComponents();

        dlmDownloaded = new DefaultListModel<>();
        lstDownloaded.setModel(dlmDownloaded);

        loadPreferences();
        scanLibraryFolder();
        
        ImageIcon lblLogoIcon = new ImageIcon("src\\main\\resources\\images\\TubifyLogoTransparent.png");
        ImageIcon scaledIcon = UpscaleIcon(lblLogoIcon, 150, 150);
        lblLogo.setIcon(scaledIcon);
        lblLogo.setBounds(20, 10, 130, 100);
        pnlMain.add(lblLogo);

        preferences.setBounds(0, 0, getWidth(), getHeight());
        preferences.setVisible(false);
        libraryPanel.setVisible(false);
        getContentPane().add(preferences);
        getContentPane().add(libraryPanel);

    }

    private void downloadVideo(String outputPath) {
        //txaDebug.append("Trying to download " + txtUrl.getText() + "...\n\n");
        //txaDebug.append(YTDLP_PATH);
        SwingWorker<Void, String> worker;
        worker = new SwingWorker<Void, String>() {

            private boolean downloadSucceeded = false;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Replace "yourExecutable.exe" and arguments as needed
                    /*ProcessBuilder pb = new ProcessBuilder(YTDLP_PATH, txtUrl.getText(), "-o",
                            outputPath
                    );*/
                    String ytdlpExePath = props.getProperty("ytdlpPath", YTDLP_PATH);
                    String url = txtUrl.getText().trim();
                    String selectedRes = (String) cmbResolucion.getSelectedItem();

                    List<String> cmd = new ArrayList<>();

                    cmd.add(ytdlpExePath);
                    cmd.add(url);
                    cmd.add("-o");
                    cmd.add(outputPath);
                    cmd.add("--no-playlist");

                    if (chkOnlyAudio.isSelected()) {
                        String selectedAudioFormat = (String) cmbAudioFormat.getSelectedItem();
                        cmd.add("-x");
                        cmd.add("--audio-format");
                        cmd.add(selectedAudioFormat);

                    } else {
                        String selectedResolution = (String) cmbResolucion.getSelectedItem();
                        String formatSelector = chooseResolution(selectedResolution);
                        cmd.add("-f");
                        cmd.add(formatSelector);
                        cmd.add("--merge-output-format");
                        cmd.add("mp4");
                    }
                    String speedLimit = props.getProperty("speedLimit", "");
                    if (speedLimit != null && !speedLimit.trim().isEmpty()) {
                        cmd.add("--limit-rate");
                        cmd.add(speedLimit.trim());
                    }
                    String tempPath = props.getProperty("tempDirPath", "");
                    if (tempPath != null && !tempPath.trim().isEmpty()) {
                        cmd.add("--paths");
                        cmd.add("temp:" + tempPath.trim());
                    }
                    boolean createM3u = Boolean.parseBoolean(props.getProperty("createM3u", "false"));
                    if (createM3u) {
                        cmd.add("--create-m3u");
                    }
                    ProcessBuilder pb = new ProcessBuilder(cmd);
                    pb.redirectErrorStream(true);
                    Process process = pb.start();

                    // Read output
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("doInBackground> 1 line published. In thread " + Thread.currentThread().getName());
                        System.out.println("\t" + line);

                        publish(line);
                    }

                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        publish("Download completed successfully!");

                        downloadSucceeded = true;

                    } else {
                        publish("Download failed with exit code: " + exitCode);

                    }
                } catch (IOException | InterruptedException e) {
                    publish("Error: " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                System.out.println("Process> " + chunks.size() + " lines recieved. In thread " + Thread.currentThread());
                for (String line : chunks) {
                    System.out.println("\t" + line);
                    txaDownloadResult.append(line + "\n");
                    Pattern pattern = Pattern.compile("\\[download\\]\\s+(\\d+\\.\\d+)%");
                    Matcher matcher = pattern.matcher(line);

                    if (matcher.find()) {
                        try {
                            double percentage = Double.parseDouble(matcher.group(1));
                            int valor = (int) percentage;

                            if (valor >= 0 && valor <= 100) {
                                prg1.setValue(valor);
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing progress percentage: " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            protected void done() {
                try {
                    get();

                    if (downloadSucceeded) {
                        scanLibraryFolder();
                        publish("Library refreshed.");
                    }
                } catch (Exception e) {
                    publish("Error on worker completion: " + e.getMessage());
                }
            }

        };
        worker.execute();
    }

    public void savePreferences() {
        try (FileOutputStream out = new FileOutputStream(PROPERTIES_PATH)) {
            props.setProperty("ytdlpPath", preferences.getYtdlpPath());
            props.setProperty("tempDirPath", preferences.getTempDirPath());
            props.setProperty("speedLimit", preferences.getSelectedSpeedLimit());
            props.setProperty("createM3u", String.valueOf(preferences.isM3uCreationEnabled()));
            props.setProperty("libraryPath", preferences.getLibraryPath());

            props.store(out, "Tubify Application Settings");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving preferences: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public final void loadPreferences() {
        File configFile = new File(PROPERTIES_PATH);
        String defaultDownloads = new JFileChooser().getFileSystemView().getDefaultDirectory().getPath();
        if (!configFile.exists()) {
            preferences.setYtdlpPath(YTDLP_PATH);
            preferences.setLibraryPath(defaultDownloads);
            return;
        }

        try (FileInputStream in = new FileInputStream(PROPERTIES_PATH)) {
            props.load(in);
            preferences.setYtdlpPath(props.getProperty("ytdlpPath", YTDLP_PATH));
            preferences.setTempDirPath(props.getProperty("tempDirPath", ""));
            preferences.setSelectedSpeedLimit(props.getProperty("speedLimit", ""));
            preferences.setM3uCreationEnabled(Boolean.parseBoolean(props.getProperty("createM3u", "false")));
            preferences.setLibraryPath(props.getProperty("libraryPath", defaultDownloads));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading preferences: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String chooseResolution(String selected) {
        if (selected == null) {
            return "bestvideo+bestaudio/best";
        }
        String s = selected;
        if (s.contains("FullHD")) {
            return "bv*[height<=1080]+ba/b[height<=1080]";
        } else if (s.contains("720")) {
            return "bv*[height<=720]+ba/b[height<=720]";
        } else if (s.contains("480")) {
            return "bv*[height<=480]+ba/b[height<=480]";
        }
        return "bestvideo+bestaudio/best";
    }

    public void showMainWindow() {
        pnlMain.setVisible(true);
        preferences.setVisible(false);
    }

    public void showPreferencesWindow() {
        pnlMain.setVisible(false);
        preferences.setVisible(true);
        preferences.repaint();
    }

    public void showLibraryWindow() {
        pnlMain.setVisible(false);
        libraryPanel.setModelLibrary(dlmDownloaded);
        libraryPanel.setVisible(true);
        libraryPanel.repaint();
    }

    private void addFileToLibrary(String finalFilePath) throws IOException {
        File file = new File(finalFilePath);
        if (file.exists()) {
            DownloadedFile newDownloadedFile = new DownloadedFile(file);
            dlmDownloaded.addElement(newDownloadedFile);
        } else {
            throw new IOException("The file could not be found in: " + finalFilePath);
        }
    }

    private void scanLibraryFolder() {

        String libraryPath = props.getProperty("libraryPath");

        if (libraryPath == null || libraryPath.trim().isEmpty()) {
            System.err.println("No se puede escanear: la ruta de la biblioteca no está configurada.");
            return;
        }

        File libraryDir = new File(libraryPath);
        if (!libraryDir.exists() || !libraryDir.isDirectory()) {
            System.err.println("No se puede escanear: la ruta no existe o no es una carpeta.");
            return;
        }

        dlmDownloaded.clear();

        File[] filesInDir = libraryDir.listFiles();
        if (filesInDir == null) {
            return;
        }

        for (File file : filesInDir) {
            if (file.isFile()) {

                String name = file.getName().toLowerCase();
                if (name.endsWith(".mkv") || name.endsWith(".mp4") || name.endsWith(".mp3")
                        || name.endsWith(".wav") || name.endsWith(".m4a")) {
                    try {
                        DownloadedFile df = new DownloadedFile(file);
                        dlmDownloaded.addElement(df);
                    } catch (Exception e) {
                        System.err.println("Error: " + file.getName());
                    }
                }
            }
        }
    }
    public static ImageIcon UpscaleIcon(ImageIcon icon, int width, int height) {
        Image OriginalImage = icon.getImage();
        Image UpscaledImage = OriginalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(UpscaledImage);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        lblDownloadVideo = new javax.swing.JLabel();
        txtUrl = new javax.swing.JTextField();
        btnDownload = new javax.swing.JButton();
        prg1 = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaDownloadResult = new javax.swing.JTextArea();
        btnPreferences = new javax.swing.JButton();
        cmbResolucion = new javax.swing.JComboBox<>();
        chkOnlyAudio = new javax.swing.JCheckBox();
        btnPlayLast = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstDownloaded = new javax.swing.JList<>();
        lblDownloadedFiles = new javax.swing.JLabel();
        btnLibrary = new javax.swing.JButton();
        cmbAudioFormat = new javax.swing.JComboBox<>();
        lblLogo = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mniExit = new javax.swing.JMenuItem();
        mnuEdit = new javax.swing.JMenu();
        mniPreferences = new javax.swing.JMenuItem();
        mnuHelp = new javax.swing.JMenu();
        mniAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Tubify");
        setBounds(new java.awt.Rectangle(100, 100, 100, 100));
        setMinimumSize(new java.awt.Dimension(900, 900));
        setResizable(false);
        setSize(new java.awt.Dimension(900, 900));
        getContentPane().setLayout(null);

        pnlMain.setPreferredSize(new java.awt.Dimension(1100, 700));
        pnlMain.setLayout(null);

        lblDownloadVideo.setText("Download video");
        pnlMain.add(lblDownloadVideo);
        lblDownloadVideo.setBounds(160, 60, 100, 16);

        txtUrl.setText("Paste your link...");
        txtUrl.setCaretColor(new java.awt.Color(51, 51, 255));
        pnlMain.add(txtUrl);
        txtUrl.setBounds(50, 100, 340, 22);

        btnDownload.setText("Download");
        btnDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadActionPerformed(evt);
            }
        });
        pnlMain.add(btnDownload);
        btnDownload.setBounds(50, 150, 99, 40);

        prg1.setForeground(new java.awt.Color(51, 51, 255));
        pnlMain.add(prg1);
        prg1.setBounds(50, 240, 340, 20);

        txaDownloadResult.setColumns(20);
        txaDownloadResult.setRows(5);
        jScrollPane1.setViewportView(txaDownloadResult);

        pnlMain.add(jScrollPane1);
        jScrollPane1.setBounds(50, 280, 340, 375);

        btnPreferences.setText("Preferences");
        btnPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreferencesActionPerformed(evt);
            }
        });
        pnlMain.add(btnPreferences);
        btnPreferences.setBounds(450, 100, 121, 23);

        cmbResolucion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FullHD", "720p", "480p" }));
        pnlMain.add(cmbResolucion);
        cmbResolucion.setBounds(210, 160, 173, 22);

        chkOnlyAudio.setText("Only Audio");
        chkOnlyAudio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOnlyAudioActionPerformed(evt);
            }
        });
        pnlMain.add(chkOnlyAudio);
        chkOnlyAudio.setBounds(50, 210, 81, 31);

        btnPlayLast.setText("Play from List");
        btnPlayLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayLastActionPerformed(evt);
            }
        });
        pnlMain.add(btnPlayLast);
        btnPlayLast.setBounds(450, 660, 259, 23);

        jScrollPane2.setViewportView(lstDownloaded);

        pnlMain.add(jScrollPane2);
        jScrollPane2.setBounds(450, 280, 259, 375);

        lblDownloadedFiles.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDownloadedFiles.setText("Downloaded Files");
        pnlMain.add(lblDownloadedFiles);
        lblDownloadedFiles.setBounds(490, 210, 156, 16);

        btnLibrary.setText("Go to Library");
        btnLibrary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLibraryActionPerformed(evt);
            }
        });
        pnlMain.add(btnLibrary);
        btnLibrary.setBounds(590, 100, 120, 23);

        cmbAudioFormat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "mp3", "wav", "m4a", "best" }));
        cmbAudioFormat.setEnabled(false);
        pnlMain.add(cmbAudioFormat);
        cmbAudioFormat.setBounds(210, 210, 170, 22);
        pnlMain.add(lblLogo);
        lblLogo.setBounds(100, 60, 41, 16);

        getContentPane().add(pnlMain);
        pnlMain.setBounds(0, 0, 820, 720);

        mnuFile.setText("File");

        mniExit.setText("Exit");
        mniExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniExitActionPerformed(evt);
            }
        });
        mnuFile.add(mniExit);

        jMenuBar1.add(mnuFile);

        mnuEdit.setText("Edit");

        mniPreferences.setText("Preferences");
        mniPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniPreferencesActionPerformed(evt);
            }
        });
        mnuEdit.add(mniPreferences);

        jMenuBar1.add(mnuEdit);

        mnuHelp.setText("Help");

        mniAbout.setText("About");
        mniAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniAboutActionPerformed(evt);
            }
        });
        mnuHelp.add(mniAbout);

        jMenuBar1.add(mnuHelp);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadActionPerformed

        String libraryPath = props.getProperty("libraryPath");

        if (libraryPath == null || libraryPath.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Library folder not set. Please set it in Preferences.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            showPreferencesWindow();
            return;
        }
        String outputPath = libraryPath + File.separator + "%(title)s.%(ext)s";

        downloadVideo(outputPath);
    }//GEN-LAST:event_btnDownloadActionPerformed

    private void btnPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreferencesActionPerformed
        showPreferencesWindow();
    }//GEN-LAST:event_btnPreferencesActionPerformed

    private void mniPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniPreferencesActionPerformed
        showPreferencesWindow();
    }//GEN-LAST:event_mniPreferencesActionPerformed

    private void chkOnlyAudioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOnlyAudioActionPerformed
        boolean isAudioOnly = chkOnlyAudio.isSelected();

        cmbAudioFormat.setEnabled(isAudioOnly);
        cmbResolucion.setEnabled(!isAudioOnly);
    }//GEN-LAST:event_chkOnlyAudioActionPerformed

    private void mniAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniAboutActionPerformed
        AboutDialog dialog = new AboutDialog(this, true);
        dialog.setVisible(true);
    }//GEN-LAST:event_mniAboutActionPerformed

    private void btnPlayLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayLastActionPerformed

        DownloadedFile selectedFile = lstDownloaded.getSelectedValue();

        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a file from the list to play.",
                    "No file selected",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String filePath = selectedFile.getFilePath();
        if (filePath == null || filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "File data is corrupt (no path).",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            File fileToOpen = new File(filePath);

            if (Desktop.isDesktopSupported() && fileToOpen.exists()) {
                Desktop.getDesktop().open(fileToOpen);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cannot open the file. It may have been moved or deleted.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                dlmDownloaded.removeElement(selectedFile);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "An error occurred while trying to open the file:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnPlayLastActionPerformed

    private void mniExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_mniExitActionPerformed

    private void btnLibraryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLibraryActionPerformed
        showLibraryWindow();
    }//GEN-LAST:event_btnLibraryActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainWindow().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDownload;
    private javax.swing.JButton btnLibrary;
    private javax.swing.JButton btnPlayLast;
    private javax.swing.JButton btnPreferences;
    private javax.swing.JCheckBox chkOnlyAudio;
    private javax.swing.JComboBox<String> cmbAudioFormat;
    private javax.swing.JComboBox<String> cmbResolucion;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblDownloadVideo;
    private javax.swing.JLabel lblDownloadedFiles;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JList<es.perelluent.tubify.dto.DownloadedFile> lstDownloaded;
    private javax.swing.JMenuItem mniAbout;
    private javax.swing.JMenuItem mniExit;
    private javax.swing.JMenuItem mniPreferences;
    private javax.swing.JMenu mnuEdit;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JProgressBar prg1;
    private javax.swing.JTextArea txaDownloadResult;
    private javax.swing.JTextField txtUrl;
    // End of variables declaration//GEN-END:variables
}

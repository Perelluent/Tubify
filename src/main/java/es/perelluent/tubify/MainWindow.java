/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package es.perelluent.tubify;

import java.awt.Desktop;
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
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author Perelluent
 */
public class MainWindow extends javax.swing.JFrame {

    private final String YTDLP_PATH = System.getenv("LOCALAPPDATA") + "\\yt-dlp\\yt-dlp.exe";
    private final Preferences preferences = new Preferences(this);
    private String lastDownloadedFilePath = null;
    private final Properties props = new Properties();
    private final String PROPERTIES_PATH = System.getProperty("user.home") + File.separator + "TubifySettings.properties";

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainWindow.class.getName());

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {

        initComponents();
        preferences.setBounds(0, 0, getWidth(), getHeight());
        preferences.setVisible(false);
        getContentPane().add(preferences);

    }

    private void downloadVideo(String outputPath) {
        //txaDebug.append("Trying to download " + txtUrl.getText() + "...\n\n");
        //txaDebug.append(YTDLP_PATH);
        SwingWorker<Void, String> worker;
        worker = new SwingWorker<Void, String>() {
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
                    String formatSelector = chooseResolution(selectedRes);

                    List<String> cmd = new ArrayList<>();

                    cmd.add(YTDLP_PATH);
                    cmd.add(url);
                    cmd.add("-o");
                    cmd.add(outputPath);
                    cmd.add("--no-playlist");

                    if (chkOnlyAudio.isSelected()) {
                        cmd.add("-x");
                        cmd.add("--audio-format");
                        cmd.add("mp3");
                    } else {
                        cmd.add("-f");
                        cmd.add(formatSelector);
                        cmd.add("--merge-output-format");
                        cmd.add("mkv");
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

                        if (chkOnlyAudio.isSelected()) {
                            lastDownloadedFilePath = outputPath.replace(".%(ext)s", ".mp3");
                        } else {
                            lastDownloadedFilePath = outputPath.replace(".%(ext)s", ".mkv");
                        }
                        publish("File saved at: " + lastDownloadedFilePath);

                    } else {
                        publish("Download failed with exit code: " + exitCode);
                        lastDownloadedFilePath = null;
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
        };
        worker.execute();
    }

    public void savePreferences() {
        try (FileOutputStream out = new FileOutputStream(PROPERTIES_PATH)) {
            props.setProperty("ytdlpPath", preferences.getYtdlpPath());
            props.setProperty("tempDirPath", preferences.getTempDirPath());
            props.setProperty("speedLimit", preferences.getSelectedSpeedLimit());
            props.setProperty("createM3u", String.valueOf(preferences.isM3uCreationEnabled()));

            props.store(out, "Tubify Application Settings");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving preferences: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadPreferences() {
        File configFile = new File(PROPERTIES_PATH);
        if (!configFile.exists()) {
            preferences.setYtdlpPath(YTDLP_PATH);
            return;
        }

        try (FileInputStream in = new FileInputStream(PROPERTIES_PATH)) {
            props.load(in);
            preferences.setYtdlpPath(props.getProperty("ytdlpPath", YTDLP_PATH));
            preferences.setTempDirPath(props.getProperty("tempDirPath", ""));
            preferences.setSelectedSpeedLimit(props.getProperty("speedLimit", ""));
            preferences.setM3uCreationEnabled(Boolean.parseBoolean(props.getProperty("createM3u", "false")));
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        lbl1 = new javax.swing.JLabel();
        txtUrl = new javax.swing.JTextField();
        btnDownload = new javax.swing.JButton();
        prg1 = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaDownloadResult = new javax.swing.JTextArea();
        btnPreferences = new javax.swing.JButton();
        cmbResolucion = new javax.swing.JComboBox<>();
        chkOnlyAudio = new javax.swing.JCheckBox();
        btnPlayLast = new javax.swing.JButton();
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

        lbl1.setText("Download video");

        txtUrl.setText("Paste your link...");
        txtUrl.setCaretColor(new java.awt.Color(51, 51, 255));

        btnDownload.setText("Download");
        btnDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadActionPerformed(evt);
            }
        });

        prg1.setForeground(new java.awt.Color(51, 51, 255));

        txaDownloadResult.setColumns(20);
        txaDownloadResult.setRows(5);
        jScrollPane1.setViewportView(txaDownloadResult);

        btnPreferences.setText("Preferences");
        btnPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreferencesActionPerformed(evt);
            }
        });

        cmbResolucion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FullHD", "720p", "480p" }));

        chkOnlyAudio.setText("Only Audio");
        chkOnlyAudio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOnlyAudioActionPerformed(evt);
            }
        });

        btnPlayLast.setText("Play Last Downloaded");
        btnPlayLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayLastActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addGap(170, 170, 170)
                .addComponent(lbl1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(328, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMainLayout.createSequentialGroup()
                        .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlMainLayout.createSequentialGroup()
                                .addComponent(prg1, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnPlayLast, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlMainLayout.createSequentialGroup()
                                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtUrl, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(pnlMainLayout.createSequentialGroup()
                                        .addComponent(btnDownload, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cmbResolucion, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(pnlMainLayout.createSequentialGroup()
                                                .addGap(37, 37, 37)
                                                .addComponent(chkOnlyAudio)))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                                .addComponent(btnPreferences, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(81, 81, 81))))
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(lbl1)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(btnPreferences))
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(txtUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDownload, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbResolucion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkOnlyAudio)
                .addGap(41, 41, 41)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(prg1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPlayLast))
                .addGap(26, 26, 26)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(102, Short.MAX_VALUE))
        );

        getContentPane().add(pnlMain);
        pnlMain.setBounds(0, 0, 720, 720);

        mnuFile.setText("File");

        mniExit.setText("Exit");
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
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save as...");
        chooser.setSelectedFile(new File("video"));
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            String outputPath = selectedFile.getAbsolutePath();

            if (!outputPath.contains("%(ext)s")) {    //para añadir la extensión .mp4
                outputPath = outputPath + ".%(ext)s";
            }
            downloadVideo(outputPath);
        }
    }//GEN-LAST:event_btnDownloadActionPerformed

    private void btnPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreferencesActionPerformed
        showPreferencesWindow();
    }//GEN-LAST:event_btnPreferencesActionPerformed

    private void mniPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniPreferencesActionPerformed
        showPreferencesWindow();
    }//GEN-LAST:event_mniPreferencesActionPerformed

    private void chkOnlyAudioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOnlyAudioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkOnlyAudioActionPerformed

    private void mniAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniAboutActionPerformed
        AboutDialog dialog = new AboutDialog(this, true);
        dialog.setVisible(true);
    }//GEN-LAST:event_mniAboutActionPerformed

    private void btnPlayLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayLastActionPerformed
        if (lastDownloadedFilePath == null || lastDownloadedFilePath.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No file has been downloaded in this session.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            File fileToOpen = new File(lastDownloadedFilePath);

            if (Desktop.isDesktopSupported() && fileToOpen.exists()) {
                Desktop.getDesktop().open(fileToOpen);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cannot open the file. It may have been moved or deleted.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "An error occurred while trying to open the file:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnPlayLastActionPerformed

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
    private javax.swing.JButton btnPlayLast;
    private javax.swing.JButton btnPreferences;
    private javax.swing.JCheckBox chkOnlyAudio;
    private javax.swing.JComboBox<String> cmbResolucion;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl1;
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

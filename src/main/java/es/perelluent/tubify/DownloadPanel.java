/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.perelluent.tubify;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Perelluent
 */
public class DownloadPanel extends JPanel {

    private final MainWindow main;
    private final Properties props = new Properties();

    private JTextField txtUrl;
    private JComboBox<String> cmbResolucion;
    private JCheckBox chkOnlyAudio;
    private JComboBox<String> cmbAudioFormat;
    private JButton btnDownload;
    private JProgressBar progressBar;

    private final String YTDLP_PATH = System.getenv("LOCALAPPDATA") + "\\yt-dlp\\yt-dlp.exe";

    public DownloadPanel(MainWindow main) {
        this.main = main;
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[]20[]20[]20[grow]"));
        setBackground(Color.BLACK);

        initComponents();
    }

    private void initComponents() {

        JLabel lblTitle = new JLabel("Download New Media");
        add(lblTitle, "wrap");

        JPanel pnlInput = new JPanel(new MigLayout("insets 0, fillx", "[][grow]", "[]"));
        pnlInput.setOpaque(false);

        JLabel lblUrl = new JLabel("Video URL:");
        lblUrl.setForeground(Color.LIGHT_GRAY);
        pnlInput.add(lblUrl);

        txtUrl = new JTextField();
        txtUrl.putClientProperty("JTextField.placeholderText", "Paste YouTube link here...");
        txtUrl.putClientProperty("FlatLaf.style", "arc: 10");
        pnlInput.add(txtUrl, "growx");

        add(pnlInput, "growx, wrap");

        JPanel pnlOptions = new JPanel(new MigLayout("insets 0, fillx", "[]20[]20[]", "[]"));
        pnlOptions.setOpaque(false);

        pnlOptions.add(new JLabel("Resolution:"));
        cmbResolucion = new JComboBox<>(new String[]{"FullHD", "720p", "480p", "Best"});
        pnlOptions.add(cmbResolucion, "w 100!");

        chkOnlyAudio = new JCheckBox("Audio Only");
        chkOnlyAudio.setOpaque(false);
        chkOnlyAudio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleAudioOptions();
            }
        });
        pnlOptions.add(chkOnlyAudio);

        // Audio Format
        cmbAudioFormat = new JComboBox<>(new String[]{"mp3", "wav", "m4a", "best"});
        cmbAudioFormat.setEnabled(false);
        pnlOptions.add(cmbAudioFormat, "w 80!");

        // Download Button
        btnDownload = new JButton("START DOWNLOAD");
        btnDownload.setBackground(Color.decode("#c6458f"));
        btnDownload.setForeground(Color.WHITE);
        btnDownload.setFocusPainted(false);
        btnDownload.setBorderPainted(false);
        btnDownload.setContentAreaFilled(false);
        btnDownload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDownload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startDownloadProcess();
            }
        });
        btnDownload.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnDownload.setContentAreaFilled(true);
                btnDownload.setBackground(Color.decode("#c6458f"));
            }

            public void mouseExited(MouseEvent e) {
                btnDownload.setContentAreaFilled(false);
            }
        });

        add(pnlOptions, "growx, wrap");
        add(btnDownload, "h 45!, growx, wrap");

        // 4. Logs & Progress Area
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.putClientProperty("FlatLaf.style", "arc: 10");
        progressBar.setForeground(Color.decode("#7134bf"));
        add(progressBar, "growx, h 20!, wrap");

    }

    private void toggleAudioOptions() {
        boolean isAudio = chkOnlyAudio.isSelected();
        cmbAudioFormat.setEnabled(isAudio);
        cmbResolucion.setEnabled(!isAudio);
    }

    private void startDownloadProcess() {
        String url = txtUrl.getText().trim();
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid URL", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Logic to get output path (Simulated from your Preferences)
        // You should essentially get this from your Preferences class
        String downloadPath = System.getProperty("user.home") + File.separator + "Downloads";

        // If you have a Preferences object in Main, use: main.getPreferencesPanel().getLibraryPath();
        String outputPath = downloadPath + File.separator + "%(title)s.%(ext)s";

        btnDownload.setEnabled(false); // Disable button during download
        downloadVideo(outputPath);
    }

    private void downloadVideo(String outputPath) {

        System.out.println("Starting download");
        progressBar.setValue(0);
        //txaLog.append("Initializing download for: " + txtUrl.getText() + "...\n");

        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            private boolean downloadSucceeded = false;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Logic to find yt-dlp (Using defaults or props)
                    String ytdlpExePath = props.getProperty("ytdlpPath", YTDLP_PATH);

                    List<String> cmd = new ArrayList<>();
                    cmd.add(ytdlpExePath);
                    cmd.add(txtUrl.getText().trim());
                    cmd.add("--user-agent");
                    cmd.add("--no-cache-dir");
                    cmd.add("--no-check-certificate");
                    cmd.add("-o");
                    cmd.add(outputPath);
                    cmd.add("--no-playlist");

                    if (chkOnlyAudio.isSelected()) {
                        String selectedAudioFormat = (String) cmbAudioFormat.getSelectedItem();
                        cmd.add("-x");
                        cmd.add("--audio-format");
                        cmd.add("best".equalsIgnoreCase(selectedAudioFormat) ? "mp3" : selectedAudioFormat);
                    } else {
                        String selectedResolution = (String) cmbResolucion.getSelectedItem();
                        cmd.add("-f");
                        cmd.add(chooseResolution(selectedResolution));
                        cmd.add("--merge-output-format");
                        cmd.add("mp4");
                    }

                    // Add other flags like --limit-rate here if needed
                    ProcessBuilder pb = new ProcessBuilder(cmd);
                    pb.redirectErrorStream(true);
                    Process process = pb.start();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        publish(line);
                    }

                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        downloadSucceeded = true;
                        publish("Download completed successfully!");
                    } else {
                        publish("Download failed with exit code: " + exitCode);
                    }
                } catch (Exception e) {
                    publish("Error: " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String line : chunks) {
                    System.out.println(line);
                    //txaLog.append(line + "\n");
                    //txaLog.setCaretPosition(txaLog.getDocument().getLength()); // Auto-scroll

                    // Progress Bar Parsing
                    Pattern pattern = Pattern.compile("\\[download\\]\\s+(\\d+\\.\\d+)%");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        try {
                            double percentage = Double.parseDouble(matcher.group(1));
                            progressBar.setValue((int) percentage);
                        } catch (Exception e) {
                        }
                    }
                }
            }

            @Override
            protected void done() {
                btnDownload.setEnabled(true);
                try {
                    get(); // Check for exceptions
                    if (downloadSucceeded) {
                        JOptionPane.showMessageDialog(DownloadPanel.this, "Download Finished!");
                        // Refresh Library
                        // main.refreshLibrary(); // If you expose this method in Main
                    }
                } catch (Exception e) {
                    System.out.println("\nCritical Error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private String chooseResolution(String selected) {
        if (selected == null) {
            return "bestvideo+bestaudio/best";
        }
        if (selected.contains("FullHD")) {
            return "bv*[height<=1080]+ba/b[height<=1080]";
        }
        if (selected.contains("720")) {
            return "bv*[height<=720]+ba/b[height<=720]";
        }
        if (selected.contains("480")) {
            return "bv*[height<=480]+ba/b[height<=480]";
        }
        return "bestvideo+bestaudio/best";
    }
}

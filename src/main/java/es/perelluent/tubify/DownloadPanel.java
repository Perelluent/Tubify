/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.perelluent.tubify;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class DownloadPanel extends JPanel {

    private final String YTDLP_PATH = System.getenv("LOCALAPPDATA") + "\\yt-dlp\\yt-dlp.exe";
    private final String PROPERTIES_PATH = System.getProperty("user.home") + File.separator + "TubifySettings.properties";
    private final Properties props = new Properties();

    private final MainWindow main;
    private JTextField txtUrl;
    private JComboBox<String> cmbResolucion;
    private JCheckBox chkOnlyAudio;
    private JComboBox<String> cmbAudioFormat;
    private JButton btnDownload;
    private JProgressBar progressBar;
    private JLabel lblNotification;

    public DownloadPanel(MainWindow main) {
        this.main = main;

        setLayout(new MigLayout("fill, insets 10", "[grow]", "[]")); //Layout principal
        
        initComponents();
    }

    private void initComponents() {

        JPanel pnlCard = new JPanel(new MigLayout("wrap, insets 20, gapy 10", "[grow, fill]")); // Layout del panel
        pnlCard.putClientProperty("FlatLaf.style", "arc: 20");

        JLabel lblTitle = new JLabel("DOWNLOAD");
        lblTitle.setFont(new Font("Montserrat", Font.BOLD, 18));
        pnlCard.add(lblTitle, "gapbottom 10");

        pnlCard.add(new JLabel("URL:"));
        txtUrl = new JTextField();
        txtUrl.putClientProperty("JTextField.placeholderText", "Paste link here");
        pnlCard.add(txtUrl, "h 35!");

        pnlCard.add(new JLabel("Resolution:"), "gaptop 10");
        cmbResolucion = new JComboBox<>(new String[]{"FullHD (1080p)", "HD (720p)", "SD (480p)", "Best"});
        pnlCard.add(cmbResolucion, "h 30!");

        chkOnlyAudio = new JCheckBox("Only Audio");
        chkOnlyAudio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmbAudioFormat.setEnabled(chkOnlyAudio.isSelected());
            }
        });
        pnlCard.add(chkOnlyAudio);

        cmbAudioFormat = new JComboBox<>(new String[]{"mp3", "m4a", "wav"});
        cmbAudioFormat.setEnabled(false);
        pnlCard.add(cmbAudioFormat, "h 30!");

        btnDownload = new JButton("DOWNLOAD");
        btnDownload.setBackground(Color.decode("#fb3f62"));
        btnDownload.setForeground(Color.WHITE);
        btnDownload.setFont(new Font("Montserrat", Font.BOLD, 12));
        btnDownload.putClientProperty("JButton.buttonType", "roundRect");
        btnDownload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String libraryFolder = main.getLibraryPath();
                String dynamicOutputPath = libraryFolder + File.separator + "%(title)s.%(ext)s";
                downloadVideo(dynamicOutputPath);
            }
        });
        pnlCard.add(btnDownload, "h 45!, gaptop 15");

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        pnlCard.add(progressBar, "h 20!, gaptop 10");
        lblNotification = new JLabel("");
        lblNotification.setFont(new Font("Montserrat", Font.PLAIN, 12));
        lblNotification.setHorizontalAlignment(SwingConstants.CENTER);
        pnlCard.add(lblNotification, "h 20!, gaptop 5");

        add(pnlCard, "grow");
    }

    private void downloadVideo(String outputPath) {

        btnDownload.setEnabled(false);
        progressBar.setValue(0);
        //txaDebug.append("Trying to download " + txtUrl.getText() + "...\n\n");
        //txaDebug.append(YTDLP_PATH);
        SwingWorker<Void, String> worker;
        worker = new SwingWorker<Void, String>() {

            private boolean downloadSucceeded = false;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Replace "yourExecutable.exe" and arguments as needed
                    ProcessBuilder pb = new ProcessBuilder(YTDLP_PATH, txtUrl.getText(), "-o",
                            outputPath
                    );
                    String ytdlpExePath = props.getProperty("ytdlpPath", YTDLP_PATH);
                    String url = txtUrl.getText().trim();
                    //String selectedRes = (String) cmbResolucion.getSelectedItem();

                    List<String> cmd = new ArrayList<>();

                    cmd.add(ytdlpExePath);
                    cmd.add(url);
                    cmd.add("-o");
                    cmd.add(outputPath);
                    cmd.add("--no-playlist");
                    cmd.add("--newline");

                    if (chkOnlyAudio.isSelected()) {
                        String selectedAudioFormat = (String) cmbAudioFormat.getSelectedItem();
                        cmd.add("-x");
                        cmd.add("--audio-format");
                        if ("best".equalsIgnoreCase(selectedAudioFormat)) {
                            cmd.add("mp3");
                        } else {
                            cmd.add(selectedAudioFormat);
                        }

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
            protected void process(List<String> chunks
            ) {
                System.out.println("Process> " + chunks.size() + " lines recieved. In thread " + Thread.currentThread());
                for (String line : chunks) {
                    System.out.println("\t" + line);
                    //txaDownloadResult.append(line + "\n");
                    Pattern pattern = Pattern.compile("\\[download\\]\\s+(\\d+\\.\\d+)%");
                    Matcher matcher = pattern.matcher(line);

                    if (matcher.find()) {
                        try {
                            double percentage = Double.parseDouble(matcher.group(1));
                            int valor = (int) percentage;

                            if (valor >= 0 && valor <= 100) {
                                progressBar.setValue(valor);
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing progress percentage: " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            protected void done() {
                btnDownload.setEnabled(true);
                try {
                    get();

                    if (downloadSucceeded) {
                        progressBar.setValue(100);
                        lblNotification.setText("Download Finished!");

                        if (main.getLibraryPanel() != null) {
                            main.getLibraryPanel().loadMedia();
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error on worker completion: " + e.getMessage());
                }
            }

        };
        worker.execute();
    }

    private String chooseResolution(String selected) {
        if (selected.contains("1080")) {
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

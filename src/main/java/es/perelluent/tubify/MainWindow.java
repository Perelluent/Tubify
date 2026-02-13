/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package es.perelluent.tubify;

import es.perelluent.mediapollingbean.MediaPollingBean;
import es.perelluent.MediaPollingBeanEvent.MediaPollingBeanEvent;
import es.perelluent.MediaPollingBeanEvent.MediaPollingBeanListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import net.miginfocom.swing.MigLayout;

public class MainWindow extends JFrame implements MediaPollingBeanListener {

    private final String YTDLP_PATH = System.getenv("LOCALAPPDATA") + "\\yt-dlp\\yt-dlp.exe";
    private final String PROPERTIES_PATH = System.getProperty("user.home") + File.separator + "TubifySettings.properties";
    private final Properties props = new Properties();

    private final MediaPollingBean mediaPollingBean;
    private String token;

    private final LoginPanel loginPanel;
    private final LibraryPanel libraryPanel;
    private final DownloadPanel downloadPanel;
    private final Preferences preferences;
    private final AboutPanel aboutPanel;

    private JMenuBar menuBar;
    private JMenu mnuFile, mnuEdit, mnuHelp;
    private JMenuItem mniExit, mniPreferences, mniAbout, mniTheme;

    public MainWindow() {
        mediaPollingBean = new MediaPollingBean();
        mediaPollingBean.setApiUrl("https://difreenet9.azurewebsites.net");
        mediaPollingBean.setPollingInterval(30);

        pnlMain = new javax.swing.JPanel();
        initMenu();

        this.getContentPane().setLayout(new BorderLayout());
        this.setMinimumSize(new Dimension(1100, 700));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loginPanel = new LoginPanel(mediaPollingBean, this);
        preferences = new Preferences(this);
        loadPreferences();
        aboutPanel = new AboutPanel(this);
        downloadPanel = new DownloadPanel(this);
        libraryPanel = new LibraryPanel(this);

        showLoginPanel();

        mediaPollingBean.addMediaPollingBeanListener(this);
        loginPanel.checkRememberMe();
    }

    private void initMenu() {
        menuBar = new JMenuBar();

        mnuFile = new JMenu("File");
        mniExit = new JMenuItem("Exit");
        mniExit.addActionListener(e -> {
            savePreferences();
            System.exit(0);
        });
        mnuFile.add(mniExit);

        mnuEdit = new JMenu("Edit");
        mniPreferences = new JMenuItem("Preferences");
        mniPreferences.addActionListener(e -> showPreferences());
        mniTheme = new JMenuItem("Toggle Theme");
        mniTheme.addActionListener(e -> changeTheme());
        mnuEdit.add(mniPreferences);
        mnuEdit.add(mniTheme);

        mnuHelp = new JMenu("Help");
        mniAbout = new JMenuItem("About");
        mniAbout.addActionListener(e -> showAbout());
        mnuHelp.add(mniAbout);

        JLabel lblLogo = new JLabel();
        java.net.URL imageUrl = getClass().getResource("/images/LogoIsotypeTrans.png");
        if (imageUrl != null) {
            lblLogo.setIcon(MainWindow.UpscaleIcon(new ImageIcon(imageUrl), 20, 20));
        }
        menuBar.add(lblLogo);
        menuBar.add(mnuFile);
        menuBar.add(mnuEdit);
        menuBar.add(mnuHelp);
        setJMenuBar(menuBar);
    }

    // Configuramos el Layout en dos columnas
    private void setupMainLayout() {
        pnlMain.removeAll();

        pnlMain.setLayout(new MigLayout("fill, insets 10", "[320!, fill]10[grow, fill]", "[grow]"));

        pnlMain.add(downloadPanel, "growy");
        pnlMain.add(libraryPanel, "growy");
    }

    public void showMainWindow() {
        loginPanel.setVisible(false);
        preferences.setVisible(false);
        aboutPanel.setVisible(false);

        this.getContentPane().removeAll();
        setupMainLayout();
        this.getContentPane().add(pnlMain, BorderLayout.CENTER);

        pnlMain.setVisible(true);
        libraryPanel.loadMedia();

        this.revalidate();
        this.repaint();
    }

    public void showLoginPanel() {
        pnlMain.setVisible(false);
        this.getContentPane().removeAll();
        this.getContentPane().add(loginPanel, BorderLayout.CENTER);
        loginPanel.setVisible(true);
        this.revalidate();
        this.repaint();
    }

    public void showPreferences() {
        pnlMain.setVisible(false);
        this.getContentPane().removeAll();
        this.getContentPane().add(preferences, BorderLayout.CENTER);
        preferences.setVisible(true);
        this.revalidate();
        this.repaint();
    }

    public void showAbout() {
        pnlMain.setVisible(false);
        this.getContentPane().removeAll();
        this.getContentPane().add(aboutPanel, BorderLayout.CENTER);
        aboutPanel.setVisible(true);
        this.revalidate();
        this.repaint();
    }

    public void savePreferences() {
        try (FileOutputStream out = new FileOutputStream(PROPERTIES_PATH)) {
            // para guardar las porpiedades del usuario
            props.setProperty("ytdlpPath", preferences.getYtdlpPath());
            props.setProperty("tempDirPath", preferences.getTempDirPath());
            props.setProperty("speedLimit", preferences.getSelectedSpeedLimit());
            props.setProperty("createM3u", String.valueOf(preferences.isM3uCreationEnabled()));
            props.setProperty("libraryPath", preferences.getLibraryPath());

            props.store(out, "Tubify Application Settings");

            loadPreferences();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    public final void loadPreferences() { // carga las propiedades 
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

    public LibraryPanel getLibraryPanel() {
        return libraryPanel;
    }

    public LoginPanel getLoginPanel() {
        return this.loginPanel;
    }

    public String getLibraryPath() {
        return preferences.getLibraryPath();
    }

    public MediaPollingBean getMediaPollingBean() {
        return mediaPollingBean;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void onNewMediaFound(MediaPollingBeanEvent evt) {
        if (libraryPanel != null) {
            libraryPanel.loadMedia();
        }
    }

    private javax.swing.JPanel pnlMain;

    public static ImageIcon UpscaleIcon(ImageIcon icon, int width, int height) {
        if (icon == null) {
            return null;
        }
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }

    public void changeTheme() { // para cambiar de dark mode a light mode
        try {

            LookAndFeel nextLaf = com.formdev.flatlaf.FlatLaf.isLafDark()
                    ? new com.formdev.flatlaf.FlatLightLaf()
                    : new com.formdev.flatlaf.FlatDarkLaf();

            UIManager.setLookAndFeel(nextLaf);

            SwingUtilities.updateComponentTreeUI(this);

            if (loginPanel != null) {
                SwingUtilities.updateComponentTreeUI(loginPanel);
            }
            if (libraryPanel != null) {
                SwingUtilities.updateComponentTreeUI(libraryPanel);
            }
            if (downloadPanel != null) {
                SwingUtilities.updateComponentTreeUI(downloadPanel);
            }
            if (preferences != null) {
                SwingUtilities.updateComponentTreeUI(preferences);
            }
            if (aboutPanel != null) {
                SwingUtilities.updateComponentTreeUI(aboutPanel);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String args[]) {
        try {
            // Configuramos una nueva fuente
            InputStream is = MainWindow.class.getResourceAsStream("/fonts/Montserrat-Regular.ttf");
            Font montserrat = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(13f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(montserrat);

            // Configurar FlatLaf para que use esa fuente por defecto
            UIManager.put("defaultFont", montserrat);

            com.formdev.flatlaf.FlatDarkLaf.setup();
        } catch (Exception e) {
            System.err.println("No se pudo cargar Montserrat, usando fuente por defecto.");
            com.formdev.flatlaf.FlatDarkLaf.setup();
        }
        Locale.setDefault(Locale.ENGLISH);
        java.awt.EventQueue.invokeLater(() -> new MainWindow().setVisible(true));
    }
}

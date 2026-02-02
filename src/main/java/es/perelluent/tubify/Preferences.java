/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package es.perelluent.tubify;

import com.formdev.flatlaf.ui.FlatLineBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Perelluent
 */
public class Preferences extends JPanel {

    private final MainWindow main;
    
    public Preferences(MainWindow main) {

        this.main = main;
        setLayout(new MigLayout("fill, insets 0", "[center]", "[center]"));
        initComponents();
    }
    
    private void initComponents() {

        JPanel pnlCard = new JPanel(new MigLayout("wrap, insets 50, gapy 15", "[grow, fill][]"));
        pnlCard.setOpaque(true);
        pnlCard.setBackground(UIManager.getColor("EditorPane.background"));
        pnlCard.putClientProperty("FlatLaf.style", "arc: 25");
        pnlCard.setBorder(new CompoundBorder(
            new FlatLineBorder(new Insets(0,0,0,0), Color.GRAY, 1, 25),
            new EmptyBorder(5, 5, 5, 5)
        ));

        lblPreferences = new JLabel("Settings");
        lblPreferences.setFont(new Font("Montserrat", Font.BOLD, 22));
        lblPreferences.setHorizontalAlignment(SwingConstants.CENTER);

        txtDownloadPath = new JTextField();
        txtDownloadPath.putClientProperty("JTextField.placeholderText", "Library folder path...");
        txtDownloadPath.putClientProperty("FlatLaf.style", "arc: 12");

        txtTempDir = new JTextField();
        txtTempDir.putClientProperty("JTextField.placeholderText", "Temp files path...");
        txtTempDir.putClientProperty("FlatLaf.style", "arc: 12");

        txtYtdlpPath = new JTextField();
        txtYtdlpPath.putClientProperty("JTextField.placeholderText", "yt-dlp executable path...");
        txtYtdlpPath.putClientProperty("FlatLaf.style", "arc: 12");

        chkCreateM3u = new JCheckBox("Create .m3u playlist files");
        chkCreateM3u.setOpaque(false);

        radLimitSpeed500 = new JRadioButton("500K");
        radLimitSpeed2M = new JRadioButton("2M");
        radLimitSpeed500.setOpaque(false);
        radLimitSpeed2M.setOpaque(false);
        
        buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(radLimitSpeed500);
        buttonGroup2.add(radLimitSpeed2M);

        btnBrowseFolderPath = new JButton("Browse...");
        btnBrowseTempDir = new JButton("Browse...");
        btnBrowseYtdlpPath = new JButton("Browse...");

        btnBack = new JButton("SAVE & CLOSE");
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.putClientProperty("JButton.buttonType", "roundRect");
        btnBack.setBackground(Color.decode("#c6458f")); // Color rosa del login
        btnBack.setForeground(Color.WHITE);

        pnlCard.add(lblPreferences, "span 2, align center, gapbottom 15");

        pnlCard.add(new JLabel("Download Folder"), "span 2, gapleft 5");
        pnlCard.add(txtDownloadPath, "h 38!");
        pnlCard.add(btnBrowseFolderPath, "h 38!");

        pnlCard.add(new JLabel("Temporary Files"), "span 2, gapleft 5, gaptop 5");
        pnlCard.add(txtTempDir, "h 38!");
        pnlCard.add(btnBrowseTempDir, "h 38!");

        pnlCard.add(new JLabel("YT-DLP Path"), "span 2, gapleft 5, gaptop 5");
        pnlCard.add(txtYtdlpPath, "h 38!");
        pnlCard.add(btnBrowseYtdlpPath, "h 38!");

        pnlCard.add(chkCreateM3u, "span 2, gaptop 5");

        pnlCard.add(new JLabel("Speed Limit:"), "split 3, gapleft 5");
        pnlCard.add(radLimitSpeed500);
        pnlCard.add(radLimitSpeed2M);

        pnlCard.add(btnBack, "span 2, h 45!, gaptop 15, align center");

        add(pnlCard, "width 750!");

        setupListeners();
    }

    private void setupListeners() {
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        btnBrowseFolderPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnBrowseFolderPathActionPerformed(evt);
            }
        });

        btnBrowseTempDir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnBrowseTempDirActionPerformed(evt);
            }
        });

        btnBrowseYtdlpPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnBrowseYtdlpPathActionPerformed(evt);
            }
        });
    }

    private void btnBackActionPerformed(ActionEvent evt) {
        main.savePreferences();
        this.setVisible(false);
        main.showMainWindow();
    }

    private void btnBrowseTempDirActionPerformed(ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtTempDir.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void btnBrowseYtdlpPathActionPerformed(ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtYtdlpPath.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void btnBrowseFolderPathActionPerformed(ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtDownloadPath.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    public String getYtdlpPath() { return txtYtdlpPath.getText(); }
    public void setYtdlpPath(String path) { txtYtdlpPath.setText(path); }
    public String getTempDirPath() { return txtTempDir.getText(); }
    public void setTempDirPath(String path) { txtTempDir.setText(path); }
    public String getLibraryPath() { return txtDownloadPath.getText(); }
    public void setLibraryPath(String path) { txtDownloadPath.setText(path); }
    public boolean isM3uCreationEnabled() { return chkCreateM3u.isSelected(); }
    public void setM3uCreationEnabled(boolean enabled) { chkCreateM3u.setSelected(enabled); }

    public String getSelectedSpeedLimit() {
        if (radLimitSpeed500.isSelected()) return "500K";
        if (radLimitSpeed2M.isSelected()) return "2M";
        return "";
    }

    public void setSelectedSpeedLimit(String limit) {
        if ("500K".equals(limit)) radLimitSpeed500.setSelected(true);
        else if ("2M".equals(limit)) radLimitSpeed2M.setSelected(true);
        else buttonGroup2.clearSelection();
    }

    private JButton btnBack;
    private JButton btnBrowseFolderPath;
    private JButton btnBrowseTempDir;
    private JButton btnBrowseYtdlpPath;
    private ButtonGroup buttonGroup2;
    private JCheckBox chkCreateM3u;
    private JLabel lblPreferences;
    private JRadioButton radLimitSpeed2M;
    private JRadioButton radLimitSpeed500;
    private JTextField txtDownloadPath;
    private JTextField txtTempDir;
    private JTextField txtYtdlpPath;
}

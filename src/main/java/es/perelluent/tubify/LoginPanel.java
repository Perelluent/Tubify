/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.perelluent.tubify;

import com.formdev.flatlaf.ui.FlatLineBorder;
import es.perelluent.mediapollingbean.MediaPollingBean;
import static es.perelluent.tubify.MainWindow.UpscaleIcon;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.URL;
import java.util.prefs.Preferences;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Perelluent
 */
public class LoginPanel extends JPanel {

    private final MainWindow mainWindow;
    private final MediaPollingBean mediaPollingBean;

    private JPanel pnlLogin;
    private JLabel lblUser;
    private JLabel lblPassword;
    private JTextField txtUser;
    private JPasswordField pswPassword;
    private JButton btnLogin;
    private JLabel lblLogo;
    private JCheckBox chkRememberMe;
    private JLabel lblError;

    private final Preferences prefs = Preferences.userNodeForPackage(es.perelluent.tubify.LoginPanel.class);
    private final String prefEmail = "user_email";
    private final String prefToken = "user_token";
    private final String prefRememberMe = "remember_me_check";

    private final URL imageUrl = getClass().getResource("/images/TubifyLogoTransparent.png");

    public LoginPanel(MediaPollingBean mediaPollingBean, MainWindow mainWindow) {

        this.mediaPollingBean = mediaPollingBean;
        this.mainWindow = mainWindow;

        setLayout(new MigLayout("fill, insets 0", "[center]", "[center]"));

        initComponents();
    }

    public void checkRememberMe() {
        boolean remember = prefs.getBoolean(prefRememberMe, false);
        if (remember) {
            String savedEmail = prefs.get(prefEmail, "");
            String savedToken = prefs.get(prefToken, "");

            if (!savedToken.isEmpty()) {
                try {
                    mediaPollingBean.setToken(savedToken);
                    mainWindow.setToken(savedToken);
                    mainWindow.showMainWindow();
                    return;
                } catch (Exception e) {
                    prefs.remove(prefToken);
                    lblError.setText("Session expired. Please login again.");
                    System.out.println("Token validation failed: " + e.getMessage());
                }
            }
            txtUser.setText(savedEmail);
            chkRememberMe.setSelected(true);
        }
    }

    private void initComponents() {

        JPanel pnlForm = new JPanel(new MigLayout("wrap, insets 30, gapy 10", "[grow, fill]"));
        pnlForm.putClientProperty("FlatLaf.style", "arc: 30");
        pnlForm.setBackground(UIManager.getColor("EditorPane.background"));
        pnlForm.setBorder(BorderFactory.createCompoundBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), Color.GRAY, 1, 20),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        lblLogo = new JLabel();
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            lblLogo.setIcon(UpscaleIcon(icon, 280, 120));
        }
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);

        // Campos de texto
        txtUser = new JTextField();
        txtUser.putClientProperty("JTextField.placeholderText", "Email");
        txtUser.putClientProperty("FlatLaf.style", "arc: 15");

        pswPassword = new JPasswordField();
        pswPassword.putClientProperty("JTextField.placeholderText", "Password");
        pswPassword.putClientProperty("FlatLaf.style", "arc: 15");

        // Checkbox
        chkRememberMe = new JCheckBox("Remember Me");
        chkRememberMe.setOpaque(false);

        // Botón Login moderno
        btnLogin = new JButton("LOGIN");
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.putClientProperty("JButton.buttonType", "roundRect");
        // Color corporativo con el logo
        btnLogin.setBackground(Color.decode("#c6458f"));
        btnLogin.setForeground(Color.WHITE);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    btnLoginActionPerformed(e);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        });

        // Etiqueta de error
        lblError = new JLabel("", SwingConstants.CENTER);
        lblError.setForeground(Color.RED);
        lblError.setFont(new Font("Montserrat-Regular", Font.BOLD, 9));

        // Construcción del LoginForm
        pnlForm.add(lblLogo, "align center, gapbottom 20");
        pnlForm.add(new JLabel("User"), "gapleft 5");
        pnlForm.add(txtUser, "h 40!");
        pnlForm.add(new JLabel("Password"), "gapleft 5, gaptop 5");
        pnlForm.add(pswPassword, "h 40!");
        pnlForm.add(chkRememberMe, "left");
        pnlForm.add(btnLogin, "h 45!, gaptop 10");
        pnlForm.add(lblError, "h 20!");

        add(pnlForm, "width 350!");

    }

    private void btnLoginActionPerformed(ActionEvent evt) throws Exception {
        String email = txtUser.getText();
        var password = new String(pswPassword.getPassword());
        lblError.setText("");
        try {
            String token = mediaPollingBean.login(email, password);
            mainWindow.setToken(token);

            if (chkRememberMe.isSelected()) {
                prefs.put(prefEmail, email);
                prefs.put(prefToken, token);
                prefs.putBoolean(prefRememberMe, true);
            } else {
                // Si se desmarca, borramos credenciales
                prefs.remove(prefEmail);
                prefs.remove(prefToken);
                prefs.putBoolean(prefRememberMe, false);
            }

            mainWindow.showMainWindow();
        } catch (Exception e) {
            lblError.setText("Login failed. Please check your username and password.");
            e.printStackTrace();
        }
    }

    public void clearTextAreas() {
        prefs.remove(prefToken);
        prefs.remove(prefEmail);
        txtUser.setText("");
        pswPassword.setText("");
    }
}

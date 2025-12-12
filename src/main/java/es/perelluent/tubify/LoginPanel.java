/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.perelluent.tubify;

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
import javax.swing.border.Border;

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

        initComponents();
        setupLayoutAndAddComponents();
        setupListeners();
        checkRememberMe();
    }

    private void setupLayoutAndAddComponents() {

        setLayout(null);
        this.setPreferredSize(new Dimension(1500, 1000));
        ImageIcon lblLogoIcon = new ImageIcon(imageUrl);
        ImageIcon scaledIcon = UpscaleIcon(lblLogoIcon, 570, 250);
        lblLogo.setIcon(scaledIcon);
        lblLogo.setBounds(450, 100, 570, 250);

        lblUser.setSize(30, 30);
        lblUser.setBounds(737, 400, 300, 25);
        txtUser.setBounds(600, 425, 300, 25);
        lblPassword.setBounds(725, 500, 300, 25);
        pswPassword.setBounds(600, 525, 300, 25);
        btnLogin.setBounds(700, 575, 100, 50);
        chkRememberMe.setBounds(690, 650, 150, 75);
        lblError.setBounds(590, 700, 400, 50);
        lblError.setForeground(Color.red);

        add(lblLogo);
        add(lblUser);
        add(txtUser);
        add(lblPassword);
        add(pswPassword);
        add(btnLogin);
        add(chkRememberMe);
        add(lblError);
    }
    private void setupListeners() {
    btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    btnLoginActionPerformed(e);
                } catch (Exception ex) {
                    System.getLogger(LoginPanel.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            }
        });
        txtUser.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtUser.getText().equals("Email")) {
                    txtUser.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtUser.getText().isEmpty()) {
                    txtUser.setText("Email");
                }
            }
        });
        pswPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (pswPassword.getText().equals("Password")) {
                    pswPassword.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (pswPassword.getText().isEmpty()) {
                    pswPassword.setText("Email");
                }
            }

        });
    }
    private void checkRememberMe() {
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

        pnlLogin = new JPanel();
        pnlLogin.setLayout(null);

        lblUser = new JLabel("User");
        lblPassword = new JLabel("Password");
        txtUser = new JTextField("Email");
        pswPassword = new JPasswordField("Password");
        btnLogin = new JButton("Login");
        lblLogo = new JLabel();
        chkRememberMe = new JCheckBox("Remember me");
        lblError = new JLabel();

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

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.perelluent.tubify;

import static es.perelluent.tubify.MainWindow.UpscaleIcon;
import es.perelluent.tubify.dto.ApiClient;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.URL;
import java.util.prefs.Preferences;
import javax.swing.*;

/**
 *
 * @author Perelluent
 */
public class LoginPanel extends JPanel {

    private final MainWindow mainWindow;
    private final ApiClient apiClient;
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
    private final String prefPassword = "user_password";
    private final String prefRememberMe = "remember_me_check";
    private URL imageUrl = getClass().getResource("/images/TubifyLogoTransparent.png");

    public LoginPanel(ApiClient apiClient, MainWindow mainWindow) {

        this.apiClient = apiClient;
        this.mainWindow = mainWindow;
        initComponents();

        boolean remember = prefs.getBoolean(prefRememberMe, false);
        if (remember) {
            String savedEmail = prefs.get(prefEmail, "");
            txtUser.setText(savedEmail);

            String savedPassword = prefs.get(prefPassword, "");
            pswPassword.setText(savedPassword);

            chkRememberMe.setSelected(true);
        }

        setLayout(null);
        setMinimumSize(new Dimension(900, 900));
        setSize(new Dimension(900, 900));
        ImageIcon lblLogoIcon = new ImageIcon(imageUrl);
        ImageIcon scaledIcon = UpscaleIcon(lblLogoIcon, 570, 250);
        lblLogo.setIcon(scaledIcon);
        lblLogo.setBounds(150, 100, 570, 250);

        lblUser.setSize(30, 30);
        lblUser.setBounds(437, 400, 300, 25);
        txtUser.setBounds(300, 425, 300, 25);
        lblPassword.setBounds(425, 500, 300, 25);
        pswPassword.setBounds(300, 525, 300, 25);
        btnLogin.setBounds(400, 575, 100, 50);
        chkRememberMe.setBounds(390, 650, 150, 75);
        lblError.setBounds(290, 700, 400, 50);
        lblError.setForeground(Color.red);

        add(lblLogo);
        add(lblUser);
        add(txtUser);
        add(lblPassword);
        add(pswPassword);
        add(btnLogin);
        add(chkRememberMe);
        add(lblError);

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
                    String token = apiClient.login(email, password);
                    mainWindow.setToken(token);

                    if (chkRememberMe.isSelected()) {
                        prefs.put(prefEmail, email);
                        prefs.put(prefPassword, password);
                        prefs.putBoolean(prefRememberMe, true);
                    } else {
                        // Si se desmarca, borramos credenciales
                        prefs.remove(prefEmail);
                        prefs.remove(prefPassword);
                        prefs.putBoolean(prefRememberMe, false);
                    }

                    mainWindow.showMainWindow();
                } catch (Exception e) {
                    lblError.setText("Login failed. Please check your username and password.");
                    e.printStackTrace();
                }
            }
        }

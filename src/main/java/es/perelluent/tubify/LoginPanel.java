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
import java.net.URL;
import java.util.prefs.Preferences;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

/**
 *Panel responsible for handling user authentication within the application.
 * 
 * This component provides a modern login interface with support for:
 * <ul>
 *   <li>Email and password input fields</li>
 *   <li>A “Remember Me” option using {@link java.util.prefs.Preferences}</li>
 *   <li>Automatic session restoration when a valid token is stored</li>
 *   <li>Visual error feedback for failed login attempts</li>
 * </ul>
 * 
 * The panel communicates with {@link MediaPollingBean} to authenticate the user
 * and with {@link MainWindow} to update the application state after login.
 * 
 * @author Perelluent
 * @version 1.0
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

    // Local preferences for the remember me check.
    private final Preferences prefs = Preferences.userNodeForPackage(es.perelluent.tubify.LoginPanel.class);
    private final String prefEmail = "user_email";
    private final String prefToken = "user_token";
    private final String prefRememberMe = "remember_me_check";

    private final URL imageUrl = getClass().getResource("/images/logo.png");

    public LoginPanel(MediaPollingBean mediaPollingBean, MainWindow mainWindow) {

        this.mediaPollingBean = mediaPollingBean;
        this.mainWindow = mainWindow;

        setLayout(new MigLayout("fill, insets 0", "[center]", "[center]"));

        initComponents();
    }

    /**
     * Initializes and configures all UI components of the login panel.
     * This includes:
     * <ul>
     *   <li>Logo display</li>
     *   <li>Email and password fields with placeholder styling</li>
     *   <li>“Remember Me” checkbox</li>
     *   <li>Login button with FlatLaf styling</li>
     *   <li>Error label for authentication feedback</li>
     * </ul>
     */
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

        // textfields
        txtUser = new JTextField();
        txtUser.putClientProperty("JTextField.placeholderText", "Email");
        txtUser.putClientProperty("FlatLaf.style", "arc: 15");

        pswPassword = new JPasswordField();
        pswPassword.putClientProperty("JTextField.placeholderText", "Password");
        pswPassword.putClientProperty("FlatLaf.style", "arc: 15");

        // Checkbox
        chkRememberMe = new JCheckBox("Remember Me");
        chkRememberMe.setOpaque(false);

        // Login button
        btnLogin = new JButton("LOGIN");
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.putClientProperty("JButton.buttonType", "roundRect");

        btnLogin.setBackground(Color.decode("#fb3f62"));
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

        // Label error
        lblError = new JLabel("", SwingConstants.CENTER);
        lblError.setForeground(Color.RED);
        lblError.setFont(new Font("Montserrat-Regular", Font.BOLD, 9));

        // add components to the panel
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

    /**
     * Checks whether the user previously selected “Remember Me” and attempts
     * automatic login if a stored token is still valid.
     * If the token is valid, the user is redirected directly to the main window.
     * If the token is invalid or expired, it is removed and the user is prompted
     * to log in again.
     */
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

    /**
     * Handles the login button action. Retrieves the email and password entered 
     * by the user, attempts authentication through {@link MediaPollingBean#login},
     * saves or clears stored credentials depending on the “Remember Me” option,
     * updates the main window with the new authentication token and displays 
     * an error message if authentication fails.
     * 
     * @param evt the action event triggered by the Login button.
     * @throws Exception if an unexpected error occurs during authentication
     */
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

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.perelluent.tubify;

import static es.perelluent.tubify.MainWindow.UpscaleIcon;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author Perelluent
 */
public class LoginPanel extends JPanel {

    private final MainWindow mainWindow;
    private JPanel pnlLogin;
    private JLabel lblUser;
    private JLabel lblPassword;
    private JTextField txtUser;
    private JPasswordField pwfPassword;
    private JButton btnLogin;
    private JLabel lblLogo;
    private JCheckBox chkRememberMe;

    public LoginPanel(MainWindow mainWindow) {

        this.mainWindow = mainWindow;
        initComponents();

        setLayout(null);
        setMinimumSize(new Dimension(900, 900));
        setSize(new Dimension(900, 900));
        ImageIcon lblLogoIcon = new ImageIcon("src\\main\\resources\\images\\TubifyLogoTransparent.png");
        ImageIcon scaledIcon = UpscaleIcon(lblLogoIcon, 570, 250);
        lblLogo.setIcon(scaledIcon);
        lblLogo.setBounds(150, 100, 570, 250);

        lblUser.setSize(30, 30);
        lblUser.setBounds(437, 400, 300, 25);
        txtUser.setBounds(300, 425, 300, 25);
        lblPassword.setBounds(425, 500, 300, 25);
        pwfPassword.setBounds(300, 525, 300, 25);
        btnLogin.setBounds(400, 575, 100, 50);
        chkRememberMe.setBounds(300, 650, 150, 75);

        add(lblLogo);
        add(lblUser);
        add(txtUser);
        add(lblPassword);
        add(pwfPassword);
        add(btnLogin);
        add(chkRememberMe);


        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnLoginActionPerformed(e);
            }
        });

    }

    private void initComponents() {

        pnlLogin = new JPanel();
        pnlLogin.setLayout(null);

        lblUser = new JLabel("User");
        lblPassword = new JLabel("Password");
        txtUser = new JTextField("User");
        pwfPassword = new JPasswordField("Password");
        btnLogin = new JButton("Login");
        lblLogo = new JLabel();
        chkRememberMe = new JCheckBox("Remember me");

    }


    private void btnLoginActionPerformed(ActionEvent evt) {
        mainWindow.showMainWindow();
    }

}

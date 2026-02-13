/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package es.perelluent.tubify;

import com.formdev.flatlaf.ui.FlatLineBorder;
import static es.perelluent.tubify.MainWindow.UpscaleIcon;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Perelluent
 */
public class AboutPanel extends JPanel {
    
    private final MainWindow main;

    public AboutPanel(MainWindow main) {
        this.main = main;
        setLayout(new MigLayout("fill, insets 0", "[center]", "[center]"));
        initComponents();
    }

    private void initComponents() {

        // Layout principal
        JPanel pnlAbout = new JPanel(new MigLayout("wrap, insets 40, gapy 12", "[grow, fill]"));

        pnlAbout.setOpaque(true);
        pnlAbout.putClientProperty("FlatLaf.style", "arc: 25");       
        pnlAbout.setBorder(new CompoundBorder(
            new FlatLineBorder(new Insets(0,0,0,0), Color.GRAY, 1, 25),
            new EmptyBorder(10, 10, 10, 10)
        ));
        //Logo
        JLabel lblLogo = new JLabel();
        URL imageUrl = getClass().getResource("/images/logo.png");
        lblLogo = new JLabel();
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            lblLogo.setIcon(UpscaleIcon(icon, 280, 120));
        }
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblAuthor = new JLabel("Autor: Pere Garcias");
        lblAuthor.setFont(new Font("Montserrat", Font.PLAIN, 16));
        
        JLabel lblCourse = new JLabel("DAM - Desarrollo de Interfaces");
        lblCourse.setFont(new Font("Montserrat", Font.PLAIN, 14));
        lblCourse.setForeground(Color.GRAY);

        JSeparator separator = new JSeparator();

        JLabel lblResourcesTitle = new JLabel("Resources:");
        lblResourcesTitle.setFont(new Font("Montserrat", Font.BOLD, 14));

        JLabel lblResources = new JLabel("<html>• yt-dlp<br>• ffmpeg<br>• FlatLaf<br>• MigLayout</html>");
        lblResources.setFont(new Font("Montserrat", Font.PLAIN, 13));

        JLabel lblRepoTitle = new JLabel("Repository:");
        lblRepoTitle.setFont(new Font("Montserrat", Font.BOLD, 14));

        JTextField txtRepo = new JTextField("https://github.com/Perelluent/Tubify.git");
        txtRepo.setEditable(false);
        txtRepo.setBorder(null);
        txtRepo.setOpaque(false);
        txtRepo.setFont(new Font("Montserrat", Font.PLAIN, 12)); 
        
        // Botón back
        JButton btnBack = new JButton("Back to App");
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.putClientProperty("FlatLaf.style", "arc: 15; background: #fb3f62; foreground: #ffffff");
        
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.showMainWindow();
            }
        });

        // Añadimos complementos al panel.
        pnlAbout.add(lblLogo, "align center, gapbottom 15");
        pnlAbout.add(lblAuthor, "align center");
        pnlAbout.add(lblCourse, "align center, gapbottom 10");
        pnlAbout.add(separator, "growx, gapbottom 10");
        
        pnlAbout.add(lblResourcesTitle, "gapleft 5");
        pnlAbout.add(lblResources, "gapleft 15, gapbottom 10");
        
        pnlAbout.add(lblRepoTitle, "gapleft 5");
        pnlAbout.add(txtRepo, "gapleft 15");
        pnlAbout.add(btnBack, "h 40!, gaptop 20");

        add(pnlAbout, "width 750!");
    }
}

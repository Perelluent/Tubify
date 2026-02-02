/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package es.perelluent.tubify;

import com.formdev.flatlaf.ui.FlatLineBorder;
import static es.perelluent.tubify.MainWindow.UpscaleIcon;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
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
public class AboutPanel extends JPanel {

    public AboutPanel() {
        // Centrado total en la pantalla
        setLayout(new MigLayout("fill, insets 0", "[center]", "[center]"));
        initComponents();
    }

    private void initComponents() {

        JPanel pnlCard = new JPanel(new MigLayout("wrap, insets 40, gapy 12", "[grow, fill]"));

        pnlCard.setOpaque(true);
        pnlCard.setBackground(UIManager.getColor("EditorPane.background"));
        pnlCard.putClientProperty("FlatLaf.style", "arc: 25");
        
        pnlCard.setBorder(new CompoundBorder(
            new FlatLineBorder(new Insets(0,0,0,0), Color.GRAY, 1, 25),
            new EmptyBorder(10, 10, 10, 10)
        ));
        JLabel lblLogo = new JLabel();
        URL imageUrl = getClass().getResource("/images/TubifyLogoTransparent.png");
        lblLogo = new JLabel();
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            lblLogo.setIcon(UpscaleIcon(icon, 280, 120));
        }
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblAuthor = new JLabel("Autor: Pere Garcias");
        lblAuthor.setFont(new Font("Montserrat", Font.PLAIN, 16));
        
        JLabel lblCourse = new JLabel("DAM - Desarrollo de Interfaces");
        lblCourse.setFont(new Font("Montserrat", Font.ITALIC, 14));
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
        txtRepo.setForeground(Color.white); 

        pnlCard.add(lblLogo, "align center, gapbottom 15");
        pnlCard.add(lblAuthor, "align center");
        pnlCard.add(lblCourse, "align center, gapbottom 10");
        pnlCard.add(separator, "growx, gapbottom 10");
        
        pnlCard.add(lblResourcesTitle, "gapleft 5");
        pnlCard.add(lblResources, "gapleft 15, gapbottom 10");
        
        pnlCard.add(lblRepoTitle, "gapleft 5");
        pnlCard.add(txtRepo, "gapleft 15");

        add(pnlCard, "width 750!");
    }
}

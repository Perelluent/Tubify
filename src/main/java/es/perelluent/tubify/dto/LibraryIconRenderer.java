/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.perelluent.tubify.dto;

import es.perelluent.tubify.MainWindow;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Perelluent
 */
public class LibraryIconRenderer extends DefaultTableCellRenderer {

    private Icon audioIcon;
    private Icon videoIcon;
    private Icon cloudIcon;
    private Icon localIcon;
    private Icon syncIcon;

    public LibraryIconRenderer() {

        audioIcon = loadIcon("/images/audio.png", 40, 40);
        videoIcon = loadIcon("/images/play.png", 40, 40);
        cloudIcon = loadIcon("/images/cloud.png", 40, 40);
        localIcon = loadIcon("/images/local.png", 40, 40);
        syncIcon = loadIcon("/images/sync.png", 40, 40);

        setHorizontalAlignment(JLabel.CENTER);
    }

    private Icon loadIcon(String path, int w, int h) {
        java.net.URL imgUrl = getClass().getResource(path);
        if (imgUrl != null) {
            return MainWindow.UpscaleIcon(new ImageIcon(imgUrl), w, h);
        }
        return null;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setText("");

        String val = (value != null) ? value.toString() : "";

        if (column == 0) {
            if (val.toLowerCase().contains("audio") || val.toLowerCase().contains("mp3")) {
                setIcon(audioIcon);
                setToolTipText("Audio File");
            } else {
                setIcon(videoIcon);
                setToolTipText("Video File");
            }
        } 
        else if (column == 1) {
            switch (val) {
                case "Cloud Only":
                    setIcon(cloudIcon);
                    break;
                case "Local Only":
                    setIcon(localIcon);
                    break;
                case "Synchronized":
                    setIcon(syncIcon);
                    break;
                default:
                    setIcon(null);
            }
            setToolTipText(val);
        }

        return this;
    }
}

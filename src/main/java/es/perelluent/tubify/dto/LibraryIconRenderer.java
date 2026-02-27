/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.perelluent.tubify.dto;

import es.perelluent.tubify.MainWindow;
import java.awt.Component;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Custom cell renderer for the Library JTable that replaces text values 
 * with icons that represent, the media type and  the media origin.
 * It enhances readability by showing intuitive icons instead of raw text,
 * and applies tooltips to provide additional context to the user.
 * 
 * @author Perelluent
 * @version 1.0
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

        setHorizontalAlignment(JLabel.CENTER); // Center icons inside table cells
    }

    /**
     * Loads and scales an icon from the given resource path.
     * 
     * @param path where the image is located.
     * @param w desired width
     * @param h desired height
     * @return a scaled image (Icon).
     */
    private Icon loadIcon(String path, int w, int h) {
        URL imgUrl = getClass().getResource(path);
        if (imgUrl != null) {
            return MainWindow.UpscaleIcon(new ImageIcon(imgUrl), w, h);
        }
        return null;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setText(""); //Remove text.
 
        String val = (value != null) ? value.toString() : "";

        if (column == 0) { //Media Type
            if (val.toLowerCase().contains("audio") || val.toLowerCase().contains("mp3")) {
                setIcon(audioIcon);
                setToolTipText("Audio File");
            } else {
                setIcon(videoIcon);
                setToolTipText("Video File");
            }
        } 
        else if (column == 1) { // Media origin
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

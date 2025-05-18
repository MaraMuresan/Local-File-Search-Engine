package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ImageContextWidget extends JPanel {
    public ImageContextWidget(List<String[]> results) {
        setLayout(new BorderLayout());
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(183, 255, 255));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Image Files"));

        int count = 0;
        for (String[] row : results) {
            String path = row[0];
            if (isImage(path)) {
                File file = new File(path);
                JButton btn = new JButton(file.getName());
                btn.setToolTipText(file.getAbsolutePath());
                btn.setAlignmentX(Component.LEFT_ALIGNMENT);
                btn.addActionListener(e -> {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Unable to open image.");
                    }
                });
                content.add(btn);
                content.add(Box.createVerticalStrut(5));
                count++;
                if (count >= 12) break;
            }
        }

        if (count == 0) {
            content.add(new JLabel("No image files found."));
        }

        add(content, BorderLayout.CENTER);
    }

    private boolean isImage(String path) {
        String lower = path.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                || lower.endsWith(".png") || lower.endsWith(".bmp");
    }
}

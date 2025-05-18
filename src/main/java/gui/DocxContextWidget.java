package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class DocxContextWidget extends JPanel {
    public DocxContextWidget(List<String[]> results) {
        setLayout(new BorderLayout());
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(168, 255, 188));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Word Documents"));


        int count = 0;
        for (String[] row : results) {
            String path = row[0];
            if (path.toLowerCase().endsWith(".docx")) {
                File file = new File(path);
                JButton btn = new JButton(file.getName());
                btn.setToolTipText(file.getAbsolutePath());
                btn.setAlignmentX(Component.LEFT_ALIGNMENT);
                btn.addActionListener(e -> {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Cannot open DOCX file.");
                    }
                });
                content.add(btn);
                content.add(Box.createVerticalStrut(5));
                count++;
                if (count >= 12) break;
            }
        }

        if (count == 0) {
            content.add(new JLabel("No DOCX files found."));
        }

        add(content, BorderLayout.CENTER);
    }
}
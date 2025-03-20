package gui;

import javax.swing.*;

public class StatusBox extends JPanel {
    private JLabel statusLabel;

    public StatusBox() {
        statusLabel = new JLabel("Ready");
        add(statusLabel);
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }
}

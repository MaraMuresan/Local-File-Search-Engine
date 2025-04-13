package gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StatusBox extends JPanel {
    private JLabel statusLabel;
    private JLabel historyLabel;

    public StatusBox() {
        setLayout(new GridLayout(2, 1));

        statusLabel = new JLabel("Ready", SwingConstants.CENTER);
        historyLabel = new JLabel("Last searches: ", SwingConstants.CENTER);

        add(statusLabel);
        add(historyLabel);
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    public void setHistory(List<String> history) {
        StringBuilder builder = new StringBuilder("Last searches: ");

        for (int i = 0; i < history.size(); i++) {
            builder.append(history.get(i));
            if (i < history.size() - 1) {
                builder.append(" | ");
            }
        }

        historyLabel.setText(builder.toString());
    }

}

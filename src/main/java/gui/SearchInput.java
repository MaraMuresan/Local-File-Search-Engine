package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SearchInput extends JPanel {
    public SearchInput(SearchControllerClient controllerClient) {
        JTextField input = new JTextField(20);
        JButton button = new JButton("Search");

        button.addActionListener((ActionEvent e) -> {
            controllerClient.sendSearchQuery(input.getText());
        });

        add(input);
        add(button);
    }
}

package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CutWidget extends JPanel {
    public CutWidget() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(280, 100));
        setBackground(new Color(255, 209, 255));
        setBorder(BorderFactory.createTitledBorder("Logic Programming: Cut"));

        JTextArea info = new JTextArea(
                "In Prolog, 'cut' (!) is used to control backtracking and improve efficiency.\n" +
                        "It is a predefined predicate with no arguments, which is evaluated immediately to true."
        );
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setEditable(false);
        info.setBackground(getBackground());

        JButton learnMore = new JButton("Want to learn more about cut in Prolog?");
        learnMore.addActionListener((ActionEvent e) -> {
            try {
                Desktop.getDesktop().browse(new java.net.URI("https://athena.ecs.csus.edu/~mei/logicp/cut-negation.html#:~:text=Cut%20basically%20gives%20order%20to,side)%20or%20on%20prolog%20query."));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        add(info, BorderLayout.CENTER);
        add(learnMore, BorderLayout.SOUTH);
    }
}

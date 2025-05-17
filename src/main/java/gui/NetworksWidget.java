package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class NetworksWidget extends JPanel {
    public NetworksWidget() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(280, 100));
        setBackground(new Color(255, 255, 170));
        setBorder(BorderFactory.createTitledBorder("Cisco Packet Tracer"));

        JTextArea info = new JTextArea(
                "Need to open Computer Networks related files?\n" +
                        "Cisco Packet Tracer is a tool used for learning and practicing Computer Networks."
        );
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setEditable(false);
        info.setBackground(getBackground());

        JButton learnMore = new JButton("Learn more about Cisco Packet Tracer");
        learnMore.addActionListener((ActionEvent e) -> {
            try {
                Desktop.getDesktop().browse(new java.net.URI("https://www.netacad.com/cisco-packet-tracer"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        add(info, BorderLayout.CENTER);
        add(learnMore, BorderLayout.SOUTH);
    }
}
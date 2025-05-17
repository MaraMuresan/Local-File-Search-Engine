package gui;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class WidgetPanel extends JPanel {
    private final Map<String, JPanel> widgets = new HashMap<>();

    public WidgetPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Widgets"));
    }

    public void showWidgetsForQuery(String query) {
        removeAll();
        widgets.clear();

        query = query.toLowerCase();

        if (query.contains("cut")) {
            addWidget("cut", new CutWidget());
        }

        revalidate();
        repaint();
    }

    private void addWidget(String key, JPanel widget) {
        widgets.put(key, widget);
        add(widget);
    }
}

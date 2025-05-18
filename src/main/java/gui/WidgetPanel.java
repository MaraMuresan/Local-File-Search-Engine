package gui;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class WidgetPanel extends JPanel {
    private final Map<String, JPanel> widgets = new HashMap<>();

    public WidgetPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Widgets"));
    }

    public void showWidgets(String query, List<JPanel> widgetsFromResults) {
        removeAll();
        widgets.clear();

        query = query.toLowerCase();

        //widgets from query
        if (query.contains("cut")) {
            addWidget("cut", new CutWidget());
        }
        if (query.contains("networks")) {
            addWidget("networks", new NetworksWidget());
        }

        //widgets from results
        for (JPanel widget : widgetsFromResults) {
            add(widget);
        }

        revalidate();
        repaint();
    }

    private void addWidget(String key, JPanel widget) {
        widgets.put(key, widget);
        add(widget);
    }
}

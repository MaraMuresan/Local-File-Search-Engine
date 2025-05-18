package gui;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class WidgetFactory {
    public static List<JPanel> getContextWidgets(List<String[]> allResults) {
        int imageCount = 0;
        int docxCount = 0;

        for (String[] row : allResults) {
            String extension = row.length > 3 ? row[3].toLowerCase() : "";

            if (isImageExtension(extension)) imageCount++;
            if (extension.equals("docx")) docxCount++;
        }

        List<JPanel> widgets = new ArrayList<>();
        if (imageCount >= 10) widgets.add(new ImageContextWidget(allResults));
        if (docxCount >= 5) widgets.add(new DocxContextWidget(allResults));

        return widgets;
    }

    private static boolean isImageExtension(String ext) {
        return ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("bmp");
    }
}
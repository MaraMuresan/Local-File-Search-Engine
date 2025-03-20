package gui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.List;

public class ResultDisplay extends JPanel {
    private JTextPane resultPane;
    private StyledDocument doc;

    public ResultDisplay() {
        setLayout(new BorderLayout());

        resultPane = new JTextPane();
        resultPane.setEditable(false);
        resultPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultPane.setBackground(Color.WHITE);

        doc = resultPane.getStyledDocument();
        setupStyles(doc);

        JScrollPane scrollPane = new JScrollPane(resultPane);
        scrollPane.setPreferredSize(new Dimension(900, 500));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateResults(List<String[]> nameResults, List<String[]> contentResults, String query) {
        resultPane.setText("");
        try {
            doc.insertString(doc.getLength(), "SEARCHING BY FILE NAME:\n", doc.getStyle("header"));
            if (nameResults.isEmpty()) {
                doc.insertString(doc.getLength(), "  No file name matches found.\n\n", doc.getStyle("error"));
            } else {
                for (String[] file : nameResults) {
                    addFileResult(file[0], file[1], query);
                }
                doc.insertString(doc.getLength(), "\n", null);
            }

            doc.insertString(doc.getLength(), "SEARCHING BY CONTENT:\n", doc.getStyle("header"));
            if (contentResults.isEmpty()) {
                doc.insertString(doc.getLength(), "  No content matches found.\n\n", doc.getStyle("error"));
            } else {
                for (String[] file : contentResults) {
                    addFileResult(file[0], file[1], query);
                }
            }

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void insertHighlightedPreview(String text, String query) throws BadLocationException {
        String lowerText = text.toLowerCase();
        String lowerQuery = query.toLowerCase();
        int index = 0;

        while (index < text.length()) {
            int found = lowerText.indexOf(lowerQuery, index);
            if (found == -1) {
                doc.insertString(doc.getLength(), text.substring(index), doc.getStyle("dim"));
                break;
            }

            if (found > index) {
                doc.insertString(doc.getLength(), text.substring(index, found), doc.getStyle("dim"));
            }

            doc.insertString(doc.getLength(), text.substring(found, found + query.length()), doc.getStyle("highlight"));

            index = found + query.length();
        }
    }


    private void addFileResult(String fileName, String content, String query) throws BadLocationException {
        doc.insertString(doc.getLength(), "  " + fileName + "\n", doc.getStyle("bold"));
        String preview = content.length() > 300 ? content.substring(0, 300) + "..." : content;
        doc.insertString(doc.getLength(), "    > ", doc.getStyle("dim"));
        insertHighlightedPreview(preview, query);
        doc.insertString(doc.getLength(), "\n\n", doc.getStyle("dim"));
    }

    private void setupStyles(StyledDocument doc) {
        Style header = doc.addStyle("header", null);
        StyleConstants.setForeground(header, new Color(0, 102, 204));
        StyleConstants.setBold(header, true);
        StyleConstants.setFontSize(header, 16);

        Style bold = doc.addStyle("bold", null);
        StyleConstants.setBold(bold, true);
        StyleConstants.setFontSize(bold, 14);

        Style dim = doc.addStyle("dim", null);
        StyleConstants.setForeground(dim, Color.DARK_GRAY);
        StyleConstants.setFontSize(dim, 13);

        Style highlight = doc.addStyle("highlight", null);
        StyleConstants.setBackground(highlight, Color.YELLOW);
        StyleConstants.setBold(highlight, true);

        Style error = doc.addStyle("error", null);
        StyleConstants.setForeground(error, Color.RED);
        StyleConstants.setBold(error, true);
    }
}

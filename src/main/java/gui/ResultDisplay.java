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

    public void updateResults(List<String[]> pathResults, List<String[]> contentResults, String highlightQuery) {
        resultPane.setText("");
        try {
            doc.insertString(doc.getLength(), "SEARCHING BY FILE PATH:\n", doc.getStyle("header"));
            if (pathResults.isEmpty()) {
                doc.insertString(doc.getLength(), "  No file path matches found.\n\n", doc.getStyle("error"));
            } else {
                for (String[] file : pathResults) {
                    addFileResult(file[0], file[1], highlightQuery);
                }
                doc.insertString(doc.getLength(), "\n", null);
            }

            doc.insertString(doc.getLength(), "SEARCHING BY CONTENT:\n", doc.getStyle("header"));
            if (contentResults.isEmpty()) {
                doc.insertString(doc.getLength(), "  No content matches found.\n\n", doc.getStyle("error"));
            } else {
                for (String[] file : contentResults) {
                    addFileResult(file[0], file[1], highlightQuery);
                }
            }

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void insertHighlightedPreview(String text, String query) throws BadLocationException {
        String lowerText = text.toLowerCase();
        int current = 0;

        String[] keywords = query.toLowerCase().split("\\s*(?i)AND\\s*");

        while (current < text.length()) {
            int nextMatch = -1;
            int matchLength = 0;
            String matchedKeyword = null;

            for (String keyword : keywords) {
                if (keyword.isBlank()) continue;

                int found = lowerText.indexOf(keyword, current);
                if (found != -1 && (nextMatch == -1 || found < nextMatch)) {
                    nextMatch = found;
                    matchLength = keyword.length();
                    matchedKeyword = keyword;
                }
            }

            if (nextMatch == -1) {
                doc.insertString(doc.getLength(), text.substring(current), doc.getStyle("dim"));
                break;
            }

            if (nextMatch > current) {
                doc.insertString(doc.getLength(), text.substring(current, nextMatch), doc.getStyle("dim"));
            }

            doc.insertString(doc.getLength(),
                    text.substring(nextMatch, nextMatch + matchLength),
                    doc.getStyle("highlight"));

            current = nextMatch + matchLength;
        }
    }



    private void addFileResult(String filePath, String content, String highlightQuery) throws BadLocationException {
        doc.insertString(doc.getLength(), "  " + filePath + "\n", doc.getStyle("bold"));
        String preview = content.length() > 300 ? content.substring(0, 300) + "..." : content;
        doc.insertString(doc.getLength(), "    > ", doc.getStyle("dim"));
        insertHighlightedPreview(preview, highlightQuery);
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

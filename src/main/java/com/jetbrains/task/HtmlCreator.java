package com.jetbrains.task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;

public class HtmlCreator {

    private enum FileType {
        ORIGINAL,
        NEW
    }

    public static String textToHtmlText(String line) {
        StringBuilder htmlText = new StringBuilder();
        boolean lastWasASpace = false;
        for (char c : line.toCharArray()) {
            if (c == ' ') {
                if (lastWasASpace) {
                    htmlText.append("&nbsp;");
                    lastWasASpace = false;
                    continue;
                }
                lastWasASpace = true;
            } else {
                lastWasASpace = false;
            }
            switch (c) {
                case '<': htmlText.append("&lt;"); break;
                case '>': htmlText.append("&gt;"); break;
                case '&': htmlText.append("&amp;"); break;
                case '"': htmlText.append("&quot;"); break;
                case '\n': htmlText.append("<br>"); break;
                case '\t': htmlText.append("&nbsp; &nbsp; &nbsp;"); break;
                default:
                    if (c < 128) {
                        htmlText.append(c);
                    } else {
                        htmlText.append("&#").append((int)c).append(";");
                    }
            }
        }
        return htmlText.toString();
    }

    public static void createDiffHtml(List<String> originalFile, List<String> newFile,
                                   String originalFileName, String newFileName) {
        String html;

        try {
            html = Files.lines(Paths.get("src/main/resources/template.html")).collect(Collectors.joining());
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        html = html.replace("${originalFileName}", originalFileName)
                .replace("${newFileName}", newFileName)
                .replace("${firstFile}", HtmlCreator.getLinesInTags(originalFile, FileType.ORIGINAL))
                .replace("${secondFile}", HtmlCreator.getLinesInTags(newFile, FileType.NEW));

        html = HtmlCreator.diffProcessing(html, new Diff(originalFile, newFile));


        try (PrintStream out = new PrintStream(new FileOutputStream("diff.html"))) {
            out.print(html);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

    }

    private static String diffProcessing(String html, Diff diff) {
        List<Integer> linesToDelete = diff.getLinesToDelete();
        for (Integer lineNumber : linesToDelete) {
            html = html.replace("${default" + FileType.ORIGINAL + lineNumber + '}', "deleted");
        }
        List<Integer> linesToAdd = diff.getLinesToAdd();
        for (Integer lineNumber : linesToAdd) {
            html = html.replace("${default" + FileType.NEW + lineNumber + '}', "added");
        }
        List<AbstractMap.SimpleEntry<List<Integer>, List<Integer>>> linesToChange = diff.getLinesToChange();
        for (var linesPair : linesToChange) {
            for (Integer lineNumber : linesPair.getKey()) {
                html = html.replace("${default" + FileType.ORIGINAL + lineNumber + '}', "changed");
            }
            for (Integer lineNumber : linesPair.getValue()) {
                html = html.replace("${default" + FileType.NEW + lineNumber + '}', "changed");
            }
        }

        return html;
    }

    private static String getLinesInTags(List<String> fileLines, FileType fileType) {
        StringBuilder linesInTags = new StringBuilder();
        String classTemplate = "${default" + fileType;
        for (int i = 1; i <= fileLines.size(); i++) {
            linesInTags.append("<tr><td class=\"line-number\">")
                    .append(i).append("</td><td class=\"")
                    .append(classTemplate)
                    .append(i)
                    .append("}\">")
                    .append(HtmlCreator.textToHtmlText(fileLines.get(i - 1)))
                    .append("</td></tr>\n");
        }
        return linesInTags.toString();
    }
}

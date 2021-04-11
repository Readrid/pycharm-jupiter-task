package com.jetbrains.task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        String originalFileName = args[0];
        String newFileName = args[1];

        try {
            List<String> originalFile = Files.lines(Paths.get(originalFileName)).collect(Collectors.toList());
            List<String> newFile = Files.lines(Paths.get(newFileName)).collect(Collectors.toList());

            HtmlCreator.createDiffHtml(originalFile, newFile, originalFileName, newFileName);

            System.out.println("SUCCESS!");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}

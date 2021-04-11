package com.jetbrains.task;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class Diff {

    private static class Pair {
        public int x;
        public int y;

        Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean equals(int otherX, int otherY) {
            return x == otherX && y == otherY;
        }

        @Override
        public String toString() {
            return "{" + x + ", " + y + "} ";
        }
    }

    private final List<Integer> linesToAdd = new ArrayList<>();
    private final List<Integer> linesToDelete = new ArrayList<>();
    private final List<AbstractMap.SimpleEntry<List<Integer>, List<Integer>>> linesToChange = new ArrayList<>();

    public Diff(List<String> originalFile, List<String> newFile) {

        int originalFileSize = originalFile.size();
        int newFileSize = newFile.size();

        int[][] diffTable = new int[originalFileSize + 1][newFileSize + 1];
        Pair[][] path = new Pair[originalFileSize + 1][newFileSize + 1];

        for (int i = 1; i <= originalFileSize; i++) {
            for (int j = 1; j <= newFileSize; j++) {
                if (originalFile.get(i - 1).equals(newFile.get(j - 1))) {
                    diffTable[i][j] = diffTable[i - 1][j - 1] + 1;
                    path[i][j] = new Pair(i - 1, j - 1);
                } else {
                    if (diffTable[i - 1][j] > diffTable[i][j - 1]) {
                        diffTable[i][j] = diffTable[i - 1][j];
                        path[i][j] = new Pair(i - 1, j);
                    } else {
                        diffTable[i][j] = diffTable[i][j - 1];
                        path[i][j] = new Pair(i, j - 1);
                    }
                }
            }
        }

        for (int i = 1; i <= originalFileSize; i++) {
            path[i][0] = new Pair(i - 1, 0);
        }
        for (int i = 1; i <= newFileSize; i++) {
            path[0][i] = new Pair(0, i - 1);
        }

        List<Pair> diffGraphPath = creatDiffGraphPath(path, originalFileSize, newFileSize);

        findDiffLines(diffGraphPath);

    }

    private List<Pair> creatDiffGraphPath(Pair[][] path, int originalFileSize, int newFileSize) {
        int i = originalFileSize;
        int j = newFileSize;
        List<Pair> diffGraphPath = new ArrayList<>();
        while (i > 0 || j > 0) {
            diffGraphPath.add(new Pair(i, j));
            if (path[i][j].equals(i - 1, j - 1)) {
                i--;
                j--;
            } else if (path[i][j].equals(i - 1, j)) {
                i--;
            } else if (path[i][j].equals(i, j - 1)) {
                j--;
            }
        }
        diffGraphPath.add(new Pair(0, 0));
        return diffGraphPath;
    }

    private void findDiffLines(List<Pair> diffGraphPath) {
        ArrayList<Integer> addHelper = new ArrayList<>();
        ArrayList<Integer> deleteHelper = new ArrayList<>();
        for (int k = diffGraphPath.size() - 1; k > 0; k--) {
            if (diffGraphPath.get(k).x == diffGraphPath.get(k - 1).x
                    && diffGraphPath.get(k).y == diffGraphPath.get(k - 1).y) {
                continue;
            }
            while (k > 0 && diffGraphPath.get(k).x == diffGraphPath.get(k - 1).x - 1 &&
                    diffGraphPath.get(k).y == diffGraphPath.get(k - 1).y) {
                deleteHelper.add(diffGraphPath.get(k - 1).x);
                k--;
            }
            while (k > 0 && diffGraphPath.get(k).y == diffGraphPath.get(k - 1).y - 1 &&
                    diffGraphPath.get(k).x == diffGraphPath.get(k - 1).x) {
                addHelper.add(diffGraphPath.get(k - 1).y);
                k--;
            }

            if (!deleteHelper.isEmpty() && !addHelper.isEmpty()) {
                linesToChange.add(new AbstractMap.SimpleEntry<>(new ArrayList<>(deleteHelper),
                        new ArrayList<>(addHelper)));
            } else {
                linesToDelete.addAll(deleteHelper);
                linesToAdd.addAll(addHelper);
            }

            deleteHelper.clear();
            addHelper.clear();
        }
    }

    public List<Integer> getLinesToAdd() {
        return linesToAdd;
    }

    public List<Integer> getLinesToDelete() {
        return linesToDelete;
    }

    public List<AbstractMap.SimpleEntry<List<Integer>, List<Integer>>> getLinesToChange() {
        return linesToChange;
    }
}

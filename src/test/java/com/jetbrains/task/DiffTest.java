package com.jetbrains.task;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DiffTest {

    private Diff testDiff1;
    private Diff testDiff2;
    private Diff testDiff3;


    private void initTestDiffs() {
        List<String> originals = new ArrayList<>();
        List<String> news = new ArrayList<>();

        originals.add("a");
        originals.add("b");
        originals.add("c");
        originals.add("d");
        originals.add("e");
        originals.add("f");
        originals.add("g");

        news.add("w");
        news.add("a");
        news.add("b");
        news.add("x");
        news.add("y");
        news.add("z");
        news.add("e");

        testDiff1 = new Diff(originals, news);

        originals.clear();
        news.clear();

        originals.add("London is the capital");
        originals.add("of great Britan");
        originals.add("");
        originals.add("Hello, JetBrains!");
        originals.add("this should be deleted");
        originals.add("the end.");

        news.add("London is the capital");
        news.add("of great Britain");
        news.add("");
        news.add("this should be added");
        news.add("Hello, JetBrains!");
        news.add("the end.");

        testDiff2 = new Diff(originals, news);

        originals.clear();
        news.clear();


        for (int i = 0; i < 100; i++) {
            originals.add("a");
            originals.add("b");
            originals.add("c");

            news.add("a");
            news.add("c");
            news.add("b");
        }

        testDiff3 = new Diff(originals, news);



    }

    @Test
    public void linesToAddTest() {
        initTestDiffs();

        Assertions.assertEquals(Collections.singletonList(1), testDiff1.getLinesToAdd());
        Assertions.assertEquals(Collections.singletonList(4), testDiff2.getLinesToAdd());

        List<Integer> correct = new ArrayList<>();
        for (int i = 102; i <= 300; i += 2) {
            correct.add(i);
        }
        Assertions.assertEquals(correct, testDiff3.getLinesToAdd());
    }

    @Test
    public void linesToDeleteTest() {
        initTestDiffs();

        Assertions.assertEquals(Arrays.asList(6, 7), testDiff1.getLinesToDelete());
        Assertions.assertEquals(Collections.singletonList(5), testDiff2.getLinesToDelete());

        List<Integer> correct = new ArrayList<>();
        for (int i = 2; i <= 200; i += 2) {
            correct.add(i);
        }
        Assertions.assertEquals(correct, testDiff3.getLinesToDelete());
    }

    @Test
    public void linesToChange() {
        initTestDiffs();

        AbstractMap.SimpleEntry<List<Integer>, List<Integer>> tmp =
                new AbstractMap.SimpleEntry<>(Arrays.asList(3, 4), Arrays.asList(4, 5, 6));
        Assertions.assertEquals(Collections.singletonList(tmp), testDiff1.getLinesToChange());

        tmp = new AbstractMap.SimpleEntry<>(Collections.singletonList(2), Collections.singletonList(2));
        Assertions.assertEquals(Collections.singletonList(tmp), testDiff2.getLinesToChange());

        Assertions.assertEquals(Collections.emptyList(), testDiff3.getLinesToChange());
    }
}

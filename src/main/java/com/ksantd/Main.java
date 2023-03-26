package com.ksantd;

import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException{
        ExecutorService executorService = Executors.newFixedThreadPool(1000);
        List<Future<Integer>> futureResults = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            futureResults.add(executorService.submit(() -> {
                String route = generateRoute("RLRFR", 100);
                int rightTurns = countRightTurns(route);
                updateFrequencyMap(rightTurns);
                return rightTurns;
            }));
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        printResults();
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static int countRightTurns(String route) {
        int count = 0;
        for (char c : route.toCharArray()) {
            if (c == 'R') {
                count++;
            }
        }
        return count;
    }

    public static synchronized void updateFrequencyMap(int rightTurnsCount) {
        sizeToFreq.put(rightTurnsCount, sizeToFreq.getOrDefault(rightTurnsCount, 0) + 1);
    }

    public static void printResults() {
        Optional<Map.Entry<Integer, Integer>> mostCommonEntry = sizeToFreq.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        mostCommonEntry.ifPresent(entry -> {
            System.out.println("Самое частое количество повторений " + entry.getKey() + " (встретилось " + entry.getValue() + " раз)");
            System.out.println("Другие размеры:");

            sizeToFreq.entrySet().stream()
                    .filter(e -> !e.equals(mostCommonEntry.get()))
                    .forEach(e -> System.out.println("- " + e.getKey() + " (" + e.getValue() + " раз)"));
        });
    }
}
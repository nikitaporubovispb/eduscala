package org.pnp;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Solution4 {
    public static void main(String[] args) {
        class BadStep {
            final int start;
            final int end;
            final int sum;

            public BadStep(int start, int end, int sum) {
                this.start = start;
                this.end = end;
                this.sum = sum;
            }

            public int getStart() {
                return start;
            }

            public int getEnd() {
                return end;
            }

            public int getSum() {
                return sum;
            }

            @Override
            public String toString() {
                return "BadStep{" +
                        "start=" + start +
                        ", end=" + end +
                        ", sum=" + sum +
                        '}';
            }
        }
        List<BadStep> badSteps = new ArrayList<>();

        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int k = sc.nextInt();
        int[] a = new int[n + 1];

        boolean isBad = false;
        int badStart = 0;
        int badSum = 0;

        for (int i = 1; i <= n; i++) {
            int v = sc.nextInt();
            a[i] = v;

            if (isBad) {
                if (v < 0) {
                    badSum += v;
                } else {
                    if ((i - badStart) != 1) {
                        badSteps.add(new BadStep(badStart, i-1, badSum));
                    }
                    badSum = 0;
                    isBad = false;
                }
            } else {
                if (v < 0) {
                    isBad = true;
                    badStart = i;
                    badSum = v;
                }
            }
        }

        if (isBad && (a.length - badStart) != 1) {
            badSteps.add(new BadStep(badStart, a.length-1, badSum - a[a.length -1]));
        }
        Map<Integer, BadStep> startToBadStep = badSteps.stream()
                .sorted(Comparator.comparing(BadStep::getSum))
                .limit(k)
                .collect(Collectors.toMap(BadStep::getStart, Function.identity()));

        int[] dp = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            BadStep badStep = startToBadStep.get(i);
            if (badStep != null) {
                for (int j = i; j < badStep.end; j++, i++) {
                    dp[i] = dp[i - 1];
                    if (j == n-1 && badStep.end == n) {
                        dp[n] += dp[i - 1] + a[n];
                    }
                }
            } else {
                if (i == 1) {
                    dp[i] = a[i];
                } else {
                    int step1prev = dp[i - 1] + a[i];
                    int step2prev = dp[i - 2] + a[i];
                    dp[i] = Math.max(step1prev, step2prev);
                }
            }
        }

        int maxMood = dp[n];
        System.out.println(maxMood);
    }
}


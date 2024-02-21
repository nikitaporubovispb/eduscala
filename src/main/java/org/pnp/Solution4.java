package org.pnp;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Solution4 {
    public static void main(String[] args) {
        record BadStep(int start, int end, int sum) {}
        List<BadStep> badSteps = new ArrayList<>();

        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int k = sc.nextInt();
        int[] a = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            int v = sc.nextInt();
            a[i] = v;
            if (v < 0) {
                int badStart = i;
                int badSum = v;
                i++;
                // метод двух указателей
                // подсчитываем промежутки с плохими шагами
                for (int badStepIter = i; badStepIter <= n; badStepIter++, i++) {
                    v = sc.nextInt();
                    a[i] = v;
                    if (v < 0) {
                        if (badStepIter == n) {  // мальчик должен остановится на последнем шаге, даже если проходит не думая
                            if ((badStepIter - badStart) != 1) {    // один плохой шаг мальчик может сам перепрыгнуть
                                badSteps.add(new BadStep(badStart, i-1, badSum));
                            }
                        } else {
                            badSum += v;
                        }
                    } else {
                        if ((badStepIter - badStart) != 1) {    // один плохой шаг мальчик может сам перепрыгнуть
                            badSteps.add(new BadStep(badStart, i-1, badSum));
                        }
                        break;
                    }
                }
            }
        }

        Map<Integer, BadStep> startToBadStep = badSteps.stream()
                .sorted(Comparator.comparing(BadStep::sum))
                .limit(k)
                .collect(Collectors.toMap(BadStep::start, Function.identity()));

        int[] dp = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            BadStep badStep = startToBadStep.get(i);
            if (badStep != null) {
                for (int j = i; j < badStep.end; j++, i++) {
                    dp[i] = dp[i - 1];
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


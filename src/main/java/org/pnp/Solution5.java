package org.pnp;

import java.util.*;
import java.util.stream.Collectors;

public class Solution5 {

    public static void main(String[] args) {
        /*
        Ваш друг Илья загадал массив A, состоящий из N
        элементов A1 , A2 , A3 , ..., An.

        Вы хотите найти сумму всех элементов массива A,
        то есть A1 + A2 + A3 + ... + An .
        Илья обещал рассказать вам Q фактов про массив.
        i-й факт про массив это сумма чисел на отрезке [li , ri ,
        то есть Al i + Al i +1 + ... + Ar i

        Определите, сможете ли вы найти сумму всех
        элементов массива A, после того, как Илья
        расскажет вам все Q фактов про массив. И в случае,
        если можно, выведите наименьшее количество
        фактов, с помощью которых можно восстановить массив.
       */

    }

    public static Map<Interval, List<Interval>> makeMapOfSubs(List<Interval> intervals) {
        Map<Interval, List<Interval>> result = new HashMap<>();
        List<Interval> temp = new ArrayList<>(intervals);
        temp.sort(Comparator.comparing(Interval::size).reversed());
        for (int i = 0; i < temp.size(); i++) {
            Interval interval = temp.get(i);
            List<Interval> smalls = new ArrayList<>();
            for (int j = i+1; j < temp.size() ; j++) {
                Interval small = temp.get(j);
                if (interval.start <= small.start && small.end <= interval.end) {
                    smalls.add(small);
                }
            }
            result.put(interval, smalls);
        }
        return result;
    }

    public static boolean isDone(int size, List<Interval> intervals) {
        Map<Integer, List<Integer>> intervalMap = intervals.stream()
                .collect(Collectors.groupingBy(Interval::start, Collectors.mapping(Interval::end, Collectors.toList())));
        Queue<Node> frontier = new ArrayDeque<>();
        int nextStart = 1;
        Node cur = new Node(nextStart, intervalMap.get(nextStart));
        while (cur != null) {
            if (cur.children != null) {
                for (Integer child : cur.children) {
                    if (child == size) {
                        return true;
                    }
                    nextStart = child + 1;
                    frontier.add(new Node(nextStart, intervalMap.get(nextStart)));
                }
            }
            cur = frontier.poll();
        }
        return false;
    }

    public record Node(Integer parent, List<Integer> children){}
    public record Interval(int start, int end, int size){}
}

package org.pnp;

import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class Solution2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.next();

        int result = 0;

        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            FindResult resultA = findA(chars, i);
            if (resultA.find) {
                result++;
                i = resultA.i();
            }
        }
        System.out.println(result);
    }

    private record FindResult(boolean find, Integer i) {}

    private static FindResult findA(char[] chars, int start) {
        return find(chars, start, c -> c == 'a', c -> false, Solution2::findB);
    }

    private static FindResult findB(char[] chars, int a) {
        return find(chars, ++a, c -> c == 'b', c -> c == 'a', Solution2::findC);
    }

    private static FindResult findC(char[] chars, int b) {
        return find(chars, ++b, c -> c == 'c', c -> c == 'a', null);
    }

    private static FindResult find(char[] chars, int start,
                                   Predicate<Character> isFind, Predicate<Character> isFall,
                                   BiFunction<char[], Integer, FindResult> nextFind) {
        for (int i = start; i < chars.length; i++) {
            if (isFind.test(chars[i])) {
                if (nextFind != null) {
                    return nextFind.apply(chars, i);
                } else {
                    return new FindResult(true, i);
                }
            } else if (isFall.test(chars[i])) {
                return new FindResult(false, null);
            }
        }
        return new FindResult(false, null);
    }
}


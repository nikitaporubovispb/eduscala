package org.pnp;

import java.util.Scanner;

public class Solution2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.next();

        int result = 0;

        boolean isA = false;
        boolean isB = false;

        for (char c : s.toCharArray()) {
            if (c == 'a') {
                if (isB) {
                    isB = false;
                }
                isA = true;
            }
            else if (c == 'b') {
                if (isA) {
                    isB = true;
                }
            }
            else if (c == 'c') {
                if (isA && !isB) {
                    isA = false;
                }
                if (isA) {
                    isA = false;
                    isB = false;
                    result++;
                }
            }
        }

        System.out.println(result);
    }
}


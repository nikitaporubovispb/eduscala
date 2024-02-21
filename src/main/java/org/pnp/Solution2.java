package org.pnp;

import java.util.Scanner;

public class Solution2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.next();

        int result = 0;

        char[] chars = s.toCharArray();
        aLoop: for (int a = 0; a < chars.length; a++) {
            if (chars[a] == 'a') {
                a++;
                for (int b = a; b < chars.length; b++, a++) {
                    if (chars[b] == 'b') {
                        b++;
                        for (int c = b; c < chars.length; c++, b++, a++) {
                            if (chars[c] == 'c') {
                                result++;
                                continue aLoop;
                            } else if (chars[c] == 'a') {
                                continue aLoop;
                            }
                        }
                    } else if (chars[b] == 'c') {
                        continue aLoop;
                    }
                }
            }
        }

        System.out.println(result);
    }
}


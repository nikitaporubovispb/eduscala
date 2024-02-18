package org.pnp;

import java.util.LinkedList;
import java.util.Scanner;

public class Solution3 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        String s = sc.next();

        LinkedList<Integer> list = new LinkedList<>();
        list.add(0);
        int prevNumIndex = 0;

        for (int i = 0; i < n; i++) {
            if (s.charAt(i) == 'R') {
                prevNumIndex++;
            }
            list.add(prevNumIndex, i + 1);
        }
        list.forEach(i -> System.out.print(i + " "));
    }
}


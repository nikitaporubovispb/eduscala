package org.pnp;

import java.util.*;

public class Solution {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int m = sc.nextInt();

        if (n > 7) {
            System.out.println(n - 7);
        } else {
            System.out.println(m + 7);
        }
    }
}

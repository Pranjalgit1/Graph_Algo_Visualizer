package algorithms;

import java.util.*;

public class DFID {

    static List<List<Integer>> graph;
    static int n;

    public static void make(List<List<Integer>> list) {
        graph = list;
        n = list.size();
    }

    private static int dls(int cur, int goal, int l, int[] vis) {
        if (cur == goal) return cur;
        if (l == 0) return -1; //no node to be traversed

        vis[cur] = 1;

        for (int i : graph.get(cur)) {
            if (vis[i]==0) {
                int res = dls(i, goal, l - 1, vis);
                if (res != -1) return res;
            }
        }

        return -1;
    }

    public static int search(int start, int goal) {
        for (int depth = 0; depth <= n; depth++) {
            int[] vis = new int[n];
            int res = dls(start, goal, depth, vis);
            if (res != -1) {
                System.out.println("Found at depth: " + depth);
                return res;
            }
        }
        return -1;
    }
}

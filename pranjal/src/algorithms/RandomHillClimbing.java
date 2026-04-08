package algorithms;

import java.util.*;

public class RandomHillClimbing {

    static int[] h;
    static int[][] adj;
    static int n;
    static Random r = new Random();

    public static void convert(List<List<Integer>> list, int goal) {
        n = list.size();
        adj = new int[n][n];
        h   = new int[n];

        // adjacency list to matrix
        for (int i = 0; i < n; i++) {
            for (int nb : list.get(i)) {
                adj[i][nb] = 1;
                adj[nb][i] = 1;
            }
        }

        // BFS to calculate heuristic value
        Arrays.fill(h, -1);
        Queue<Integer> q = new LinkedList<>();
        q.add(goal);
        h[goal] = 0;

        while (!q.isEmpty()) {
            int cur = q.poll();
            for (int i = 0; i < n; i++) {
                if (adj[cur][i] == 1 && h[i] == -1) {
                    h[i] = h[cur] + 1;
                    q.add(i);
                }
            }
        }
    }

    public static int search(int goal) {
        int cur = r.nextInt(n);

        for (int step = 0; step < 1000; step++) {
            if (cur == goal) return cur;

            List<Integer> neighbour = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (adj[cur][i] == 1) neighbour.add(i);
            }

            if (neighbour.isEmpty()) break;

            int next = neighbour.get(r.nextInt(neighbour.size()));

            if (h[next] < h[cur]) {
                cur = next;
            } else {
                cur = r.nextInt(n); 
            }
        }

        return cur;
    }
}
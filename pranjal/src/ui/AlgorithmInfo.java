package ui;

import step.StepType;
import java.util.HashMap;
import java.util.Map;

public class AlgorithmInfo {

    private final String name;
    private final String timeComplexity;
    private final String spaceComplexity;
    private final String[] pseudocode;
    private final Map<StepType, Integer> stepToLine;

    private AlgorithmInfo(String name, String time, String space, String[] pseudo, Map<StepType, Integer> mapping) {
        this.name = name;
        this.timeComplexity = time;
        this.spaceComplexity = space;
        this.pseudocode = pseudo;
        this.stepToLine = mapping;
    }

    public String getName() { return name; }
    public String getTimeComplexity() { return timeComplexity; }
    public String getSpaceComplexity() { return spaceComplexity; }
    public String[] getPseudocode() { return pseudocode; }

    public int getHighlightLine(StepType type) {
        return stepToLine.getOrDefault(type, -1);
    }

    public static AlgorithmInfo get(String algorithm) {
        switch (algorithm) {
            case "BFS": return bfs();
            case "DFS": return dfs();
            case "Dijkstra": return dijkstra();
            case "Kruskal": return kruskal();
            case "Prim's": return prims();
            case "Bellman-Ford": return bellmanFord();
            case "Floyd-Warshall": return floydWarshall();
            case "Topo Sort": return topoSort();
            case "TSP": return tsp();
            default: return null;
        }
    }

    private static AlgorithmInfo bfs() {
        String[] pseudo = {
            "BFS(G, source):",
            "  queue ← {source}",
            "  visited[source] ← true",
            "  while queue not empty:",
            "    u ← dequeue()",
            "    for each neighbor v of u:",
            "      if not visited[v]:",
            "        visited[v] ← true",
            "        enqueue(v)"
        };
        Map<StepType, Integer> map = new HashMap<>();
        map.put(StepType.VISIT_NODE, 4);
        map.put(StepType.EXPLORE_EDGE, 5);
        map.put(StepType.ADD_TO_QUEUE, 8);
        map.put(StepType.PROCESS_NODE, 4);
        map.put(StepType.REMOVE_FROM_QUEUE, 4);
        return new AlgorithmInfo("BFS", "O(V + E)", "O(V)", pseudo, map);
    }

    private static AlgorithmInfo dfs() {
        String[] pseudo = {
            "DFS(G, source):",
            "  stack ← {source}",
            "  while stack not empty:",
            "    u ← pop()",
            "    if not visited[u]:",
            "      visited[u] ← true",
            "      for each neighbor v of u:",
            "        push(v)"
        };
        Map<StepType, Integer> map = new HashMap<>();
        map.put(StepType.VISIT_NODE, 5);
        map.put(StepType.EXPLORE_EDGE, 6);
        map.put(StepType.ADD_TO_QUEUE, 7);
        map.put(StepType.PROCESS_NODE, 5);
        return new AlgorithmInfo("DFS", "O(V + E)", "O(V)", pseudo, map);
    }

    private static AlgorithmInfo dijkstra() {
        String[] pseudo = {
            "Dijkstra(G, source):",
            "  dist[source] ← 0",
            "  PQ ← {(0, source)}",
            "  while PQ not empty:",
            "    (d, u) ← extractMin()",
            "    for each edge (u,v,w):",
            "      if d + w < dist[v]:",
            "        dist[v] ← d + w",
            "        insert(PQ, (dist[v], v))"
        };
        Map<StepType, Integer> map = new HashMap<>();
        map.put(StepType.VISIT_NODE, 4);
        map.put(StepType.REMOVE_FROM_QUEUE, 4);
        map.put(StepType.EXPLORE_EDGE, 5);
        map.put(StepType.RELAX_EDGE, 7);
        map.put(StepType.NO_UPDATE, 6);
        map.put(StepType.ADD_TO_QUEUE, 8);
        map.put(StepType.PROCESS_NODE, 4);
        return new AlgorithmInfo("Dijkstra", "O((V+E) log V)", "O(V)", pseudo, map);
    }

    private static AlgorithmInfo kruskal() {
        String[] pseudo = {
            "Kruskal(G):",
            "  sort edges by weight",
            "  MST ← {}",
            "  for each edge (u,v,w):",
            "    if find(u) ≠ find(v):",
            "      MST ← MST ∪ {(u,v)}",
            "      union(u, v)",
            "    else: reject edge"
        };
        Map<StepType, Integer> map = new HashMap<>();
        map.put(StepType.EDGE_CONSIDERED, 3);
        map.put(StepType.EDGE_SELECTED, 5);
        map.put(StepType.EDGE_REJECTED, 7);
        return new AlgorithmInfo("Kruskal", "O(E log E)", "O(V)", pseudo, map);
    }

    private static AlgorithmInfo prims() {
        String[] pseudo = {
            "Prim's(G, source):",
            "  key[source] ← 0",
            "  PQ ← {(0, source)}",
            "  while PQ not empty:",
            "    u ← extractMin()",
            "    inMST[u] ← true",
            "    for each edge (u,v,w):",
            "      if !inMST[v] && w < key[v]:",
            "        key[v] ← w, add to PQ"
        };
        Map<StepType, Integer> map = new HashMap<>();
        map.put(StepType.VISIT_NODE, 4);
        map.put(StepType.EDGE_CONSIDERED, 6);
        map.put(StepType.EDGE_SELECTED, 8);
        map.put(StepType.EDGE_REJECTED, 7);
        map.put(StepType.PROCESS_NODE, 5);
        return new AlgorithmInfo("Prim's", "O((V+E) log V)", "O(V)", pseudo, map);
    }

    private static AlgorithmInfo bellmanFord() {
        String[] pseudo = {
            "BellmanFord(G, source):",
            "  dist[source] ← 0",
            "  repeat V-1 times:",
            "    for each edge (u,v,w):",
            "      if dist[u] + w < dist[v]:",
            "        dist[v] ← dist[u] + w",
            "  for each edge (u,v,w):",
            "    if dist[u]+w < dist[v]:",
            "      → negative cycle!"
        };
        Map<StepType, Integer> map = new HashMap<>();
        map.put(StepType.VISIT_NODE, 3);
        map.put(StepType.EXPLORE_EDGE, 3);
        map.put(StepType.RELAX_EDGE, 5);
        map.put(StepType.NO_UPDATE, 4);
        map.put(StepType.NEGATIVE_CYCLE, 8);
        map.put(StepType.PROCESS_NODE, 3);
        return new AlgorithmInfo("Bellman-Ford", "O(V × E)", "O(V)", pseudo, map);
    }

    private static AlgorithmInfo floydWarshall() {
        String[] pseudo = {
            "FloydWarshall(G):",
            "  dist[][] ← adjacency matrix",
            "  for k = 0 to V-1:",
            "    for i = 0 to V-1:",
            "      for j = 0 to V-1:",
            "        if dist[i][k]+dist[k][j]",
            "           < dist[i][j]:",
            "          dist[i][j] ← ",
            "            dist[i][k]+dist[k][j]"
        };
        Map<StepType, Integer> map = new HashMap<>();
        map.put(StepType.VISIT_NODE, 2);
        map.put(StepType.UPDATE_CELL, 7);
        map.put(StepType.NO_UPDATE, 5);
        map.put(StepType.PROCESS_NODE, 2);
        return new AlgorithmInfo("Floyd-Warshall", "O(V³)", "O(V²)", pseudo, map);
    }

    private static AlgorithmInfo topoSort() {
        String[] pseudo = {
            "TopologicalSort(G):",
            "  for each vertex v:",
            "    if not visited[v]:",
            "      dfs(v)",
            "  dfs(u):",
            "    visited[u] ← true",
            "    for each neighbor v of u:",
            "      if not visited[v]: dfs(v)",
            "    push u to stack"
        };
        Map<StepType, Integer> map = new HashMap<>();
        map.put(StepType.VISIT_NODE, 5);
        map.put(StepType.EXPLORE_EDGE, 6);
        map.put(StepType.PROCESS_NODE, 5);
        map.put(StepType.TOPO_PUSH_STACK, 8);
        return new AlgorithmInfo("Topological Sort", "O(V + E)", "O(V)", pseudo, map);
    }

    private static AlgorithmInfo tsp() {
        String[] pseudo = {
            "TSP-NearestNeighbor(G, src):",
            "  current ← src",
            "  visited[src] ← true",
            "  while unvisited nodes:",
            "    next ← nearest unvisited",
            "    tour ← tour + (cur,next)",
            "    current ← next",
            "  tour ← tour + (cur, src)"
        };
        Map<StepType, Integer> map = new HashMap<>();
        map.put(StepType.VISIT_NODE, 4);
        map.put(StepType.EXPLORE_EDGE, 4);
        map.put(StepType.TSP_TOUR_EDGE, 5);
        map.put(StepType.PROCESS_NODE, 6);
        return new AlgorithmInfo("TSP (Nearest Neighbor)", "O(V²)", "O(V)", pseudo, map);
    }
}

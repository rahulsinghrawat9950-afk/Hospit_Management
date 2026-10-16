package com.smarthospital.m4_flow;

import java.util.*;

/**
 * Module 4 (CO4): Dinic's Algorithm for Larger Hospital-Resource Networks.
 * Constructs level graphs via BFS and pushes blocking flows via DFS with pointer optimization.
 * Optimal time complexity O(V^2 * E) (or O(E * sqrt(V)) for unit networks).
 */
public class DinicAlgorithm {

    public static class DinicResult {
        public final int maxFlow;
        public final List<String> dispatchedRoutes;
        public final int bfsPhases;

        public DinicResult(int maxFlow, List<String> dispatchedRoutes, int bfsPhases) {
            this.maxFlow = maxFlow;
            this.dispatchedRoutes = dispatchedRoutes;
            this.bfsPhases = bfsPhases;
        }
    }

    public static DinicResult computeMaxFlow(FlowNetwork network, int source, int sink) {
        network.resetFlows();
        int n = network.getNodeCount();
        int[] level = new int[n];
        int[] ptr = new int[n];
        int maxFlow = 0;
        int phases = 0;

        while (bfsLevelGraph(network, source, sink, level)) {
            phases++;
            Arrays.fill(ptr, 0);
            while (true) {
                int pushed = dfsBlockingFlow(network, source, sink, Integer.MAX_VALUE, level, ptr);
                if (pushed <= 0) break;
                maxFlow += pushed;
            }
        }

        List<String> routes = new ArrayList<>();
        for (FlowNetwork.Edge e : network.getAllEdges()) {
            if (e.flow > 0) {
                routes.add(String.format("%s -> %s: %d/%d capacity utilized",
                        network.getNodeName(e.from), network.getNodeName(e.to), e.flow, e.capacity));
            }
        }

        return new DinicResult(maxFlow, routes, phases);
    }

    private static boolean bfsLevelGraph(FlowNetwork network, int source, int sink, int[] level) {
        Arrays.fill(level, -1);
        level[source] = 0;
        Queue<Integer> q = new LinkedList<>();
        q.add(source);

        while (!q.isEmpty()) {
            int u = q.poll();
            for (FlowNetwork.Edge edge : network.getEdgesFrom(u)) {
                if (edge.residualCapacity() > 0 && level[edge.to] < 0) {
                    level[edge.to] = level[u] + 1;
                    q.add(edge.to);
                }
            }
        }
        return level[sink] >= 0;
    }

    private static int dfsBlockingFlow(FlowNetwork network, int u, int sink, int flow,
                                       int[] level, int[] ptr) {
        if (u == sink || flow == 0) {
            return flow;
        }

        List<FlowNetwork.Edge> edges = network.getEdgesFrom(u);
        for (; ptr[u] < edges.size(); ptr[u]++) {
            FlowNetwork.Edge edge = edges.get(ptr[u]);
            if (level[edge.to] == level[u] + 1 && edge.residualCapacity() > 0) {
                int tr = dfsBlockingFlow(network, edge.to, sink,
                        Math.min(flow, edge.residualCapacity()), level, ptr);
                if (tr > 0) {
                    edge.augment(tr);
                    return tr;
                }
            }
        }
        return 0;
    }
}

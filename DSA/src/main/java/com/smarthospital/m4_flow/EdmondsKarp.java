package com.smarthospital.m4_flow;

import java.util.*;

/**
 * Module 4 (CO4): Edmonds-Karp Algorithm for Hospital-Resource Networks.
 * Uses Breadth-First Search (BFS) to find shortest augmenting paths in O(V * E^2).
 * Additionally discovers the Minimum Cut identifying critical logistics bottlenecks.
 */
public class EdmondsKarp {

    public static class EdmondsKarpResult {
        public final int maxFlow;
        public final List<String> minCutEdges;
        public final List<String> reachableNodesS;
        public final List<String> unreachableNodesT;
        public final List<String> activeRoutes;

        public EdmondsKarpResult(int maxFlow, List<String> minCutEdges,
                                List<String> reachableNodesS, List<String> unreachableNodesT,
                                List<String> activeRoutes) {
            this.maxFlow = maxFlow;
            this.minCutEdges = minCutEdges;
            this.reachableNodesS = reachableNodesS;
            this.unreachableNodesT = unreachableNodesT;
            this.activeRoutes = activeRoutes;
        }
    }

    public static EdmondsKarpResult computeMaxFlow(FlowNetwork network, int source, int sink) {
        network.resetFlows();
        int n = network.getNodeCount();
        int maxFlow = 0;

        while (true) {
            FlowNetwork.Edge[] parentEdge = new FlowNetwork.Edge[n];
            Queue<Integer> queue = new LinkedList<>();
            queue.add(source);

            while (!queue.isEmpty() && parentEdge[sink] == null) {
                int curr = queue.poll();
                for (FlowNetwork.Edge edge : network.getEdgesFrom(curr)) {
                    if (edge.to != source && parentEdge[edge.to] == null && edge.residualCapacity() > 0) {
                        parentEdge[edge.to] = edge;
                        queue.add(edge.to);
                    }
                }
            }

            if (parentEdge[sink] == null) {
                // No more augmenting path
                break;
            }

            // Find bottleneck capacity along the BFS shortest path
            int bottleneck = Integer.MAX_VALUE;
            for (FlowNetwork.Edge e = parentEdge[sink]; e != null; e = parentEdge[e.from]) {
                bottleneck = Math.min(bottleneck, e.residualCapacity());
            }

            // Augment along the path
            for (FlowNetwork.Edge e = parentEdge[sink]; e != null; e = parentEdge[e.from]) {
                e.augment(bottleneck);
            }

            maxFlow += bottleneck;
        }

        // Compute Minimum Cut using BFS from source in the residual graph
        boolean[] inS = new boolean[n];
        Queue<Integer> qCut = new LinkedList<>();
        qCut.add(source);
        inS[source] = true;

        while (!qCut.isEmpty()) {
            int u = qCut.poll();
            for (FlowNetwork.Edge e : network.getEdgesFrom(u)) {
                if (!inS[e.to] && e.residualCapacity() > 0) {
                    inS[e.to] = true;
                    qCut.add(e.to);
                }
            }
        }

        List<String> setS = new ArrayList<>();
        List<String> setT = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (inS[i]) {
                setS.add(network.getNodeName(i));
            } else {
                setT.add(network.getNodeName(i));
            }
        }

        List<String> cutEdges = new ArrayList<>();
        for (FlowNetwork.Edge e : network.getAllEdges()) {
            if (inS[e.from] && !inS[e.to]) {
                cutEdges.add(String.format("Critical Bottleneck: %s -> %s (Capacity: %d, Flow: %d)",
                        network.getNodeName(e.from), network.getNodeName(e.to), e.capacity, e.flow));
            }
        }

        List<String> activeRoutes = new ArrayList<>();
        for (FlowNetwork.Edge e : network.getAllEdges()) {
            if (e.flow > 0) {
                activeRoutes.add(String.format("%s -> %s: %d/%d units",
                        network.getNodeName(e.from), network.getNodeName(e.to), e.flow, e.capacity));
            }
        }

        return new EdmondsKarpResult(maxFlow, cutEdges, setS, setT, activeRoutes);
    }
}

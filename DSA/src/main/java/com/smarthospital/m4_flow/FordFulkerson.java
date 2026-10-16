package com.smarthospital.m4_flow;

import java.util.*;

/**
 * Module 4 (CO4): Ford-Fulkerson Algorithm for Medical-Resource Distribution.
 * Finds the maximum flow of essential supplies (oxygen cylinders, blood bags, ICU beds)
 * from medical distribution depots to recipient hospital wards.
 */
public class FordFulkerson {

    public static class FlowResult {
        public final int maxFlow;
        public final List<String> augmentingPaths;
        public final List<String> allocatedRoutes;

        public FlowResult(int maxFlow, List<String> augmentingPaths, List<String> allocatedRoutes) {
            this.maxFlow = maxFlow;
            this.augmentingPaths = augmentingPaths;
            this.allocatedRoutes = allocatedRoutes;
        }
    }

    public static FlowResult computeMaxFlow(FlowNetwork network, int source, int sink) {
        network.resetFlows();
        int maxFlow = 0;
        List<String> pathLogs = new ArrayList<>();

        while (true) {
            boolean[] visited = new boolean[network.getNodeCount()];
            List<FlowNetwork.Edge> path = new ArrayList<>();
            int bottleneck = dfsFindAugmentingPath(network, source, sink, visited, Integer.MAX_VALUE, path);

            if (bottleneck <= 0) {
                break;
            }

            maxFlow += bottleneck;
            StringBuilder sb = new StringBuilder();
            sb.append("Path: ");
            for (int i = 0; i < path.size(); i++) {
                FlowNetwork.Edge e = path.get(i);
                if (i == 0) {
                    sb.append(network.getNodeName(e.from));
                }
                sb.append(" -> ").append(network.getNodeName(e.to));
            }
            sb.append(" [Bottleneck: ").append(bottleneck).append(" units]");
            pathLogs.add(sb.toString());
        }

        List<String> routeLogs = new ArrayList<>();
        for (FlowNetwork.Edge e : network.getAllEdges()) {
            if (e.flow > 0) {
                routeLogs.add(String.format("%s -> %s: %d / %d units dispatched",
                        network.getNodeName(e.from),
                        network.getNodeName(e.to),
                        e.flow,
                        e.capacity));
            }
        }

        return new FlowResult(maxFlow, pathLogs, routeLogs);
    }

    private static int dfsFindAugmentingPath(FlowNetwork network, int u, int sink,
                                            boolean[] visited, int currentBottleneck,
                                            List<FlowNetwork.Edge> path) {
        if (u == sink) {
            return currentBottleneck;
        }

        visited[u] = true;
        for (FlowNetwork.Edge edge : network.getEdgesFrom(u)) {
            if (!visited[edge.to] && edge.residualCapacity() > 0) {
                int newBottleneck = Math.min(currentBottleneck, edge.residualCapacity());
                int pushed = dfsFindAugmentingPath(network, edge.to, sink, visited, newBottleneck, path);
                if (pushed > 0) {
                    edge.augment(pushed);
                    path.add(0, edge);
                    return pushed;
                }
            }
        }
        return 0;
    }
}

package com.smarthospital.m4_flow;

import java.util.*;

/**
 * Module 4 (CO4): Flow Network representation for hospital medical-resource distribution.
 */
public class FlowNetwork {

    public static class Edge {
        public final int from;
        public final int to;
        public final int capacity;
        public int flow;
        public Edge residual;

        public Edge(int from, int to, int capacity) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.flow = 0;
        }

        public int residualCapacity() {
            return capacity - flow;
        }

        public void augment(int bottleneck) {
            this.flow += bottleneck;
            if (this.residual != null) {
                this.residual.flow -= bottleneck;
            }
        }

        @Override
        public String toString() {
            return String.format("[%d -> %d | flow: %d/%d]", from, to, flow, capacity);
        }
    }

    private final int nodeCount;
    private final List<List<Edge>> adj;
    private final Map<Integer, String> nodeNames;

    public FlowNetwork(int nodeCount) {
        this.nodeCount = nodeCount;
        this.adj = new ArrayList<>(nodeCount);
        for (int i = 0; i < nodeCount; i++) {
            this.adj.add(new ArrayList<>());
        }
        this.nodeNames = new HashMap<>();
    }

    public void setNodeName(int index, String name) {
        nodeNames.put(index, name);
    }

    public String getNodeName(int index) {
        return nodeNames.getOrDefault(index, "Node-" + index);
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public List<Edge> getEdgesFrom(int u) {
        return adj.get(u);
    }

    public List<Edge> getAllEdges() {
        List<Edge> edges = new ArrayList<>();
        for (List<Edge> list : adj) {
            for (Edge e : list) {
                // only return forward edges with positive capacity
                if (e.capacity > 0) {
                    edges.add(e);
                }
            }
        }
        return edges;
    }

    public Edge addEdge(int from, int to, int capacity) {
        Edge forward = new Edge(from, to, capacity);
        Edge backward = new Edge(to, from, 0);
        forward.residual = backward;
        backward.residual = forward;
        adj.get(from).add(forward);
        adj.get(to).add(backward);
        return forward;
    }

    public void resetFlows() {
        for (List<Edge> list : adj) {
            for (Edge e : list) {
                e.flow = 0;
            }
        }
    }
}

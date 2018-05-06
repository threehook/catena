package org.threehook.catena.networking.cluster;

import com.google.common.collect.ImmutableMap;
import io.scalecube.cluster.Cluster;
import io.scalecube.cluster.ClusterConfig;
import io.scalecube.transport.TransportConfig;

import java.util.List;
import java.util.Map;

public class ClusterNetwork {

    private final static String NAME = "name";

    private Cluster cluster;

    private ClusterNetwork(Cluster cluster) {
        this.cluster = cluster;
    }

    public static class Builder {
        private String nodeId;
        private String listenerAddress;
        private int localPort;
        private List<ClusterNode> clusterNodes;

        public Builder setNodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public Builder setListenerAddress(String listenerAddress) {
            this.listenerAddress = listenerAddress;
            return this;
        }

        public Builder setLocalPort(int localPort) {
            this.localPort = localPort;
            return this;
        }

        public Builder setClusterNodes(List<ClusterNode> clusterNodes) {
            this.clusterNodes = clusterNodes;
            return this;
        }

        public Builder addClusterNode(ClusterNode clusterNode) {
            this.clusterNodes.add(clusterNode);
            return this;
        }

        public ClusterNetwork build() {
            Cluster cluster = null;
            TransportConfig transportConfig = TransportConfig.builder().listenAddress(listenerAddress).port(localPort).build();
            Map<String, String> metadata = ImmutableMap.of(NAME, nodeId);
            ClusterConfig config = ClusterConfig.builder().transportConfig(transportConfig).metadata(metadata).build();
            cluster = Cluster.joinAwait(config);

            // Join known hosts to cluster network
            for (ClusterNode clusterNode : clusterNodes) {
                metadata = ImmutableMap.of(NAME, clusterNode.getName());
                config = ClusterConfig.builder().transportConfig(transportConfig).seedMembers(cluster.address()).metadata(metadata).build();
                Cluster.joinAwait(config);
            }
            return new ClusterNetwork(cluster);
        }
    }


    public Cluster getCluster() {
        return cluster;
    }

}

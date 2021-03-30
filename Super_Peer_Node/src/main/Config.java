package main;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Config {

    private int superPeerNodePort;
    private String superPeerNodeIp;
    /* */
    private ConcurrentHashMap<String, SuperNode> superPeerNodesConnections;
    private ConcurrentHashMap<String, PeerNode> peerNodesConnections;


    public Config(String superPeerNodeIp, int superPeerNodePort, ConcurrentHashMap<String, SuperNode> superPeerNodesConnections, ConcurrentHashMap<String, PeerNode> peerNodesConnections) {
        this.superPeerNodeIp=superPeerNodeIp;
        this.superPeerNodePort=superPeerNodePort;
        this.superPeerNodesConnections=superPeerNodesConnections;
        this.peerNodesConnections=peerNodesConnections;
    }

    public int getSuperPeerNodePort() {
        return superPeerNodePort;
    }

    public String getSuperPeerNodeIp() {
        return superPeerNodeIp;
    }

    public ConcurrentHashMap<String, SuperNode> getSuperPeerNodesConnections() {
        return superPeerNodesConnections;
    }

    public ConcurrentHashMap<String, PeerNode> getPeerNodesConnections() {
        return peerNodesConnections;
    }
}

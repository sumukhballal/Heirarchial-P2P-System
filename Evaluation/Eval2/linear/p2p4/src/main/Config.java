package main;

public class Config {
    private String id;
    private int peerNodePort;

    public int getSuperPeerNodePort() {
        return superPeerNodePort;
    }

    public String getSuperPeerIp() {
        return superPeerIp;
    }

    private int superPeerNodePort;
    private String superPeerIp;
    private String peerNodeIp;
    private String hostFilePath;

    public Config(int peerNodePort,  String peerNodeIp, int superPeerNodePort, String superPeerIp, String hostFilePath) {
        this.peerNodePort = peerNodePort;
        this.superPeerNodePort = superPeerNodePort;
        this.superPeerIp = superPeerIp;
        this.peerNodeIp = peerNodeIp;
        this.hostFilePath = hostFilePath;
        this.id=peerNodeIp+":"+peerNodePort;
    }


    public String getId() {
        return id;
    }

    public int getPeerNodePort() {
        return peerNodePort;
    }


    public String getPeerNodeIp() {
        return peerNodeIp;
    }

    public String getHostFilePath() {
        return hostFilePath;
    }
}

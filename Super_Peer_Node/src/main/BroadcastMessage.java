package main;

import java.io.Serializable;

public class BroadcastMessage implements Serializable {
    String id;
    String fileName;
    int TTL;
    String hostId;

    public BroadcastMessage(String id, String fileName, int TTL, String hostId) {
        this.id = id;
        this.fileName = fileName;
        this.TTL = TTL;
        this.hostId=hostId;
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public int getTTL() {
        return TTL;
    }

    public String getHostId() {
        return hostId;
    }

    public void setTTL(int TTL) {
        this.TTL = TTL;
    }
}

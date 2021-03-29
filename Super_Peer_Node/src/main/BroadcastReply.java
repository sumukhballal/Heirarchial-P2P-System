package main;

public class BroadcastReply {
    String id;
    String fileName;
    String peerNodeWithFileId;

    public BroadcastReply(String id, String fileName, String peerNodeWithFileId) {
        this.id = id;
        this.fileName = fileName;
        this.peerNodeWithFileId = peerNodeWithFileId;
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPeerNodeWithFileId() {
        return peerNodeWithFileId;
    }

    public void setPeerNodeWithFileId(String peerNodeWithFileId) {
        this.peerNodeWithFileId = peerNodeWithFileId;
    }
}

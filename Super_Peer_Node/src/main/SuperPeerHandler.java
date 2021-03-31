package main;

import javax.swing.text.AbstractDocument;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SuperPeerHandler extends Thread {

    Socket socket;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    String superPeerId;
    String superPeerIp;
    int superPeerNodePort;
    Logger logger;
    Config config;
    ConcurrentHashMap<String, BroadcastMessage> broadcastMessageConcurrentHashMap;
    volatile HashMap<String, BroadcastReply> broadcastReplyHashMap;

    SuperPeerHandler(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream,
                     ConcurrentHashMap<String, BroadcastMessage> broadcastMessageConcurrentHashMap,
                     HashMap<String, BroadcastReply> broadcastReplyHashMap,
                     Config config, Logger logger) {
        this.socket = socket;
        this.logger = logger;
        this.config = config;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
        this.broadcastMessageConcurrentHashMap = broadcastMessageConcurrentHashMap;
        this.broadcastReplyHashMap = broadcastReplyHashMap;
        setSuperPeerId();
    }


    @Override
    public void run() {
            try {
                while (true) {
                    String command = dataInputStream.readUTF();

                    logger.serverLog("A command for " + command + " has come in from Super Peer with ID: " + superPeerId);

                    switch (command) {
                        case "register_supernode":
                            register();
                            break;
                        case "broadcast_request":
                            broadcastRequest();
                            break;
                        case "broadcast_reply":
                            broadcastReply();
                            break;
                    }
                }
            }  catch (IOException e) {
                e.printStackTrace();
            } finally {
                exit();
            }

    }

    private void exit() {
        logger.serverLog("Exiting Super peer with ID "+superPeerId);

        try {
            dataInputStream.close();
            dataOutputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Check if file is there on server else broadcast to other nodes */
    private void broadcastRequest() {

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            BroadcastMessage broadcastMessage = (BroadcastMessage) objectInputStream.readObject();

            /* Store the broadcast Message */
            String broadcastMessageId=broadcastMessage.getId();
            logger.serverLog("A broadcast message has come in with id : "+broadcastMessageId);
            /* If TTL is 0 : Ignore */
            if(broadcastMessage.getTTL()!=0) {

//                if (broadcastMessageConcurrentHashMap.containsKey(broadcastMessageId)) {
//                    /* Reduce TTL by 1 or minimum */
//                    broadcastMessage.setTTL(broadcastMessageConcurrentHashMap.get(broadcastMessageId).getTTL() - 1);
//                    broadcastMessageConcurrentHashMap.put(broadcastMessage.getId(), broadcastMessage);
//                }


                /* First check if file resides on other peer nodes on the same list */
                String fileName = broadcastMessage.getFileName();
                boolean foundFile = false;

                logger.serverLog("Checking if this file exists on this SuperNode! ");
                for (Map.Entry node : config.getPeerNodesConnections().entrySet()) {

                    PeerNode currentPeerNode = (PeerNode) node.getValue();

                    if (currentPeerNode.files.contains(fileName)) {
                        logger.serverLog("File has been found on this supernode! "+ fileName);
                        broadcastReplyWrite();
                        BroadcastReply broadcastReply = new BroadcastReply(broadcastMessageId, fileName, currentPeerNode.getId());
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream.writeObject(broadcastReply);
                        foundFile = true;
                        break;
                    }
                }

                /* Reduce TTL and store the broadcast message */
                broadcastMessage.setTTL(broadcastMessage.getTTL() - 1);
                broadcastMessageConcurrentHashMap.put(broadcastMessage.getId(), broadcastMessage);

                /* If not found on their peer nodes : send a broadcast request to all other Super Peers */
                if (!foundFile) {
                    logger.serverLog("File for broadcast ID "+broadcastMessageId+" not found! Hence broadcasting to other super nodes!");
                    for (String key : config.getSuperPeerNodesConnections().keySet()) {
                        SuperNode superNode = config.getSuperPeerNodesConnections().get(key);
                        broadcastRequestWrite(superNode.getDataOutputStream());
                        broadcastRequestWriteObject(new ObjectOutputStream(superNode.getSocket().getOutputStream()), broadcastMessage);
                    }
                }
            } else {
                logger.serverLog("TTL For this broadcast message is 0! Ignoring! ");
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /* Get the broadcast reply from other super nodes */
    private void broadcastReply() {

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            BroadcastReply broadcastReply = (BroadcastReply) objectInputStream.readObject();
            String broadcastReplyId=broadcastReply.getId();

            logger.serverLog("A broadcast reply has come in for message ID: "+broadcastReplyId+" If a client still wants this, it is now available!");

            /* If reply id has not been seen before  add it to concurrent hash map - so the client can see it*/
            if(!broadcastReplyHashMap.containsKey(broadcastReplyId)) {
                logger.serverLog("The broadcast reply for message id "+broadcastReplyId+" is now stored in hashmap! ");
                broadcastReplyHashMap.put(broadcastReplyId, broadcastReply);
            } else {
                logger.serverLog("The broadcast reply for message ID: "+broadcastReplyId+" has been seen before! Ignoring! ");
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void broadcastRequestWrite(DataOutputStream outputStream) {
        try {
//            outputStream.writeUTF("superpeer_request");
//            outputStream.writeUTF(Integer.toString(superPeerNodePort));
            outputStream.writeUTF("broadcast_request");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcastRequestWriteObject(ObjectOutputStream outputStream, BroadcastMessage broadcastMessage) {
        try {
            outputStream.writeObject(broadcastMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcastReplyWrite() {
        try {
//            dataOutputStream.writeUTF("superpeer_request");
//            dataOutputStream.writeUTF(Integer.toString(superPeerNodePort));
            dataOutputStream.writeUTF("broadcast_reply");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void register() throws IOException {

        logger.serverLog("Checking if SuperNode "+superPeerId+" has been registered previously!");
        if(isRegistered()) {
            logger.serverLog("SuperNode "+superPeerId+" has been registered already!");
            done();
            return;
        }

        SuperNode superNode = new SuperNode(superPeerId, socket, dataOutputStream, dataInputStream, superPeerIp, superPeerNodePort);
        config.getSuperPeerNodesConnections().put(superPeerId, superNode);
        logger.serverLog("SuperNode "+superPeerId+" has been registered! ");
        done();
        dataOutputStream.writeUTF(Integer.toString(config.getSuperPeerNodePort()));
    }

    /* A helper function which lets the client know it can start writing new data to the this thread */
    private void done() {
        try {
            dataOutputStream.writeUTF("done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* A helper function which lets the client know its request was not processed */
    private void error() {
        try {
            dataOutputStream.writeUTF("error");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* A helper function which checks if client has been registered */
    private boolean isRegistered() {

        if(config.getSuperPeerNodesConnections().containsKey(superPeerId))
            return true;

        return false;
    }

    private void setSuperPeerId() {
        try {
            String portNumber = dataInputStream.readUTF();
            superPeerIp = socket.getRemoteSocketAddress().toString();
            superPeerNodePort = Integer.parseInt(portNumber);

            if (superPeerIp.charAt(0) == '/') {
                superPeerIp = superPeerIp.substring(1);
            }
            superPeerIp=superPeerIp.split(":")[0];

            superPeerId = superPeerIp+":"+superPeerNodePort;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getSuperPeerId() {
        return superPeerId;
    }
}

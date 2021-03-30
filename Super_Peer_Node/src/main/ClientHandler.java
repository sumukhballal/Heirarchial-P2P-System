package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ClientHandler extends  Thread {

    final int THRESHOLD_TO_WAIT_TILL_REPLY=5;
    Socket socket;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    String clientIpAddress;
    int clientPortNumber;
    String clientId;
    Logger logger;
    Config config;
    volatile HashMap<String, BroadcastReply> broadcastReplyHashMap;

    ClientHandler(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream, HashMap<String, BroadcastReply> broadcastReplyHashMap,
                  Config config, Logger logger) {
        this.socket=socket;
        this.logger=logger;
        this.config=config;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
        this.broadcastReplyHashMap=broadcastReplyHashMap;
        setClientId();
    }

    /* Commands supported
     *  register
     *  unregister
     *  update file
     *  query
     *  exit
     * */

    @Override
    public void run() {

        try {
        while(true) {

            String command=dataInputStream.readUTF();
            logger.serverLog("Connected to client with ID: "+getClientId()+" !");
            logger.serverLog("Received command "+command+" from client with ID: "+getClientId());

            switch (command) {

                case "register":
                    register();
                    break;
                case "unregister":
                    unregister();
                    break;
                case "update_add":
                    updateFileAdd();
                    break;
                case "update_delete":
                    updateFileDelete();
                    break;
                case "query":
                    query();
                    break;
                case "exit":
                    exit();
                    break;
                default:
                    logger.serverLog("This option is not available! Exiting! ");
                    exit();
                    break;
            }
        }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            unregister();
            exit();
        }
    }

    private void exit() {
        logger.serverLog("Exiting client with ID "+clientId);

        try {
            dataInputStream.close();
            dataOutputStream.close();
            socket.close();
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateFileAdd() {
        logger.serverLog("Adding a new file to  client " + clientId + "! ");

        if(isRegistered()) {
            done();
        }
        else {
            logger.serverLog("Client with ID: "+clientId+" has not been registered before! ");
            error();
        }

        try {
            /* Blocking call to read from all files from client comma separated */
            String fileNames=dataInputStream.readUTF();
            PeerNode currentPeerNode=config.getPeerNodesConnections().get(clientId);
            boolean result= currentPeerNode.addFiles(fileNames.trim());
            if(result) {
                logger.serverLog("Added all files to client with ID: "+clientId);
                done();
            } else {
                logger.serverLog("Unable to add files to client with ID: "+clientId);
                error();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateFileDelete() {
        logger.serverLog("Deleting a file to client " + clientId + "! ");

        if(isRegistered()) {
            done();
        }
        else {
            logger.serverLog("Client with ID: "+clientId+" has not been registered before! ");
            error();
        }

        try {
            /* Blocking call to read from all files from client comma separated */
            String fileNames=dataInputStream.readUTF();
            PeerNode currentPeerNode=config.getPeerNodesConnections().get(clientId);
            boolean result= currentPeerNode.deleteFiles(fileNames.trim());
            if(result) {
                logger.serverLog("Deleted all files to client with ID: "+clientId);
                done();
            } else {
                logger.serverLog("Unable to delete files to client with ID: "+clientId);
                error();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void query() {
        logger.serverLog("Querying for a file name on all Super Nodes!");

        if(isRegistered()) {
            done();
        }
        else {
            logger.serverLog("Client with ID: "+clientId+" has not been registered before! ");
            error();
            return;
        }

        try {
            /* Blocking call to read  client which file needs to be checked */
            String uniqueMessageId=dataInputStream.readUTF();
            String fileName=dataInputStream.readUTF();
            List<String> peerNodesWithFile=new ArrayList<>();

            /* First check if file resides on other peer nodes on the same list */

            for(Map.Entry node : config.getPeerNodesConnections().entrySet()) {
                PeerNode currentPeerNode = (PeerNode) node.getValue();

                if (currentPeerNode.files.contains(fileName) && !currentPeerNode.id.equals(clientId)) {
                    queryHit();
                    peerNodesWithFile.add(currentPeerNode.getId());
                }
            }

            /* Already found in local peer nodes - send nodes back */
            if(peerNodesWithFile.size()!=0) {
                logger.serverLog("Found in local peer nodes! Not broadcasting request! ");
                dataOutputStream.writeUTF(peerNodesWithFile.toString());
            } else {
                /* Broadcast message to all super   nodes */
                List<String> peerNodesWithFileFromBroadCast=broadcastMessage(fileName, uniqueMessageId);
                /* If size is 0 - we have a query miss */
                if(peerNodesWithFileFromBroadCast.size()==0)
                    queryMiss();
                else
                    dataOutputStream.writeUTF(peerNodesWithFileFromBroadCast.toString());
            }
            done();
        } catch (IOException e) {
            e.printStackTrace();
            error();
        }
    }

    /* Helper function to broadcast message to other super Nodes */
    private List<String> broadcastMessage(String fileName, String broadcastId) {

        /* Get super nodes hashmap */
        ConcurrentHashMap<String, SuperNode> superNodes = config.getSuperPeerNodesConnections();
        /* Result List */
        List<String> result = new ArrayList<>();

        try {
            /* Go one by one over and broadcast the message to all super nodes */
            for (Map.Entry superNodeEntry : superNodes.entrySet()) {
                SuperNode superNode = (SuperNode) superNodeEntry.getValue();
                logger.serverLog("Broadcasting request for  "+fileName+" to "+superNode.getId());
                /* Type of request */
                //superNode.getDataOutputStream().writeUTF("superpeer_request");
                /* Send port number */
                //superNode.getDataOutputStream().writeUTF(Integer.toString(superNode.getPortNumber()));
                /* Send action */

                if(superNode.getSocket()==null) {
                    logger.serverLog("The socket is null for this! ");
                    return result;
                }
                DataOutputStream out = new DataOutputStream(superNode.getSocket().getOutputStream());
                out.writeUTF("broadcast_request");
                logger.serverLog("Just wrote broadcast request to super node with id "+superNode.getId());
                /* Create Broadcast Request */
                BroadcastMessage broadcastMessage = new BroadcastMessage(broadcastId, fileName, 3, clientId);
                /* Object output stream */
                new ObjectOutputStream(superNode.getSocket().getOutputStream()).writeObject(broadcastMessage);
            }


            /* Check periodically to get to see if response has come in */
            int i = 0;
            /* It will wait a maximum of 5 minutes */
            while (i < THRESHOLD_TO_WAIT_TILL_REPLY) {

               if(broadcastReplyHashMap.containsKey(broadcastId)) {
                   /* We have got a response */
                   logger.serverLog("We have got a response for a files location! ");
                   BroadcastReply reply = broadcastReplyHashMap.get(broadcastId);
                   result.add(reply.getPeerNodeWithFileId());
                   break;
               }

                logger.serverLog("Broadcast reply not yet received! Retrying again in 1 minute! ");
                TimeUnit.MINUTES.sleep(1);
                i++;
            }

            /* Return empty List if threshold of wait is over */

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    /* A helper function which lets the client know that something has been found */
    private void queryHit() {
        try {
            dataOutputStream.writeUTF("query_hit");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* A helper function which lets the client know that something has been found */
    private void queryMiss() {
        try {
            dataOutputStream.writeUTF("query_miss");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void register() {

        logger.serverLog("Checking if client "+clientId+" has been registered previously!");
        if(isRegistered()) {
            logger.serverLog("Client "+clientId+" has been registered already!");
            done();
            return;
        }

        PeerNode peerNode = new PeerNode(clientId, clientIpAddress, clientPortNumber, logger);
        config.getPeerNodesConnections().put(clientId, peerNode);
        logger.serverLog("Client "+clientId+" has been registered! ");
        done();
    }

    private void unregister() {
        logger.serverLog("Checking if client "+clientId+" has been registered previously!");

        if(isRegistered()) {
            config.getPeerNodesConnections().remove(clientId);
            logger.serverLog("Client "+clientId+" has been unregistered!");
            return;
        }

        logger.serverLog("Client "+clientId+" was not registered previously! ");
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

        if(config.getPeerNodesConnections().containsKey(clientId))
            return true;

        return false;
    }

    private void setClientId() {
        try {
            String portNumber = dataInputStream.readUTF();
            clientIpAddress = socket.getRemoteSocketAddress().toString();
            clientPortNumber = Integer.parseInt(portNumber);

            if (clientIpAddress.charAt(0) == '/') {
                clientIpAddress = clientIpAddress.substring(1);
            }
            clientIpAddress=clientIpAddress.split(":")[0];

            clientId = clientIpAddress+":"+clientPortNumber;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getClientId() {
        return clientId;
    }
}

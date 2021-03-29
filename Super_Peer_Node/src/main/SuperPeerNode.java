package main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class SuperPeerNode {


    static main.Logger logger;

    enum clientType {
        superpeer,
        peer
    }

    public static void main(String[] args) {

        /* Start the Super Peer Node and perform all intial duties */

        /* Initial Duties
        1.) Read config properties
        2.) Listen to Socket Connections.
        3.) Accept and send it to Client Handler.
        * */


        /* Read config file */

        SuperPeerNode superPeerNode = new SuperPeerNode();
        Config config=superPeerNode.readConfigFile();
        superPeerNode.createLogFile();

        /* Create the broadcast messages hashmap Output and Input */
        ConcurrentHashMap<String, BroadcastMessage> broadcastMessageConcurrentHashMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, BroadcastReply> broadcastReplyConcurrentHashMap = new ConcurrentHashMap<>();

        try {

            ServerSocket serverSocket = new ServerSocket(config.getSuperPeerNodePort());
            int totalRequests=0;
            logger.serverLog("Started Super Peer server!");
            logger.serverLog("Listening on port "+config.getSuperPeerNodePort());
            /* Infinite Loop which listens to sockets */
            while(true) {
                totalRequests+=1;
                Socket socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                /* What king of request is this - Peer Node or Super Peer Node */
                String command=dataInputStream.readUTF();
                int type=0;

                if(command.equals("superpeer_request")) {
                    type=0;
                    new SuperPeerHandler(socket, dataInputStream, dataOutputStream, broadcastMessageConcurrentHashMap, config, logger).start();
                } else if(command.equals("peer_request")) {
                    type=1;
                    new ClientHandler(socket, dataInputStream, dataOutputStream, broadcastReplyConcurrentHashMap,  config,  logger).start();
                }

                logger.serverLog("Accepted a request from "+clientType.values()[type]+" Total Number of Requests: "+totalRequests);
            }
        } catch (IOException e ){
            e.printStackTrace();
            logger.serverLog("IOException has been caused in the indexing server! ");
        }
    }

    /* Read config file */
    private Config readConfigFile() {

        String configFilePath=System.getProperty("user.dir")+"/resources/config.properties";
        HashMap<String,String> configProperties=new HashMap<>();

        try {

            BufferedReader reader = new BufferedReader(new FileReader(configFilePath));
            String line=reader.readLine();

            while(line!=null) {
                String[] lineArray=line.split("=");
                configProperties.put(lineArray[0], lineArray[1]);
                line=reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Create config object */
        Config config = new Config(configProperties.get("super_node_ip"),
                Integer.parseInt(configProperties.get("super_node_port")),
                getSuperNodeConnectionsHashMap(configProperties),
                getPeerNodeConnectionHashmap(configProperties));

        return config;
    }

    private HashMap<String, SuperNode> getSuperNodeConnectionsHashMap(HashMap<String, String> configProperties) {

        String connectionString = configProperties.get("super_node_connection_string");
        String[] connectionStringArray = connectionString.trim().split(",");
        HashMap<String, SuperNode> result = new HashMap<>();

        for(String superPeerNodeId : connectionStringArray) {

            String ip = superPeerNodeId.split(":")[0];
            int port = Integer.parseInt(superPeerNodeId.split(":")[1]);

            try {
                Socket superPeerSocket = new Socket(ip, port);

                SuperNode superNode = new SuperNode(superPeerNodeId, superPeerSocket,
                        new DataOutputStream(superPeerSocket.getOutputStream()),
                        new DataInputStream(superPeerSocket.getInputStream()),
                        new ObjectOutputStream(superPeerSocket.getOutputStream()),
                        new ObjectInputStream(superPeerSocket.getInputStream()),
                        ip, port);

                /* Tell the already existing super node that he wants to register himself */
                superNode.getDataOutputStream().writeUTF("superpeer_request");
                /* Who are you */
                superNode.getDataOutputStream().writeUTF(configProperties.get("super_node_port"));
                /* Rgister command */
                superNode.getDataOutputStream().writeUTF("register_supernode");
                /* Check if register happened successfully */
                String response = superNode.getDataInputStream().readUTF();

                if(response.equals("done")) {
                    logger.serverLog("Successfully registered with Super Node with ID "+superPeerNodeId);
                }

                result.put(superPeerNodeId, superNode);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return result;
    }

    private ConcurrentHashMap<String, PeerNode> getPeerNodeConnectionHashmap(HashMap<String, String> configProperties) {

        String connectionString = configProperties.get("peer_node_connection_string");
        String[] connectionStringArray = connectionString.trim().split(",");
        ConcurrentHashMap<String, PeerNode> result = new ConcurrentHashMap<>();

        for(String peerNodeId : connectionStringArray) {

            String ip = peerNodeId.split(":")[0];
            int port = Integer.parseInt(peerNodeId.split(":")[1]);
            PeerNode peerNode = new PeerNode(peerNodeId, ip, port, logger);
            result.put(peerNodeId, peerNode);
        }

        return result;
    }

    private void createLogFile() {
        String serverLogPath=System.getProperty("user.dir")+"/logs/server.log";

        File serverFile = new File(serverLogPath);
        try {
            /* Create the log files if not created */
            serverFile.createNewFile();

            /* Assign logger */
            logger=new Logger(serverLogPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

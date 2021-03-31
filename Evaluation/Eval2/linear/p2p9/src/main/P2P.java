package main;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class P2P {

    int noOfClients=0;
    static Logger logger;

    public static void main(String[] args) {

        /* Start the Peer Node and perform all intial duties */

        /* Initial Duties
        1.) Read config properties
        2.) Connect to Super Peer on startup
        3.)
        * */

        P2P p2p = new P2P();
        /* Read config properties */
        Config config=p2p.readConfigFile();
        /* Create the log files */
        p2p.createLogFile();
        logger.serverLog("Starting up the P2P Node with ID "+config.getId());
        SuperPeerNode superPeerNode=p2p.connectToSuperPeer(config);
        p2p.registerFiles(superPeerNode, config);

        try {
            /* Start server socket */
            ServerSocket serverSocket = new ServerSocket(config.getPeerNodePort());
            logger.serverLog("P2P Node started up!");
            logger.serverLog("Listening on "+config.getPeerNodePort());

            /* Client - The client is configured as a thread. */
            new Client(superPeerNode, config, args, logger).start();
            /* This is the server */
            while(true) {
                /* Accept a connection from another P2P node */
                Socket socket = serverSocket.accept();
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                /* Read which client is requesting */
                String clientId=dataInputStream.readUTF();
                logger.serverLog("Accepted Client with ID: "+clientId+" ! Total clients! "+(++p2p.noOfClients));

                /* Service the client */
                String response=dataInputStream.readUTF();
                if(response.equals("download")) {
                    /* Send socket to upload thread and let it do the rest */
                    new UploadHandler(new P2PNode(clientId, socket, dataOutputStream, dataInputStream), config, p2p).start();
                }

                /* Done */
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            superPeerNode.exit();
        }

    }

    /* Connect to the indexing server */
    private SuperPeerNode connectToSuperPeer(Config config) {

        SuperPeerNode superPeerNode=null;

        try {

            InetAddress superPeerNodeIp = InetAddress.getByName(config.getSuperPeerIp());
            int superPeerNodePort = config.getSuperPeerNodePort();
            Socket superPeerSocket = new Socket(superPeerNodeIp, superPeerNodePort);
            DataInputStream superPeerInputStream = new DataInputStream(superPeerSocket.getInputStream());
            DataOutputStream superPeerOutputStream = new DataOutputStream(superPeerSocket.getOutputStream());
            superPeerOutputStream.writeUTF("peer_request");
            superPeerOutputStream.writeUTF(Integer.toString(config.getPeerNodePort()));
            System.out.println("Connected to the Super Peer Node! ");

            superPeerNode = new SuperPeerNode(superPeerSocket, superPeerOutputStream, superPeerInputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return superPeerNode;
    }

    /* Register the files in the files directory to the indexing server */
    private void registerFiles(SuperPeerNode superPeerNode, Config config) {

        try {
            /* Read all files present on the server and register them with the Indexing server */

            DataOutputStream output = superPeerNode.getDataOutputStream();
            DataInputStream input = superPeerNode.getDataInputStream();

            String hostFilesDirectory=config.getHostFilePath();
            File hostFilesFolder = new File(hostFilesDirectory);

            if(hostFilesFolder==null){
                System.out.println("The firectory is null");
            }

            StringBuilder resultFiles = new StringBuilder();

            for(File file : hostFilesFolder.listFiles()) {
                resultFiles.append(file.getName());
                resultFiles.append(",");
            }

            if(resultFiles.length() > 0 && resultFiles.charAt(resultFiles.length()-1)==',')
                resultFiles.deleteCharAt(resultFiles.length()-1);

            /* Speak to the indexing server */

            output.writeUTF("register");
                /* Check if register happened successfully */
            String response = input.readUTF();

            if(response.equals("done")) {
                System.out.println("Successfully registered with Indexing server.");
            }

            /* Register happened successfully */
            /* Register the files with the server */

            output.writeUTF("update_add");
            response=input.readUTF();

            if(response.equals("done")) {
                /* Client is registerd, send the files */
                output.writeUTF(resultFiles.toString());
            }

            response=input.readUTF();

            if(response.equals("done")) {
                System.out.println("All files have been regsitered with the Indexing server! ");
            }

        } catch (IOException e) {
            e.printStackTrace();
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

        Config config = new Config(
                Integer.parseInt(configProperties.get("peer_node_port")),
                configProperties.get("peer_node_ip"),
                Integer.parseInt(configProperties.get("super_peer_port")),
                configProperties.get("super_peer_ip"),
                configProperties.get("host_files_directory"));

        return config;
    }

    /* Create a log file in logs directory */
    private void createLogFile() {
        String serverLogPath=System.getProperty("user.dir")+"/logs/server.log";
        String clientLogPath=System.getProperty("user.dir")+"/logs/client.log";

        File serverFile = new File(serverLogPath);
        File clientFile = new File(clientLogPath);

        try {
        /* Create the log files if not created */
            serverFile.createNewFile();
            clientFile.createNewFile();

            /* Assign logger */
            logger=new Logger(serverLogPath, clientLogPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

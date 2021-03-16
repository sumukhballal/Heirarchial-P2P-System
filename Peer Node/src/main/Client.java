package main;

import java.awt.desktop.SystemEventListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class Client extends Thread {


    IndexingServer indexingServer;
    Config config;
    String clientId;

    Client(IndexingServer indexingServer, Config config) {
        this.indexingServer=indexingServer;
        this.config=config;
        this.clientId=setClientId();
    }

    /* As a client we should be able to send a download request for a certain file
    * Two things should be done here
    * Send a query request to indexing server
    * Choose a random peer to download file from
    *  */
    @Override
    public void run() {

        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.println("\n \n Choose your operation! \n 1.) Download a file \n > \n");
            Integer input=Integer.parseInt(scanner.next());

            switch (input) {
                case 1:
                    System.out.println("Enter File Name! \n >");
                    String fileName=scanner.next();
                    downloadFile(fileName);
                    break;
                default:
                    System.out.println("Not a valid option! ");
            }
        }
    }

    /* First send a query request to Indexing server
    * Second select a peer node and download from it.
    *  */
    private void downloadFile(String fileName) {
        try {
            DataOutputStream output=indexingServer.getDataOutputStream();
            DataInputStream input=indexingServer.getDataInputStream();

            /* Query the indexing server */
            output.writeUTF("query");
            String response=input.readUTF();

            if(response.equals("error")) {
                System.out.println("Client is unable to download a file because indexing server threw an error during query! ");
                return;
            }

            /* Send filename to be checked */
            output.writeUTF(fileName);
            String nodeListWithFile=input.readUTF();
            response=input.readUTF();

            if(response.equals("error")) {
                System.out.println("Client is unable to download a file because indexing server threw an error during query! ");
                return;
            }

            P2PNode node = getNodeToDownloadFrom(nodeListWithFile);
            if(node==null) {
                System.out.println("No node found where this file resides! ");
                return;
            }

            System.out.println("File found in P2P Node with ID "+node.getId());
            System.out.println("");
            System.out.println("Sending download request for file "+fileName+" to node with ID "+node.getId());
            /* Download the file from the node */
            downloadRequest(node, fileName);
            /* Exit node socket */
            node.exit();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadRequest(P2PNode node, String fileName) {
        DataOutputStream output = node.getDataOutputStream();
        DataInputStream input = node.getDataInputStream();
        try {
            /* Send the ID first */
            output.writeUTF(clientId);
             /* Send the request */
            output.writeUTF("download");
            /* Send download file name */
            output.writeUTF(fileName);
            /* Receive file size */
            int fileSize=Integer.parseInt(input.readUTF());
            /* Download the file serially */
            downloadSerial(fileName, config.getHostFilePath(), fileSize, input);
            /* Let indexing server know */
            informIndexingServer(true, fileName);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Inform the indexing server a file has been added/deleted from Server */
    /* Args - supports added/deleted whcih will get see if added is true else it is deleted */
    private void informIndexingServer(boolean added, String fileName) {
        DataOutputStream indexingServerOutput = indexingServer.getDataOutputStream();
        DataInputStream indexingServerInput = indexingServer.getDataInputStream();


        try {
            String operation="update_add";
            if (!added)
                operation="update_delete";

            indexingServerOutput.writeUTF(operation);
            String response=indexingServerInput.readUTF();

            if(response.equals("error")) {
                System.out.println("Not possible to inform the indexing server! Retry! ");
                return;
            }

            indexingServerOutput.writeUTF(fileName);
            response=indexingServerInput.readUTF();

            if(response.equals("error")) {
                System.out.println("Not possible to inform the indexing server! Retry! ");
                return;
            }

            /* File has been added */
            System.out.println("File "+fileName+" has been added to the indexing server! ");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private P2PNode getNodeToDownloadFrom(String nodeListWithFile) {

        nodeListWithFile=nodeListWithFile.substring(1,nodeListWithFile.length()-1);
        int size=nodeListWithFile.split(",").length;

        if(size==0) {
            return null;
        }

        int index=0;

        if(size==1) {
            index=0;
        }

        if(size>1) {
            Random random = new Random();
            int low=0;
            int high=size-1;
            index=random.nextInt(high-low) + low;
        }

        String nodeId=nodeListWithFile.split(",")[index];

        try {
            String nodeIP=nodeId.split(":")[0];
            String nodePort=nodeId.split(":")[1];
            InetAddress p2pNodeIP = InetAddress.getByName(nodeIP);
            int p2pNodePort = Integer.parseInt(nodePort);
            Socket p2pNodeSocket = new Socket(p2pNodeIP, p2pNodePort);

            P2PNode clientNode = new P2PNode(nodeId, p2pNodeSocket, new DataOutputStream(p2pNodeSocket.getOutputStream()), new DataInputStream(p2pNodeSocket.getInputStream()));
            return clientNode;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /* Sets the client id for the P2P node to tell other nodes who it is */
    private String setClientId() {
        String clientIp=config.getPeerNodeIp();
        int clientPort=config.getPeerNodePort();

        if(clientIp.equals("localhost"))
            clientIp="127.0.0.1";

        return clientIp+":"+clientPort;
    }

    private void downloadSerial(String fileName, String filePath, int fileSize, DataInputStream input)  {
        System.out.println("Downloading file  : " + fileName + " to directory " + filePath + " of size " + fileSize);
        File file = new File(filePath+"/"+fileName);
        byte[] fileBytes = new byte[fileSize];

            try {
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                int bytesRead = input.read(fileBytes, 0, fileBytes.length);
                bufferedOutputStream.write(fileBytes, 0, bytesRead);
                bufferedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}

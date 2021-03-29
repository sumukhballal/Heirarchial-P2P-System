package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SuperNode {

    String id;
    Socket socket;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    String ipAddress;
    int portNumber;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;


    public SuperNode(String id, Socket socket, DataOutputStream dataOutputStream, DataInputStream dataInputStream,
                     ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream, String ipAddress, int portNumber) {
        this.id = id;
        this.socket = socket;
        this.dataOutputStream = dataOutputStream;
        this.dataInputStream = dataInputStream;
        this.ipAddress=ipAddress;
        this.portNumber=portNumber;
        this.objectInputStream=objectInputStream;
        this.objectOutputStream=objectOutputStream;
    }

    public String getId() {
        return id;
    }

    public Socket getSocket() {
        return socket;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }
}

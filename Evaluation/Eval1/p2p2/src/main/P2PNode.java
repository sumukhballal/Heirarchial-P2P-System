package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class P2PNode {

        String id;
        Socket socket;
        DataOutputStream dataOutputStream;
        DataInputStream dataInputStream;

        public P2PNode(String id, Socket socket, DataOutputStream dataOutputStream, DataInputStream dataInputStream) {
            this.id=id;
            this.socket = socket;
            this.dataOutputStream = dataOutputStream;
            this.dataInputStream = dataInputStream;
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

        public void exit() {
            try {

                dataOutputStream.close();
                dataInputStream.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

}

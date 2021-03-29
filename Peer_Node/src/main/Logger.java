package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

//    FileWriter serverFileWriter;
//    FileWriter clientFileWriter;
//    BufferedWriter bufferedServerWriter;

    String serverFile;
    String clientFile;

    Logger(String serverFile, String clientFile) {
        this.serverFile=serverFile;
        this.clientFile=clientFile;

        // createFileWriters(serverFile, clientFile);
    }


    public void serverLog(String data) {
        System.out.println(data);
        try {

            FileWriter serverFileWriter=new FileWriter(serverFile, true);
            serverFileWriter.write(data);
            serverFileWriter.write(System.lineSeparator());
            serverFileWriter.flush();
            serverFileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clientLog(String data) {
        System.out.println(data);
        try {

            FileWriter clientFileWriter=new FileWriter(clientFile, true);
            clientFileWriter.write(data);
            clientFileWriter.write(System.lineSeparator());
            clientFileWriter.flush();
            clientFileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void createFileWriters(String serverFile, String clientFile) {
//        try {
//
//            serverFileWriter = new FileWriter(serverFile, true);
//            clientFileWriter = new FileWriter(clientFile, true);
//            //bufferedServerWriter = new BufferedWriter(serverFileWriter);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    private void writeToLog() {
//        try {
//            //bufferedServerWriter.close();
//            serverFileWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}

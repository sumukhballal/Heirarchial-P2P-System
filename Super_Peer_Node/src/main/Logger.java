package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    String serverFile;

    Logger(String serverFile) {
        this.serverFile=serverFile;
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
}

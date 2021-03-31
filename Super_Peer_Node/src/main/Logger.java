package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    String serverFile;
    int logSupressMode;

    Logger(String serverFile, int logSupressMode) {

        this.serverFile=serverFile;
        this.logSupressMode=logSupressMode;
    }


    public void serverLog(String data) {
        if(logSupressMode==0) {
            System.out.println(data);
        }
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

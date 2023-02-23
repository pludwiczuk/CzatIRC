package Serwer;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            ServerSocket sc = new ServerSocket(9999);
            Socket soc1 = sc.accept();
            //Socket soc2 = sc.accept();
            Scanner scanner = new Scanner(soc1.getInputStream());
            PrintWriter pw = new PrintWriter(soc1.getOutputStream(),true);
            while(scanner.hasNext()) {
                pw.println("0 OK");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

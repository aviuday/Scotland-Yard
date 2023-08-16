package bobby;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Manual {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java bobby.Manual <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        try (Socket socket = new Socket("127.0.0.1", port);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             Scanner console = new Scanner(System.in)) {

            String feedback;

            // First move, feedback is the welcome
            if ((feedback = input.readLine()) != null) {
                System.out.println(feedback);
                String move = console.nextLine();
                output.println(move);
            } else {
                return;
            }

            // Subsequent moves, feedback in format
            while ((feedback = input.readLine()) != null) {
                System.out.println(feedback);
                String[] parts = feedback.split("; ");
                String indicator = parts[2];

                if (!indicator.equals("Play")) {
                    break;
                }

                String move = console.nextLine();
                output.println(move);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

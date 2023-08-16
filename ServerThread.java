package bobby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread implements Runnable {
    private final Board board;
    private final int id;
    private boolean registered;
    private final BufferedReader input;
    private final PrintWriter output;
    private final Socket socket;
    private final int port;
    private final int gamenumber;

    public ServerThread(Board board, int id, Socket socket, int port, int gamenumber) {
        this.board = board;
        this.id = id;
        this.registered = false;
        this.socket = socket;
        this.port = port;
        this.gamenumber = gamenumber;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream(), true);
    }

    public void run() {
        try {
            // PART 0: Set up the socket and welcome message
            try {
                if (this.id == -1) {
                    output.println(String.format(
                            "Welcome. You play Fugitive in Game %d:%d. You start on square 42. Make a move, and wait for feedback",
                            this.port, this.gamenumber));
                } else {
                    output.println(String.format(
                            "Welcome. You play Detective %d in Game %d:%d. You start on square 0. Make a move, and wait for feedback",
                            this.id, this.port, this.gamenumber));
                }
            } catch (IOException i) {
                return;
            }

            while (true) {
                boolean quit = false;
                boolean client_quit = false;
                boolean quit_while_reading = false;
                int target = -1;



                // PART 1: Read input from the client
                String cmd = "";
                try {
                    cmd = input.readLine();
                } catch (IOException i) {
                    // Set flags and release resources

                }

                if (cmd == null) {
                    // Rage quit, set flags and release resources

                } else if (cmd.equals("Q")) {
                    // Client wants to disconnect, set flags and release resources

                } else {
                    try {
                        // Interpret input as the integer target
                        target = Integer.parseInt(cmd);
                    } catch (Exception e) {
                        // Set target that does nothing for a mispressed key
                        target = -1;
                    }
                }



                // PART 2: Entering the round
                if (!this.registered) {

                }



                // PART 3: Play the move
                if (!quit) {

                } else {
                    // Erase the player

                }



                // PART 4: Cyclic barrier, first part


                // PART 5: Get the state of the game and process accordingly
                if (!client_quit) {
                    String feedback;
                    try {
                        // ...
                    } catch (Exception i) {
                        // Set flags, release resources, and handle Fugitive case
                    }

                    String indicator;

                    if (!indicator.equals("Play")) {
                        // Proceed similarly to IOException
                    }
                }

            }
        } catch (InterruptedException ex) {
            return;
        } catch (IOException i) {
            return;
        } finally {
            try {
                input.close();
                output.close();
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}

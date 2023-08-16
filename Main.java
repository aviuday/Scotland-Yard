package bobby;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main implements Runnable {

    public int port;
    public int gamenumber;

    public Main(int port) {
        this.port = port;
        this.gamenumber = 0;
    }

    public void run() {
        while (true) {
            Thread tau = new Thread(new MainGame(this.port, this.gamenumber));
            tau.start();
            try {
                tau.join();
            } catch (InterruptedException e) {
                return;
            }
            this.gamenumber++;
        }
    }

    public class MainGame implements Runnable {
        private final Board board;
        private final ServerSocket server;
        public int port;
        public int gamenumber;
        private final ExecutorService threadPool;

        public MainGame(int port, int gamenumber) {
            this.port = port;
            this.board = new Board();
            this.gamenumber = gamenumber;
            try {
                this.server = new ServerSocket(port);
                System.out.println(String.format("Game %d:%d on", port, gamenumber));
                server.setSoTimeout(5000);
            } catch (IOException i) {
                return;
            }
            this.threadPool = Executors.newFixedThreadPool(10);
        }

        public void run() {
            try {
                // Game initialization

                Socket socket = null;
                boolean fugitiveIn;

                // Listen for a client to play fugitive and spawn the moderator

                do {

                } while (!fugitiveIn);

                System.out.println(this.gamenumber);

                // Spawn a thread to run the Fugitive

                // Spawn the moderator

                while (true) {
                    try {
                        Socket clientSocket = server.accept();
                        threadPool.submit(new ServerThread(clientSocket, board));
                    } catch (SocketTimeoutException t) {
                        continue;
                    }
                }

                // Reap the moderator thread, close the server

                threadPool.shutdown();

                System.out.println(String.format("Game %d:%d Over", this.port, this.gamenumber));
                return;
            } catch (InterruptedException ex) {
                System.err.println("An InterruptedException was caught: " + ex.getMessage());
                ex.printStackTrace();
                return;
            } catch (IOException i) {
                return;
            }

        }
    }

    public static void main(String[] args) {
        for (String arg : args) {
            int port = Integer.parseInt(arg);
            Thread tau = new Thread(new Main(port));
            tau.start();
        }
    }
}

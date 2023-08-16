package bobby;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

public class Random {
    private final boolean isFugitive;
    private int square;

    public Random(boolean isFugitive) {
        this.isFugitive = isFugitive;
        this.square = isFugitive ? 42 : 0;
    }

    public int nextSquare() {
        int previous = this.square;
        int abort = ThreadLocalRandom.current().nextInt(0, 50);
        if (abort == 0) {
            this.square = -1;
            return -1;
        }
        int row = previous / 8;
        int col = previous % 8;
        
        int direction = ThreadLocalRandom.current().nextInt(0, isFugitive ? 4 : 2);

        int shift = ThreadLocalRandom.current().nextInt(0, 8);
        int tarrow, tarcol, tarsq;

        if (direction == 0) {
            tarrow = (row + shift) % 8;
            tarcol = col;
        } else if (direction == 1) {
            tarrow = row;
            tarcol = (col + shift) % 8;
        } else if (direction == 2) {
            int sum = row + col;
            tarrow = ThreadLocalRandom.current().nextInt(0, 8);
            tarcol = sum - tarrow;
        } else {
            int diff = row - col;
            int minLimit = Math.max(diff, -diff);
            int maxLimit = Math.min(8 - diff, 8 + diff);
            tarrow = ThreadLocalRandom.current().nextInt(minLimit, maxLimit);
            tarcol = tarrow - diff;
        }

        tarsq = 8 * tarrow + tarcol;
        this.square = tarsq;
        return tarsq;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java bobby.Random <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        boolean isFugitive = false;
        Random agent;
        int square = -1;

        try (Socket socket = new Socket("127.0.0.1", port);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

            String feedback;

            // First move, feedback is the welcome
            if ((feedback = input.readLine()) != null) {
                System.out.println(feedback);
                if (feedback.contains("Fugitive")) {
                    isFugitive = true;
                }
                agent = new Random(isFugitive);

                if (agent.nextSquare() == -1) {
                    output.println("Q");
                } else {
                    output.println(agent.square);
                }
            } else {
                return;
            }

            // Subsequent moves, feedback in format
            while ((feedback = input.readLine()) != null) {
                System.out.println(feedback);
                String indicator = feedback.split("; ")[2];

                if (!indicator.equals("Play")) {
                    break;
                }

                if (agent.nextSquare() == -1) {
                    output.println("Q");
                } else {
                    output.println(agent.square);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package bobby;

import java.util.concurrent.TimeUnit;

public class Moderator implements Runnable {
    private final Board board;

    public Moderator(Board board) {
        this.board = board;
    }

    public void run() {
        while (true) {
            try {
                board.moderatorEnabler.acquire(); // Acquire the permit to run

                synchronized (board.threadInfoProtector) {
                    // Base case
                    if (board.embryo) {
                        continue;
                    }

                    int newbies = board.totalThreads - board.playingThreads;

                    if (board.totalThreads == 0) {
                        board.dead = true;
                        board.threadInfoProtector.release(); // Release the lock
                        return;
                    }

                    // Update playingThreads and reset quitThreads
                    board.playingThreads -= board.quitThreads;
                    board.quitThreads = 0;

                    // Release permits for threads to play and modify thread info
                    board.registration.release(newbies);
                    board.threadInfoProtector.release();
                }

                TimeUnit.MILLISECONDS.sleep(100); // Give some time for threads to play

                // Wait for all threads to reach the cyclic barrier
                for (int i = 0; i < board.playingThreads; i++) {
                    board.barrier1.acquire();
                }

                // Release the barrier for all waiting threads to proceed
                for (int i = 0; i < board.playingThreads; i++) {
                    board.barrier2.release();
                }

                TimeUnit.MILLISECONDS.sleep(100); // Give some time for threads to synchronize
            } catch (InterruptedException ex) {
                System.err.println("An InterruptedException was caught: " + ex.getMessage());
                ex.printStackTrace();
                return;
            }
        }
    }
}

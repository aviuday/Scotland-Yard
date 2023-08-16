package bobby;

import java.util.concurrent.Semaphore;

public class Board {
    private int[] detectives;
    private int fugitive;
    private int time;

    // SYNC SHARED VARIABLES
    private int count;
    private int totalThreads;
    private int playingThreads;
    private int quitThreads;
    private boolean[] availableIDs;
    private boolean dead;
    private boolean embryo;

    // SYNC PRIMITIVES: SEMAPHORES
    private final Semaphore countProtector = new Semaphore(1);
    private final Semaphore barrier1 = new Semaphore(0);
    private final Semaphore barrier2 = new Semaphore(0);
    private final Semaphore moderatorEnabler = new Semaphore(1);
    private final Semaphore threadInfoProtector = new Semaphore(1);
    private final Semaphore registration = new Semaphore(0);
    private final Semaphore reentry = new Semaphore(0);

    public Board() {
        detectives = new int[5];
        availableIDs = new boolean[5];
        for (int i = 0; i < 5; i++) {
            detectives[i] = -1;
            availableIDs[i] = true;
        }
        fugitive = -1;
        time = 0;
        count = 0;
        totalThreads = 0;
        playingThreads = 0;
        quitThreads = 0;
        dead = true;
        embryo = true;
    }

    public int getAvailableID() {
        synchronized (availableIDs) {
            for (int i = 0; i < 5; i++) {
                if (availableIDs[i]) {
                    availableIDs[i] = false;
                    return i;
                }
            }
            return -1;
        }
    }

    public void erasePlayer(int id) {
        synchronized (threadInfoProtector) {
            if (id == -1) {
                fugitive = -1;
                time++;
                dead = true;
            } else {
                detectives[id] = -1;
                availableIDs[id] = true;
            }
        }
    }

    public void installPlayer(int id) {
        synchronized (threadInfoProtector) {
            if (id == -1) {
                fugitive = 42;
                embryo = false;
            } else {
                detectives[id] = 0;
            }
        }
    }

    public void moveDetective(int id, int target) {
        if (target < 0 || target > 63 || id < 0 || id > 4) {
            return;
        }

        synchronized (threadInfoProtector) {
            if (detectives[id] == -1) {
                return;
            }

            int targetRow = target / 8;
            int targetCol = target % 8;
            int sourceRow = detectives[id] / 8;
            int sourceCol = detectives[id] % 8;

            if ((targetRow != sourceRow) && (targetCol != sourceCol)) {
                return;
            }

            detectives[id] = target;
        }
    }

    // ... (Similar modifications for other methods)

    public String showDetective(int id) {
        synchronized (threadInfoProtector) {
            boolean caught = false;
            for (int i = 0; i < 5; i++) {
                if (fugitive == detectives[i] || fugitive == -1) {
                    caught = true;
                    break;
                }
            }

            if (caught) {
                return String.format("Move %d; Detective %d; Victory; Detectives on %d, %d, %d, %d, %d; Fugitive on %d",
                        time, id, detectives[0], detectives[1], detectives[2], detectives[3],
                        detectives[4], fugitive);
            }
            if (time % 5 == 3) {
                return String.format("Move %d; Detective %d; Play; Detectives on %d, %d, %d, %d, %d; Fugitive on %d",
                        time, id, detectives[0], detectives[1], detectives[2], detectives[3],
                        detectives[4], fugitive);
            }
            if (time == 25) {
                return String.format("Move %d; Detective %d; Defeat; Detectives on %d, %d, %d, %d, %d; Fugitive on %d",
                        time, id, detectives[0], detectives[1], detectives[2], detectives[3],
                        detectives[4], fugitive);
            }

            return String.format("Move %d; Detective %d; Play; Detectives on %d, %d, %d, %d, %d",
                    time, id, detectives[0], detectives[1], detectives[2], detectives[3], detectives[4]);
        }
    }

    public String showFugitive() {
        synchronized (threadInfoProtector) {
            boolean caught = false;
            for (int i = 0; i < 5; i++) {
                if (fugitive == detectives[i] || fugitive == -1) {
                    caught = true;
                    break;
                }
            }
            if (caught) {
                return String.format("Move %d; Fugitive; Defeat; Detectives on %d, %d, %d, %d, %d; Fugitive on %d",
                        time, detectives[0], detectives[1], detectives[2], detectives[3],
                        detectives[4], fugitive);
            }
            if (time == 25) {
                return String.format("Move %d; Fugitive; Victory; Detectives on %d, %d, %d, %d, %d; Fugitive on %d",
                        time, detectives[0], detectives[1], detectives[2], detectives[3],
                        detectives[4], fugitive);
            }

            return String.format("Move %d; Fugitive; Play; Detectives on %d, %d, %d, %d, %d; Fugitive on %d", time,
                    detectives[0], detectives[1], detectives[2], detectives[3], detectives[4], fugitive);
        }
    }
}

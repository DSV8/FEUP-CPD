package Server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Matchmaker {
    private final int teamSize;
    public final ArrayList<ClientHandler> waitingPlayers = new ArrayList<>();
    public final HashMap<String, Integer> queuePlacesByToken = new HashMap<>();
    public final HashMap<Integer, ArrayList<ClientHandler>> playersByLevel = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final int mode;
    private int levelDelta = 0;
    private final ScheduledExecutorService levelDeltaScheduler = Executors.newSingleThreadScheduledExecutor(Thread.ofVirtual().factory());

    public Matchmaker(int teamSize, int mode) {
        this.teamSize = teamSize;
        this.mode = mode;

        if (mode == 2) {
            levelDeltaScheduler.scheduleAtFixedRate(this::incrementLevelDelta, 0, 10, TimeUnit.SECONDS);
        }
    }

    public void addPlayer(ClientHandler clientHandler, String token) {
        clientHandler.setInMatchmaking(true);

        if (mode == 1) { // Simple mode
            lock.lock();
            try {
                waitingPlayers.add(clientHandler);
                queuePlacesByToken.put(token, waitingPlayers.size() - 1);
                if (waitingPlayers.size() >= teamSize) {
                    startSimpleGame();
                }
            } finally {
                lock.unlock();
            }
        } else if (mode == 2) { // Ranked mode
            lock.lock();
            try {
                waitingPlayers.add(clientHandler);
                queuePlacesByToken.put(token, waitingPlayers.size() - 1);
                playersByLevel.computeIfAbsent(clientHandler.getUser().getLevel(), k -> new ArrayList<>()).add(clientHandler);
                if (waitingPlayers.size() >= teamSize) {
                    attemptToStartRankedGame();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public void restorePlayer(String token, Socket newSocket, BufferedReader in, PrintWriter out) {
        lock.lock();
        try {
            int playerPlaceInQueue = queuePlacesByToken.get(token);
            ClientHandler clientHandler = waitingPlayers.get(playerPlaceInQueue);
            clientHandler.resumeConnection(newSocket, in, out);
        } finally {
            lock.unlock();
        }
    }

    public void removePlayerFromQueue(ClientHandler clientHandler) {
        lock.lock();
        try {
            int placeInQueue = waitingPlayers.indexOf(clientHandler);
            waitingPlayers.remove(clientHandler);
            queuePlacesByToken.values().remove(placeInQueue);
            if (mode == 2) {
                playersByLevel.getOrDefault(clientHandler.getUser().getLevel(), new ArrayList<>()).remove(clientHandler);
            }
            clientHandler.stopHeartbeat();
        } finally {
            lock.unlock();
        }
    }

    private void startSimpleGame() {
        lock.lock();
        try {
            // Filter out players who are not alive from the waitingPlayers list
            List<ClientHandler> alivePlayers = waitingPlayers.stream()
                    .filter(ClientHandler::isAlive)
                    .toList();

            // Check if there are enough alive players to start a game
            if (alivePlayers.size() < teamSize) {
                return;
            }

            ArrayList<ClientHandler> team = new ArrayList<>(alivePlayers.subList(0, teamSize));
            for (ClientHandler player : team) {
                player.setInMatchmaking(false);
                removePlayerFromQueue(player);
            }

            Thread.startVirtualThread(new GameInstance(team));
        } finally {
            lock.unlock();
        }
    }

    private void attemptToStartRankedGame() {
        lock.lock();
        try {
            ArrayList<ClientHandler> playersWithinRange = new ArrayList<>();

            for (int level : playersByLevel.keySet()) {
                for (int d = 0; d <= 5; d += levelDelta) {
                    int targetLevel = level + d;
                    if (targetLevel <= 5 && playersByLevel.containsKey(targetLevel)) {
                        playersWithinRange.addAll(playersByLevel.get(targetLevel));
                    }
                    targetLevel = level - d;
                    if (targetLevel >= 1 && playersByLevel.containsKey(targetLevel)) {
                        playersWithinRange.addAll(playersByLevel.get(targetLevel));
                    }
                }
            }

            ArrayList<ClientHandler> team = new ArrayList<>();
            Iterator<ClientHandler> iterator = playersWithinRange.iterator();

            while (iterator.hasNext() && team.size() < teamSize) {
                ClientHandler player = iterator.next();
                if (player.isAlive())
                    team.add(player);
            }

            if (team.size() == teamSize) {
                for (ClientHandler player : team) {
                    player.setInMatchmaking(false);
                    removePlayerFromQueue(player);
                }
                Thread.startVirtualThread(new GameInstance(team));
            }
        } finally {
            lock.unlock();
        }
    }

    private void incrementLevelDelta() {
        lock.lock();
        try {
            levelDelta++;
        } finally {
            lock.unlock();
        }
    }
}


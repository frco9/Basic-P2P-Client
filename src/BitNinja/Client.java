package BitNinja;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList ;
import java.util.Arrays;
import java.io.IOException;


public class Client implements EventProvider, Runnable {
    private boolean running = true;
    protected Queue<Event> eventQueue = new ConcurrentLinkedQueue<Event>();
    private String trackerAddr;
    private String trackerPort;
    private TrackerSession trackerSession;
    private ArrayList<FileSession> sessionList;
    private Thread runThread;

    public Client(String trackerAddrStr, String trackerPort, String listenPort, ClientFiles clientFiles) {
        // Try to connect to the tracker
        InetAddress trackerAddr;
        try {
            trackerAddr = InetAddress.getByName(trackerAddrStr);
        } catch (UnknownHostException e) {
            Event event = new Event(EventSource.CLIENT, EventType.LOG_ERROR);
            event.addAttribute("message", "Unknown host : " + trackerPort);
            eventQueue.add(event);
            return;
        }

        try {
            trackerSession = new TrackerSession(trackerAddr, Integer.parseInt(trackerPort), clientFiles);
        } catch (IOException e) {
            Event event = new Event(EventSource.CLIENT, EventType.LOG_ERROR);
            event.addAttribute("message", "Tracker connection error : " + e.getMessage() + ", tracker address : " + trackerAddr.getHostAddress() + ":" + trackerPort);
            eventQueue.add(event);
            return;
        }

        try {
            trackerSession.announce(listenPort);
            
        } catch (Exception e) {
            Event event = new Event(EventSource.CLIENT, EventType.TRACKER_CONNECTION_ERROR);
            event.addAttribute("message", "Tracker connection error : " + e.getMessage());
            eventQueue.add(event);
            return;
        } 
        Event event = new Event(EventSource.CLIENT, EventType.TRACKER_CONNECTED);
        event.addAttribute("message", "Connected to tracker");
        eventQueue.add(event);
        
        sessionList = new ArrayList<FileSession>();
    }

    public void run() {
        // Start in a new thread
        runThread = new Thread() {
            @Override
            public void run() {
                while (running) {
                    try {
                        Thread.currentThread().sleep(10);
                    } catch(InterruptedException ie) {
                        // Exit silently
                    }
                }
            }
        };
        runThread.start();
    }
    
    public Map<String, String[]> look(String[] criteria) throws Exception {
        return trackerSession.look(criteria);
    }

    public void getFile(String fName, int cSize, byte[] fKey, long fSize) {
        String keyStr = new String(fKey);

        Map<InetAddress, Integer> peers;
        try {
            peers = trackerSession.getFile(keyStr);

        } catch (Exception e) {
            Event event = new Event(EventSource.CLIENT, EventType.TRACKER_CONNECTION_ERROR);
            event.addAttribute("message", "Tracker connection error : " + e.getMessage());
            eventQueue.add(event);
            return;
        }

        if (!peers.isEmpty()) {
            ClientFile file = new ClientFile(fName, cSize, fKey, fSize);
            FileSession fileSession = new FileSession(peers, trackerSession, file);
            sessionList.add(fileSession);
        } else {
            // TODO : Lever exception : aucun peers pour ce fichier.
        }
    }

    // TODO : Update tous les x minutes. 

    public void exit() {
        running = false;
    }

    public Event getEvent() {
        return eventQueue.poll();
    }

    public boolean eventAvailable() {
        return !eventQueue.isEmpty();
    }
}
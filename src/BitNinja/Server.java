package BitNinja;

import java.net.ServerSocket ;
import java.net.SocketException ;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue; 
import java.io.IOException ;
 
public class Server implements Runnable {
    private boolean running = true;
    protected Queue<Event> eventQueue = new ConcurrentLinkedQueue<Event>();
    private ServerSocket server;
    private Thread thread;
    private ClientFiles clientfiles;

    public Server(int port, ClientFiles cfiles){
        clientfiles = cfiles;
        try {
            server = new ServerSocket(port);

        } catch (SocketException e) {
            Event event = new Event(EventSource.CLIENT, EventType.LOG_ERROR);
            event.addAttribute("message", "Unable to open listening port : " + e.getMessage());
            eventQueue.add(event);

        } catch (IOException e) {
            Event event = new Event(EventSource.CLIENT, EventType.LOG_ERROR);
            event.addAttribute("message", "Unable to open listening port : " + e.getMessage());
            eventQueue.add(event);
        }

        // Start this object in a new thread
        thread = new Thread(this) ;
        thread.start();
    }

    public void run() {
        while (running) {
            try {
                new ServerSession(server.accept(), this);
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }

            try {
                Thread.currentThread().sleep(10);
            } catch(InterruptedException ie) {
                // Exit silently
            }
        }
    }

    public ClientFile getFile(String key) {
        return clientfiles.getFile(key);
    }

    public void exit() {
        running = false;
        try {
            server.close();
        } catch (IOException e) {
        }
    }
}
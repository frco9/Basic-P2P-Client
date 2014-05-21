package BitNinja;

import java.lang.InterruptedException;
import java.net.InetAddress;
import java.util.Map;
import java.net.UnknownHostException;

public class BitNinja {
    private static boolean running = true;

    public static void main(String[] args) throws Exception {
        // Load config
        Config config = new Config();
        config.load();

        // Init user interface
        HMI hmi = new CLI();
        hmi.init();

        // Init clientFiles
        ClientFiles files = new ClientFiles(config);

        // Init Server and Client
        hmi.eventTrackerConnecting();
        Server server = new Server(Integer.parseInt(config.get("port")), files);
        Client client = new Client(config.get("tracker_address"), config.get("tracker_port"), config.get("port"), files);
        client.run();

        // Listen for and handle events
        while (running) {
            // User interface events
            while (hmi.eventAvailable()) {

                Event event = hmi.getEvent();

                switch (event.type) {
                    case HMI_INFO:
                        hmi.info(files);
                        break;

                    case HMI_SEARCH:
                        String[] query = {event.getAttributes().getValue("query")};
                        try {
                            Map<String, String[]> result = client.look(query);
                            hmi.search(result);
                        } catch (Exception exception) {
                            Event e = new Event(EventSource.CLIENT, EventType.LOG_ERROR);
                            e.addAttribute("message", exception.getMessage());
                            hmi.log(e);
                        }
                        break;

                    case HMI_DOWNLOAD:
                        String[] file = event.getAttributes().getValue("file").split("\\s+");
                        client.getFile(file[0], Integer.parseInt(file[1]), file[2].getBytes("UTF-8"), Long.parseLong(file[3]));
                        break;

                    case HMI_EXIT:
                        running = false;
                        hmi.exit();
                        server.exit();
                        client.exit();
                        break;
                }
            }

            // Client events
            while (client.eventAvailable()) {
                Event event = client.getEvent();

                switch (event.type) {
                    case LOG_ERROR:
                    case LOG_INFO:
                        hmi.log(event);
                        break;

                    case TRACKER_CONNECTED:
                        hmi.run();
                        hmi.eventTrackerConnected(event);
                        break;

                    case TRACKER_CONNECTION_ERROR:
                        hmi.eventTrackerConnectionError(event);
                        running = false;
                        hmi.exit();
                        server.exit();
                        client.exit();
                        break;
                }
            }

            try {
                Thread.currentThread().sleep(10);
            } catch(InterruptedException ie) {
                // Exit silently
            }
        }
    }
}
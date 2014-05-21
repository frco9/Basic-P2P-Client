package BitNinja;

import java.net.Socket ;
import java.net.InetAddress;

import java.util.HashMap;
import java.util.Map;

import java.nio.charset.Charset;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.IOException;


public class TrackerSession {
    private Socket trackerSocket;
    private BufferedReader in;
    private PrintWriter out;
    private byte fake[];
    private ClientFiles clientFiles;
    private boolean connected = false;
    
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // TODO : Fermer le socket et tous les input/output juste avant la destruction du client ou lors d'une erreur
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public TrackerSession(InetAddress trackerAddr, Integer trackerPort, ClientFiles clientFiles) throws IOException {
        trackerSocket = new Socket (trackerAddr , trackerPort);
        trackerSocket.setSoLinger(true , 10);
        out = new PrintWriter(new OutputStreamWriter(trackerSocket.getOutputStream()), true);
        in = new BufferedReader(new InputStreamReader(trackerSocket.getInputStream()));
        connected = true;
        this.clientFiles = clientFiles;
    }
 
    public void announce(String listenPort) throws Exception {
        if (connected) {
            String announceStr = "announce listen " + listenPort + " ";
            boolean isFirst = true;
            Map<String, ClientFile> listSeed = clientFiles.getSeedList();
            if (!listSeed.isEmpty()) {
                announceStr += "seed [";
                for (Map.Entry<String, ClientFile> entry : listSeed.entrySet()) {
                if(isFirst == false)
                    announceStr += " ";
                announceStr += entry.getValue().getFileName() + " " + String.valueOf(entry.getValue().getFileSize()) + " " + String.valueOf(entry.getValue().getChunkSize()) + " " + entry.getKey(); 
                isFirst = false;
                }
                announceStr += "] ";
            }
            else {
                announceStr += "seed [] ";
              }  

            // TODO : Gerer les leech
            announceStr += "leech []";

            sendChars(announceStr);
            // Force bytes to be written
            try {
                String trackerResponse;
                while ((trackerResponse = in.readLine()) != null) {
                    ClientCommand cmd = Parser.clientParse(trackerResponse, 1, fake);
                    if (cmd.getInstruction() == CommandType.OK) {
                        // Ok
                        return;

                    } else if (cmd.getInstruction() != null) {
                        throw new Exception("Protocole error during ANNOUNCE");
                    }
                }
            } catch (IOException e) {
                throw new Exception("Error while reading the socket: " + e.getMessage());
            }

        }
    }


    Map<String, String[]> look(String[] criteria) throws Exception {
        if (connected) {
            if (criteria.length == 0) {
                throw new Exception("Please specify at least one criteria");
            }

            String lookStr = "look [";
            for (String criterion : criteria) {
                if (criterion == criteria[0])
                    lookStr += criterion;
                lookStr += " " + criterion;
            }
            lookStr += "]";

            sendChars(lookStr);
            // Force bytes to be written
            try {
                String trackerResponse;
                while ((trackerResponse = in.readLine()) != null) {
                    ClientCommand cmd = Parser.clientParse(trackerResponse, 1, fake);
                    if (cmd.getInstruction() == CommandType.LIST) {
                        return cmd.getFiles();

                    } else if (cmd.getInstruction() != null) {
                        throw new Exception("Protocole error during LOOK");
                    }
                }
            } catch (IOException e){
                throw new Exception("Error while reading the socket: " + e.getMessage());
            }
            return new HashMap<String, String[]>();
        } else {
            return null;
        }
    }


    Map<InetAddress, Integer> getFile(String key) throws Exception {
        if (key == null) {
            throw new Exception("Missing key");
        }

        String getfileStr = "getfile " + key;
        sendChars(getfileStr);
        // Force bytes to be written
        try{
            String trackerResponse;
            while ((trackerResponse = in.readLine()) != null) {
                // Debug
                System.out.println(trackerResponse);
                // \Debug
                ClientCommand cmd = Parser.clientParse(trackerResponse, 1, fake);
                if (cmd.getInstruction() == CommandType.PEERS) {
                    return cmd.getPeers();

                } else if (cmd.getInstruction() != null) {
                    throw new Exception("Protocole error during GETFILE");
                }
            }
        } catch (IOException e){
            throw new Exception("Error while reading the socket: " + e.getMessage());
        }
        return new HashMap<InetAddress, Integer>();
    }

 
    private void sendChars (String str){
        out.println(str);
        out.flush();
    }

}
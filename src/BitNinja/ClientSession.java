package BitNinja;

import java.net.Socket ;
import java.net.InetAddress;

import java.lang.Thread ;
import java.lang.IllegalThreadStateException ;
import java.lang.NullPointerException ;
 
import java.nio.charset.Charset;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException ;
 
import java.util.ArrayList ;
import java.util.Arrays;

public class ClientSession implements Runnable {
	private Thread thd;
	private Socket clientSocket;
	private DataInputStream in;
	private DataOutputStream out;

	private FileSession fileSession;
	private byte[] buffer;
    private int chunkSize;
	private static ArrayList<ServerSession> listSession = new ArrayList<ServerSession>(1);
	
	public ClientSession (InetAddress clientAddr, Integer clientPort, FileSession fileSession) throws Exception {
		try{
			clientSocket = new Socket(clientAddr, clientPort);
			clientSocket.setSoLinger(true , 10);
			out = new DataOutputStream(clientSocket.getOutputStream());
			in = new DataInputStream(clientSocket.getInputStream());
		} catch (IOException e){
			throw new Exception("Unable to create socket: " + e.getMessage());
		}
		
		this.fileSession = fileSession;
		chunkSize = 1;
		thd = new Thread (this) ;
		thd.start();
	}
 
	public void run () {
		byte[] messageByte = new byte[100000];
		String key;
		int bytesToRead;
		// We begin with an interested to get host buffermap
		fileSession.interested(this);
		
		while(true){
			try{	
				boolean end = false;
				int bytesRead = 0;
				String peerResponse = "";
				int bytesF = 0;
				// Read size of message to read
				try{
					bytesToRead = in.readInt();
				} catch (IOException e) {
					System.out.println("Fermeture du socket par l'hote distant");
					in.close();
					out.close();
					clientSocket.close();
					return;
				}

				while(!end){
				    bytesRead = in.read(messageByte);
					peerResponse += new String(messageByte, 0, bytesRead);
					if (peerResponse.length() == bytesToRead){
					    end = true;
					}
				}
				ClientCommand cmd = Parser.clientParse(peerResponse, chunkSize, messageByte);
				switch(cmd.getInstruction()){
					case HAVE:
						key = cmd.getKey();
						byte[] buffermap = cmd.getBuffermap();
						if(!fileSession.have(this, key, buffermap)){
							try{
								System.out.println("Fichier telechargé !\nFermeture du socket.");
								in.close();
								out.close();
								clientSocket.close();
								return;
							} catch (IOException e) {
								System.out.println("Erreur fermeture socket: " + e.getMessage());
								e.printStackTrace();
							}
						}
						break;
					case DATA:
						key = cmd.getKey();
						fileSession.data(this, key, cmd.getChunks());
						break;
				}
			} catch (IOException e) {
				System.out.println("Error while reading socket: " + e.getMessage());
				e.printStackTrace();
			}
		}
		
	}
 

    void send (int msgSize, byte[] buff, int chunkSize){
		try {
		    this.chunkSize = chunkSize;
			out.writeInt(msgSize);
			out.write(buff,0,buff.length);
		} catch (IOException e) {
			System.out.println("Erreur lors de l'envoi");
			e.printStackTrace();
		}
	}
	

}
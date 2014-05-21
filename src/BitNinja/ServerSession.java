package BitNinja;

import java.net.Socket ;
 
import java.lang.Thread ;
import java.lang.IllegalThreadStateException ;
import java.lang.NullPointerException ;
import java.lang.String;
 
import java.nio.charset.Charset;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException ;
 
import java.util.ArrayList ;
import java.util.Arrays;
import java.util.BitSet;

import javax.xml.bind.DatatypeConverter;
public class ServerSession implements Runnable {
	private Thread thd;
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private Server server;

	private ClientFile file;
	private byte[] buffer;
	private static ArrayList<ServerSession> listSession = new ArrayList<ServerSession>(1);
	
	public ServerSession (Socket socket_, Server server_) {
		listSession.add(this);
		socket = socket_ ;
		server = server_;
		thd = new Thread (this) ;
		thd.start();
	}
 
	public void run () {
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		}
		catch (IOException a ) {
			System.out.println("erreur connection socket") ;
			a.printStackTrace() ;  
		}
 		
 		byte[] messageByte = new byte[100000];
		int bytesToRead;
		int writeDebug = 0;
		while(true){
			try{
				boolean end = false;
				int bytesRead = 0;
				String tmp = "";
				// Read size of message to read
				try{
					bytesToRead = in.readInt();
				} catch (IOException e) {
					System.out.println("Fermeture du socket par l'hote distant");
					in.close();
					out.close();
					socket.close();
					return;
				}
				if (bytesRead < 0) {
					System.out.println("Erreur lecture taille message");
					in.close();
					out.close();
					socket.close();
					return;
				}

				while(!end){
					bytesRead = in.read(messageByte);
					tmp += new String(messageByte, 0, bytesRead);
					if (tmp.length() == bytesToRead){
						end = true;
					}
				}
				// Debug
				if(writeDebug%10 == 0) System.out.println("Message du client : " + tmp);
				writeDebug++;
				// \Debug
				if (tmp.length() > 0) {
					ServerCommand cmd = Parser.serverParse(tmp);
					switch(cmd.getInstruction()){
						case INTERESTED:
							file = server.getFile(cmd.getKey());
						case HAVE:
							// TODO : Tester la presence d'un buffermap dans le have
							//        et mettre à jour le courant
							tmp = "have " + cmd.getKey() + " " + toString(file.bufferMap());
							buffer = tmp.getBytes(Charset.forName("UTF-8"));
							send(tmp.length(), buffer);
							break;

					case GETPIECES:
						tmp = "data " + cmd.getKey() + " [";
						buffer = tmp.getBytes(Charset.forName("UTF-8"));
						for (int piece : cmd.getPieces()) {
						    String tp = Integer.toString(piece);
						    buffer = concat(buffer, tp.getBytes(Charset.forName("UTF-8")));
						    buffer = concat(buffer, ":".getBytes(Charset.forName("UTF-8")));
						    int sizeTmp = DatatypeConverter.printBase64Binary(file.getChunks(piece)).getBytes(Charset.forName("UTF-8")).length;
						    String tpsize = Integer.toString(sizeTmp);
						    buffer = concat(buffer, tpsize.getBytes(Charset.forName("UTF-8")));
						    buffer = concat(buffer, ":".getBytes(Charset.forName("UTF-8")));
						    buffer = concat(buffer, DatatypeConverter.printBase64Binary(file.getChunks(piece)).getBytes(Charset.forName("UTF-8")));
						    buffer = concat(buffer, " ".getBytes(Charset.forName("UTF-8")));
						}
						buffer = concat(buffer, "]".getBytes(Charset.forName("UTF-8")));
						int size = (new String(buffer).length());
						send(size, buffer);
						break;
					}
				}
			} catch (IOException e) {
				System.out.println("Erreur lors de la lecture");
				e.printStackTrace();
			}			
		}
	}
 
	private void send (int msgSize, byte[] buff){
		try {
			out.writeInt(msgSize);
			out.write(buff,0,buff.length);
		} catch (IOException e) {
			System.out.println("Erreur lors de l'envoi");
			e.printStackTrace();
		}
	}
	
	private static byte[] concat(byte[] first, byte[] second) {
  		byte[] result = Arrays.copyOf(first, first.length + second.length);
	  	System.arraycopy(second, 0, result, first.length, second.length);
	  	return result;
	}

	private static String toString(BitSet bs) {
		String st = "";
		for(int i=0; i<bs.length()-1;i++){
			if(bs.get(i))
				st = st + "1";
			else
				st = st + "0";
		}
        return st;
    }
}

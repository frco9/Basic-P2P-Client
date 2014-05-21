package BitNinja;
import java.nio.charset.Charset;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestPeersConnectionClient {

	public static void main(String[] args){
		Config config = new Config();
		ClientFiles clientFiles = new ClientFiles(config);
		try {
		    Client client = new Client(InetAddress.getLocalHost().toString(), "8080", "43565",clientFiles);
		    //String keyStr = "ab2ce595023f85114082833eafbd0c3dc418ac12"; //fichier test taille 16661
		    //String keyStr = "21359ae00fdae480d2157c838dd5690570febaeb"; //fichier essai taille 40000
		    String keyStr = "15a155111ee3537afd98b0cc5c0fb64e6ed44b43"; //fichier test.png, taille 62172
			byte[] key = keyStr.getBytes(Charset.forName("UTF-8")); 
			client.getFile("essai", 2048, key, (long) 62172);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	
}

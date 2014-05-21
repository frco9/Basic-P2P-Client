package BitNinja;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.lang.String;

import java.io.IOException;

public class ClientFiles {

	private Map<String, ClientFile> listSeed;
	private Map<String, ClientFile> listLeech;

	public ClientFiles(Config config){
		Vector<String> files = config.getSeedFiles();
		int chunkSize = Integer.parseInt(config.get("chunk_size"));
		listSeed = new HashMap<String, ClientFile>();
		listLeech = new HashMap<String, ClientFile>();

		for (int i=0; i<files.size(); i++){
				ClientFile tmp = new ClientFile(files.get(i), chunkSize);
				listSeed.put(new String(tmp.getKey()),tmp);
			
		}
	}

	public Map<String, ClientFile> getSeedList(){
		Map<String, ClientFile> tmp = new HashMap<String, ClientFile>(); 
        tmp.putAll(listSeed);
		return tmp;
	}

	public ClientFile getFile(String key){
		return listSeed.get(key);
	}

	public void addFile(String name, int cSize, byte[] fKey, long fSize){
		ClientFile file = new ClientFile(name, cSize, fKey, fSize);
		listLeech.put(new String(file.getKey()),file);
		listSeed.put(new String(file.getKey()),file);
	}
}
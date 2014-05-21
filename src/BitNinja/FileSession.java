package BitNinja;
 
import java.lang.Thread ;
import java.lang.IllegalThreadStateException ;
 
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

import java.net.InetAddress;

import java.nio.charset.Charset;

import java.io.IOException;
 

public class FileSession implements Runnable {
	private Thread thd;
	private Map<InetAddress, Integer> filePeers;
    private TrackerSession trackerSession;
	private ClientFile file;
	private BitSet reservedBits;
	
	public FileSession(Map<InetAddress, Integer> filePeers, TrackerSession trackerSession, ClientFile file) {
		this.filePeers = filePeers;
		this.trackerSession = trackerSession;
		this.file = file;
		// Copie du buffermap où seront marqués les bits telechargées ou en cours de telechargement.
		reservedBits = (BitSet) file.bufferMap().clone();
		thd = new Thread (this);
		thd.start();
	}
 
	public void run() {
		for (Map.Entry<InetAddress, Integer> entry : filePeers.entrySet()) {
			try {
				new ClientSession(entry.getKey(), entry.getValue(), this);
			} catch (Exception e) {

			}
		}
	}

	void interested(ClientSession clientSession) {
		String interestedStr = "interested " + new String(file.getKey());
		byte[] interestedBytes = interestedStr.getBytes(Charset.forName("UTF-8"));
		clientSession.send(interestedStr.length(), interestedBytes, file.getChunkSize());
	}

	synchronized boolean have(ClientSession clientSession, String key, byte[] buffermap) {
		if (!key.equals(new String(file.getKey()))) {
			System.out.println("Erreur Have : Clé différente de celle attendue.\nAttendue : " + new String(file.getKey()) + "\nRecue   : " + key);
			return false;
		}
		reservedBits = (BitSet) file.bufferMap().clone();
		BitSet updatedBMap = this.bsFromString(new String(buffermap));
		BitSet tempReservedBF = (BitSet) reservedBits.clone();
		// Find avaible chunks to download
		tempReservedBF.flip(0, updatedBMap.length());
		updatedBMap.and(tempReservedBF);
		// Update reseverBits
		reservedBits.or(updatedBMap);

		if (updatedBMap.length() > 0) {
			// TODO : Recuperer de Config le nombre de pieces que l'on peut telecharger
			// simultanement et remplacer par le 5 ci-dessous.
			// Get string of indexes to download	
			String toDownload = this.bsToString(updatedBMap, 10);

			String getpiecesStr = "getpieces " + key + " [" + toDownload + "]";
			byte[] getpiecesBytes = getpiecesStr.getBytes(Charset.forName("UTF-8"));
			clientSession.send(getpiecesStr.length(), getpiecesBytes, file.getChunkSize());
			return true;
		} else {
			return false;
		}
	}

	synchronized void data(ClientSession clientSession, String key, Map<Integer, byte[]> chunks) {	
		if (!key.equals(new String(file.getKey()))) {
			System.out.println("Erreur Have : Clé différente de celle attendue.\nAttendue : " + new String(file.getKey()) + "\nRecue   : " + key);
			return;
		}

		for (Map.Entry<Integer, byte[]> chunk : chunks.entrySet()) {
			file.setChunks(chunk.getKey(), chunk.getValue());
		}

		// TODO : Si ne fonctionne pas utiliser la meme syntaxe que pour ServerSession
		String haveStr = "have "+ key + " " + bsToString(file.bufferMap());
		byte[] haveBytes = haveStr.getBytes(Charset.forName("UTF-8"));
		clientSession.send(haveStr.length(), haveBytes, file.getChunkSize());
	}

	
	private String bsToString(BitSet bs, int maxPieces) {
		String st = "";
		int j=1;
		for(int i=0; i<bs.length();i++){
			if(bs.get(i) && j <= maxPieces){
				st += i+" ";
				j += 1;
			}
		}
		if (st.length() > 0) {
			st = st.substring(0, st.length()-1);
		}
		return st;
    }

    private String bsToString(BitSet bs) {
		String st = "";
		for(int i=0; i<bs.length()-1;i++){
			if(bs.get(i))
				st = st + "1";
			else
				st = st + "0";
		}
        return st;
    }

    private BitSet bsFromString(String st) {
		BitSet bs = new BitSet();
		for(int i=0; i<st.length();i++){
			char c = st.charAt(i); 
			if(c == '1')
				bs.set(i);
		}
        return bs;
    }

}
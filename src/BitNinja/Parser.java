package BitNinja;

import java.lang.String;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.Charset;
import java.net.InetAddress;

import javax.xml.bind.DatatypeConverter;
enum CommandType {
	GETPIECES,
	HAVE,
	INTERESTED,
	OK,
	LIST,
	PEERS,
	DATA
	}

public class Parser {

	public static ServerCommand serverParse(String input) {
		// Checks syntax
		if (!input.matches("(interested|getpieces|have)\\s[a-fA-F0-9]{40}(\\s((\\[(\\d(\\s)?)+\\])|(?>0|1)+))?")) {
			System.out.println("Syntaxe incorrecte (server)");
			return new ServerCommand();
		}
		
		ServerCommand cmd = new ServerCommand();
		
		// Regex to split the string with space as separator unless there are brackets around
		String[] args = input.split("\\s+(?![^\\[]*\\])"); 

		cmd.setInstruction(args[0]);
		cmd.setKey(args[1]);
		
		if (args[0].equals("have")) {
			// La taille du buffermap peut etre trouvé, cf sujet, implementer cette verification ? 
			if ((args.length >= 3)&&(args[2].matches("(?>0|1)+"))) {
				// set the buffer map
				cmd.setBuffermap(args[2].getBytes(Charset.forName("UTF-8")));
			} else {
				System.out.println("Erreur Syntaxe : Buffermap incorrect");
				return new ServerCommand();
			}
		} else if (args[0].equals("getpieces")) {
			if ((args.length >= 3)&&(args[2].matches("\\[(?>\\d(\\s)?)+\\]"))) {
				// Resplit the array to get each pieces.
				String[] piecesChar = args[2].substring(1,args[2].length()-1).split("\\s+");
				int[] piecesInt = new int[piecesChar.length];
				for (int i=0; i < piecesChar.length; i++) {
					piecesInt[i] = Integer.parseInt(piecesChar[i]);
				}
						
				cmd.setPieces(piecesInt);
			} else {
				System.out.println("Erreur Syntaxe : Pieces array incorrect");
				return new ServerCommand();
			}
		}
		return cmd;
	}

    public static ClientCommand clientParse(String input, int sizeChunks, byte peerResponseByte[]) {
	//System.out.println("SALUT");
		if (!input.matches("(ok)|((peers|data|have)\\s[a-fA-F0-9]{40}(\\s((\\[(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\:\\d+(\\s)?)+\\])|(\\[(\\d+\\:\\d+\\:(?>.|\\n|\\t|\\r|\\s|\\S){1,2740}(\\s)?)+\\])|((?>0|1)+))))|(list\\s\\[([\\w,\\s-]+(\\.[A-Za-z0-9]{1,5})?\\s\\d+\\s\\d+\\s[a-fA-F0-9]{40}(\\s?))*\\])")) {
			System.out.println("Syntaxe incorrecte (client)");
			return new ClientCommand();
		}
		//System.out.println("SALUT");
		ClientCommand cmd = new ClientCommand();

		// Regex to split the string with space as separator unless there are brackets around
		//String[] args = input.split("\\s+(?![^\\[]*\\])"); 

		String[] args = new String[3];
		int counter=0;
		int argNum = 0;
		boolean isBrack = false;
		args[0] = "";
		args[1] = "";
		args[2] = "";
		for (char c:input.toCharArray()){
		    if (c=='[' && isBrack == false) isBrack = true;
		    if (c==' ' && isBrack == false)
			argNum++;
		    else 
			args[argNum]+=c;
		}

		cmd.setInstruction(args[0]);

		if (args[0].equals("list")) {
			if (args.length >= 2) {
				int i = 0;
				Map<String, String[]> files = new HashMap<String, String[]>();
				if(args[1].matches("\\[([\\w,\\s-]+(\\.[A-Za-z0-9]{1,5})?\\s\\d+\\s\\d+\\s[a-fA-F0-9]{40}(\\s?))+\\]")){
					String[] listChar = args[1].substring(1,args[1].length()-1).split("\\s+");		
					for(i=0;i<listChar.length;i+=4){
						String[] info = new String[3];
						System.arraycopy(listChar, i, info, 0, 3);
						try{
							files.put(listChar[i+3], info);
						} catch (Exception e) {
							System.out.println("Erreur ajout Map") ;
							e.printStackTrace() ;  
						}
					}
				}
				cmd.setFiles(files);
			} else {
				System.out.println("Erreur Syntaxe : Liste des fichiers incorrecte");
				return new ClientCommand();
			}
		} else if (args[0].equals("ok")) {
			return cmd;
		} else {
			cmd.setKey(args[1]);
			if (args[0].equals("have")) {
				if ((args.length >= 3)&&(args[2].matches("(?>0|1)+"))) {
					// set the buffer map
					cmd.setBuffermap(args[2].getBytes(Charset.forName("UTF-8")));
				} else {
					System.out.println("Erreur Syntaxe : Buffermap incorrect");
					return new ClientCommand();
				}	
			} else if (args[0].equals("peers")) {
				if ((args.length >= 3)&&(args[2].matches("\\[(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\:\\d+(\\s)?)+\\]"))) {
					// Resplit the array to get each peers.
					String[] piecesChar = args[2].substring(1,args[2].length()-1).split("\\s+");
					Map<InetAddress,Integer> peers = new HashMap<InetAddress,Integer>();
					for (int i=0; i < piecesChar.length; i++) {
						String[] adresse = piecesChar[i].split(":");
						try{
							peers.put(InetAddress.getByName(adresse[0]), Integer.parseInt(adresse[1]));
						} catch (Exception e) {
							System.out.println("Erreur ajout Map") ;
							e.printStackTrace() ;  
						}
					}	
					cmd.setPeers(peers);
				} else {
					System.out.println("Erreur Syntaxe : Peers array incorrect");
					return new ClientCommand();
				}
			} else if (args[0].equals("data")) {
				if ((args.length >= 3)&&(args[2].matches("\\[(\\d+\\:\\d+\\:(?>.|\\n|\\s|\\t|\\r|\\S){1,2740}(\\s)?)+\\]"))) {
					// On enlève les crochets
					String tmp = args[2].substring(1,args[2].length()-1);
					int tailleByte = args[0].length() + 1 + args[1].length() + 2;
					int taille = tmp.length();
					int tailleTot = peerResponseByte.length;
					byte peerByte[] = new byte[taille];
					Map<Integer, byte[]> chunks = new HashMap<Integer, byte[]>();
					int indexFirst = tmp.indexOf(":",0);

					int indexSecond = tmp.indexOf(":", indexFirst +1);

					int sizeChunkFor = Integer.parseInt(tmp.substring(indexFirst + 1, indexSecond));
					int indexTemp = 0;
					int indexNew = indexSecond;
					int fortemp = 0;
					for(int i=0;i<taille - 1;i+=sizeChunkFor){
					    if(fortemp ==1){
						if(tmp.charAt(i)==' ')
						    i++;
						indexTemp = i;
						indexFirst = tmp.indexOf(":",indexTemp);
						//System.out.println("i : " + i);
						//System.out.println("first :" + indexFirst);
						indexSecond = tmp.indexOf(":", indexFirst +1);
						//System.out.println("second :" + indexSecond);
						indexTemp = indexSecond;
						indexNew = indexSecond - i;
						//System.out.println("new :" + (indexSecond - i));
						sizeChunkFor = Integer.parseInt(tmp.substring(indexFirst + 1, indexSecond));
						//System.out.println("chunk :" + sizeChunkFor);
					    }
					    fortemp = 1;
					    try {

						String tp = tmp.substring(indexSecond, indexSecond + sizeChunkFor+1);
						chunks.put(Integer.parseInt(tmp.substring(i,indexFirst)), DatatypeConverter.parseBase64Binary(tp));
						//System.out.println(Integer.parseInt(tmp.substring(i,indexFirst)));

					    } catch (Exception e) {
						System.out.println("Erreur ajout Map") ;
						e.printStackTrace() ;  
					    }
					    i += indexNew + 2;
					}
					cmd.setChunks(chunks);
				} else {
				    System.out.println("Erreur Syntaxe : Data array incorrect");
				    return new ClientCommand();
				}
			} 
		}
		return cmd;
    }
}

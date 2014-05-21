package BitNinja;

public class TestClientFile {

	public static void main(String[] args){
		testInfo();
		testChunks();
		testFinished();
		// TODO : test checkFile avec fichier fini et non fini
	}

	/**
	 * Test the two class builder of the clientFile
	 * and the info fonction 
	 */
	static void testInfo(){
		byte[] key = {10, 5, 3, 17};
		// Test avec nouveau fichier
		ClientFile myFile = new ClientFile("Bonjour", 1024, key, (long)5042);
		assert myFile.info().equals("Bonjour 5042 1024 " + key.toString()) : "bug testInfo1";
		// Test avec fichier existant
		ClientFile myFile2 = new ClientFile("Bonjour", 1024);
		assert !myFile2.info().equals("Bonjour 5042 1024 " + key.toString()) : "bug testInfo2";
		System.out.println("testInfo OK");
	}

	static void testChunks(){
		byte[] buffer = "Bonjour".getBytes();
		byte[] key = {10, 5, 3, 17};
		byte[] tmp;
		String test;

		ClientFile myFile = new ClientFile("Bonjour2", 1024, key, (long)5042);
		assert myFile.setChunks(2, buffer) : "bug setChunks1";
		
		tmp = myFile.getChunks(2);
		test = "";
		for(int i=0 ; i<7 ; i++)
			test += (char)tmp[i];
		assert test.equals("Bonjour") : "bug getChunks1 " + test;
		
		// On vérifie si les données ont bien été enregistrées
		ClientFile myFile2 = new ClientFile("Bonjour2", 1024);
		tmp = myFile2.getChunks(2);
		test = "";
		for(int i=0 ; i<7 ; i++)
			test += (char)tmp[i];
		assert test.equals("Bonjour") : "bug getChunks2 " + test;
		System.out.println("testChunks OK");

		// assert myFile.setChunks(0, buffer2);
		// assert myFile2.setChunks(1, buffer3);
		// assert myFile.setChunks(3, buffer4);
		// assert myFile.getChunks(0) == buffer2;
		// assert myFile.getChunks(1) == buffer3;
		// assert myFile.getChunks(3) == buffer4;
		// assert myFile2.getChunks(0) == buffer2;
		// assert myFile2.getChunks(1) == buffer3;
		// assert myFile2.getChunks(3) == buffer4;

		assert !myFile.setChunks(7, buffer);
		System.out.println("testChunks avancés OK");
	}

	static void testFinished(){
		byte[] buffer1 = {1, 2, 3, 4, 5, 6, 7, 8};
		byte[] buffer2 = {8, 2, 3, 6, 5, 6, 7, 8};
		byte[] buffer3 = {1, 2, 4, 1, 5, 6, 7, 8};
		byte[] buffer4 = {1, 1, 1, 1, 1, 1, 4, 2};
		byte[] key = {10, 5, 3, 17};

		ClientFile myFile = new ClientFile("Bonjour3", 1024, key, (long)5042);

		assert myFile.finished() == false;
		assert myFile.setChunks(0, buffer1);
		assert myFile.finished() == false;
		assert myFile.setChunks(1, buffer2);
		assert myFile.finished() == false;
		assert myFile.setChunks(2, buffer3);
		assert myFile.finished() == false;
		assert myFile.setChunks(3, buffer4);
		assert myFile.finished() == false;
		assert myFile.setChunks(4, buffer4);
		assert myFile.finished() == true;

		System.out.println("testFinished OK");
	}
}
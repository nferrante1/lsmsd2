package app.test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import com.google.common.io.Files;

public class ReadFile {
	
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		byte[] r = null;
	

		Scanner sc = new Scanner(System.in);
		System.out.println("Insert path:");
		try {
			r = readFile(sc.nextLine());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		File dir =  new File("C:\\Users\\simon\\Desktop\\castagne");
		dir.mkdir();
		
		
		File obj = new File(dir.getAbsolutePath() + "/" + "b.java");
		
	    FileOutputStream outputStream = new FileOutputStream(obj);
	    outputStream.write(r);
	 
	    outputStream.close();
	}
	
	
	public static byte[] readFile(String file) throws IOException {
	    byte[] bytes = null;
	    if(!Files.getFileExtension(file).equals("java")) {
	    	System.out.println("NO .JAVA");
	    }
	    DataInputStream reader = new DataInputStream(new FileInputStream(file));
	    int nBytesToRead = reader.available();
	    if(nBytesToRead > 0) {
	    	bytes = new byte[nBytesToRead];
	        reader.read(bytes);
	    }
	    reader.close();
	    return bytes;

	}

}

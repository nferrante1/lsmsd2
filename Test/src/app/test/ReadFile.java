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
		
		String x = "Strategy.java";
		String y = x.replace(".java", "");
		System.out.println(y + " " + y.length());
		
		String ciccio = "ciccio";
		
		byte[] content1 = ciccio.getBytes(); 
		byte[] content2 = ciccio.getBytes();
		System.out.println(content1.length);
		System.out.println(content2.length);
		
		try (FileInputStream fis = new FileInputStream("C:\\Users\\simon\\Desktop\\Second.java")) {
			content1 = fis.readAllBytes();
			System.out.println(content1.toString());
			System.out.println(content1.length);
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
		}
		
		try (DataInputStream reader = new DataInputStream(new FileInputStream("C:\\Users\\simon\\Desktop\\Second.java"))) { 
			int nBytesToRead = reader.available();
			if(nBytesToRead > 0) { 
				content2 = new byte[nBytesToRead];
				 reader.read(content2);
				 System.out.println(content2.toString());
				 System.out.println(content2.length);
				 } 
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
		}
		
	}
	
	


}

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
	}
	
	


}

package app.common.net.entities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StrategyFile {
	
	protected String strategyName;
	protected byte[] file;
	protected String hashFile;
	
	
	public StrategyFile(String strategyName, byte[] file) {
		this.strategyName = strategyName;
		this.file = file;
		this.hashFile = doHash(file);
		
	}
	
	protected String doHash(byte [] file) {
		String fileHash;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] bytes = md.digest(file);
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < bytes.length; i++)
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			fileHash = sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			fileHash = "";
		}
		return fileHash;
	}
	
	
	public void save(String mainDirectory) throws FileNotFoundException, IOException {
		
		String directoryName = this.hashFile.substring(0, 2);
		String fileName = this.hashFile.substring(3);
		File dir =  new File(mainDirectory + "/" + directoryName);
		dir.mkdir();
		try (FileOutputStream fos = new FileOutputStream(new File(dir.getAbsolutePath() + "/" + fileName))) {
			fos.write(this.file);
			fos.close();
		}
	}
	

}

package app.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class StrategyFile
{
	protected byte[] file;
	protected String hashFile;
	private static String mainDirectory = "strategies";
	private String className;
	
	public StrategyFile(byte[] file, String className)
	{
		this.file = file;
		this.hashFile = doHash(file);
		this.className = className;
	}
	
	public static void setMainDirectory(String mainDirectory){
		StrategyFile.mainDirectory = mainDirectory;
	}

	protected String doHash(byte [] file)
	{
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

	public void save() throws IOException
	{

		String directoryName = this.hashFile.substring(0, 2);
		String subDirectoryName = this.hashFile.substring(3);
		File dir =  new File(mainDirectory + "/" + directoryName +"/"+subDirectoryName);
		dir.mkdirs();
		try (FileOutputStream fos = new FileOutputStream(new File(dir.getAbsolutePath() + "/" + className + ".java"))) {
			fos.write(this.file);
		}
	}

	public String getHash()
	{
		return this.hashFile;
	}

	public String getDirectoryName()
	{
		return this.hashFile.substring(0, 2) + "/" + this.hashFile.substring(3);
	}

	public String getFileName()
	{
		return className + ".java";
	}
	
	public String getFullName() {
		return mainDirectory + "/" + getDirectoryName() + "/" + getClassName();  
	}
	
	public String getJavaFile() {
		return getFullName() + ".java";
	}
	
	public String getClassFile() {
		return getFullName() + ".class";
	}
	
	public String getClassFolder() {
		return mainDirectory + "/" + getDirectoryName();
	}

	public String getClassName()
	{
		return className;
	}
	
	public void delete() {
		delete(new File(getClassFolder()));
	}
	
	public static void delete(File file) {
		File[] list = file.listFiles();
		if(list != null) {
			for(File f : list) {
				delete(f);
			}
		}
		file.delete();
	}
}

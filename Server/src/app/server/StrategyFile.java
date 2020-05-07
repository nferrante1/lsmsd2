package app.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StrategyFile
{
	protected byte[] file;
	protected String hashFile;

	public StrategyFile(byte[] file)
	{
		this.file = file;
		this.hashFile = doHash(file);
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

		String mainDirectory = "strategies";
		String directoryName = this.hashFile.substring(0, 2);
		String fileName = this.hashFile.substring(3);
		File dir =  new File(mainDirectory + "/" + directoryName);
		dir.mkdirs();
		try (FileOutputStream fos = new FileOutputStream(new File(dir.getAbsolutePath() + "/" + fileName + ".java"))) {
			fos.write(this.file);
		}
	}

	public String getHash()
	{
		return this.hashFile;
	}

	public String getDirectoryName()
	{
		return this.hashFile.substring(0, 2);
	}

	public String getFileName()
	{
		return this.hashFile.substring(3);
	}
}

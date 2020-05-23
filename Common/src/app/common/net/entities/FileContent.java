package app.common.net.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileContent extends Entity
{
	private static final long serialVersionUID = 2004229420089692143L;

	protected byte[] content;

	public FileContent(String fileName) throws IOException
	{
		try (FileInputStream fis = new FileInputStream(fileName)) {
			this.content = fis.readAllBytes();
		}
	}

	public byte[] getContent()
	{
		return content;
	}

	public void writeFile(String fileName) throws IOException
	{
		fileName = fileName.replace("/", File.separator);
		if (fileName.contains(File.separator)) {
			String dirName = fileName.substring(0, fileName.lastIndexOf(File.separator));
			File dir = new File(dirName);
			dir.mkdirs();
		}
		try (FileOutputStream fos = new FileOutputStream(fileName)) {
			fos.write(content);
		}
	}
}

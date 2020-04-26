package app.common.net.entities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileContent extends Entity
{
	private static final long serialVersionUID = 2004229420089692143L;

	protected final byte[] content;

	public FileContent(String fileName) throws FileNotFoundException, IOException
	{
		try (FileInputStream fis = new FileInputStream(fileName)) {
			this.content = fis.readAllBytes();
		}
	}

	public byte[] getContent()
	{
		return content;
	}

	public void writeFile(String fileName) throws FileNotFoundException, IOException
	{
		try (FileOutputStream fos = new FileOutputStream(fileName)) {
			fos.write(content);
		}
	}

}

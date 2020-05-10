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
		File file = new File(fileName);
		file.mkdirs();
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(content);
		}
	}

}

package app.common.net.entities;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileContent extends Entity
{
	private static final long serialVersionUID = 2004229420089692143L;

	protected byte[] content;

	public FileContent(String fileName) throws FileNotFoundException, IOException
	{
		try(DataInputStream reader = new DataInputStream(new FileInputStream(fileName))){
			
		    int nBytesToRead = reader.available();
		    if(nBytesToRead > 0) {
		    	this.content = new byte[nBytesToRead];
		        reader.read(this.content);
		    }
		    reader.close();
	    }
		/*try (FileInputStream fis = new FileInputStream(fileName)) {
			this.content = fis.readAllBytes();
		}
		*/
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

package app.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import app.library.ExecutableStrategy;

public class StrategyFile
{
	protected String hash;
	private static String mainDirectory = "strategies";
	private String className;

	public StrategyFile(String className, byte[] file)
	{
		this.className = className;
		hash = computeHash(file);
	}

	public StrategyFile(String hash) throws FileNotFoundException
	{
		this.hash = hash;
		File dir = new File(getDirectoryPath());
		for (File file: dir.listFiles()) {
			String fileName = file.getName();
			if (fileName.endsWith(".java")) {
				className = fileName.substring(0, fileName.length() - 5);
				break;
			}
		}
		if (className == null)
			throw new FileNotFoundException("Can not find a strategy for hash '" + hash + "'.");
	}

	public static void setMainDirectory(String mainDirectory)
	{
		if (mainDirectory.endsWith(File.separator))
			mainDirectory = mainDirectory.substring(0, mainDirectory.length() - 1);
		StrategyFile.mainDirectory = mainDirectory;
	}

	protected String computeHash(byte[] file)
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
			fileHash = className;
		}
		return fileHash;
	}

	public String getHash()
	{
		return this.hash;
	}

	public String getDirectoryPath()
	{
		return mainDirectory + File.separator + getHash().substring(0, 2) + File.separator + getHash().substring(3);
	}

	public String getFullName()
	{
		return getDirectoryPath() + File.separator + getClassName();
	}

	public String getJavaFilePath()
	{
		return getFullName() + ".java";
	}

	public String getClassFilePath()
	{
		return getFullName() + ".class";
	}

	public String getClassName()
	{
		return className;
	}

	public void delete()
	{
		delete(new File(getDirectoryPath()));
	}

	public static void delete(File file)
	{
		File[] list = file.listFiles();
		if(list != null)
			for(File f: list)
				delete(f);
		file.delete();
	}

	public String getStrategyName()
	{
		File classDir = new File(getDirectoryPath());
		URLClassLoader classLoader;
		try {
			classLoader = URLClassLoader.newInstance(new URL[] { classDir.toURI().toURL() });
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		try {
			Class<?> strategyClass = Class.forName(getClassName(), true, classLoader);
			if(!ExecutableStrategy.class.isAssignableFrom(strategyClass)) return null;
			try {
				ExecutableStrategy strategy = (ExecutableStrategy)strategyClass.getDeclaredConstructors()[0].newInstance();
				return strategy.getName();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | SecurityException e) {
				e.printStackTrace();
				return null;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean compile()
	{
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		return compiler.run(null, null, null, getJavaFilePath()) == 0;
	}
}

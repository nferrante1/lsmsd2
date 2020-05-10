package app.test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import app.library.ExecutableStrategy;

public class StrategyExecutor
{
	protected Class<ExecutableStrategy> strategyClass;

	@SuppressWarnings("unchecked")
	public StrategyExecutor(String folder, String className)
	{
		File file = new File(folder + "/" + className + ".java");
		String filePath = file.getAbsolutePath();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(null, null, null, filePath);
		File classDir = new File(folder);
		URLClassLoader classLoader;
		try {
			classLoader = URLClassLoader.newInstance(new URL[] { classDir.toURI().toURL() });
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		}
		try {
			strategyClass = (Class<ExecutableStrategy>)Class.forName(className, true, classLoader);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
	}
	
	public ExecutableStrategy getStrategy()
	{
		try {
			return (ExecutableStrategy)strategyClass.getDeclaredConstructors()[0].newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
			| InvocationTargetException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
		
	}

}

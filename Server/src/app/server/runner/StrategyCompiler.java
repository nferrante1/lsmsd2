package app.server.runner;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import app.library.ExecutableStrategy;
import app.server.StrategyFile;

public class StrategyCompiler {
	private StrategyFile file;
	
	public StrategyCompiler(StrategyFile file) {
		this.file = file;
	}	

	public String compile() {
		String filePath = file.getJavaFile();
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if(compiler.run(null, null, null, filePath) != 0)
			return null;
		
		File classDir = new File(file.getClassFolder());
		URLClassLoader classLoader;
		try {
			classLoader = URLClassLoader.newInstance(new URL[] { classDir.toURI().toURL() });
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		try {
			Class<?> strategyClass = Class.forName(file.getClassName(), true, classLoader);
			if(!ExecutableStrategy.class.isAssignableFrom(strategyClass)) return null;
			try {
				ExecutableStrategy exec = (ExecutableStrategy)strategyClass
						.getDeclaredConstructors()[0]
						.newInstance();
				return exec.getName();
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
}

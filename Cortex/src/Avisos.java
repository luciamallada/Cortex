import java.io.IOException;
//import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Avisos {
	public final static Logger LOGGER = Logger.getLogger("Avisos"); 

	public Avisos() {
		System.setProperty("java.util.logging.SimpleFormatter.format","%4$s: %5$s [%1$tc]%n");
		//Handler consoleHandler = new ConsoleHandler();
		Handler fileHandler = null;
		try {
			fileHandler = new FileHandler("C:\\Cortex\\incidencias.log", false);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleFormatter simpleFormatter = new SimpleFormatter();
		fileHandler.setFormatter(simpleFormatter);
		//LOGGER.addHandler(consoleHandler);
	    LOGGER.addHandler(fileHandler);
	    //consoleHandler.setLevel(Level.ALL);
	    fileHandler.setLevel(Level.ALL);
	}
//    private static class formatoIncidencias extends Formatter {
//    	@Override
//    	public String format(LogRecord record) {
////    		StringBuffer sb = new StringBuffer();
////    		sb.append(record.getMessage());
////    		sb.append("/n");
////    		return sb.toString();
//    		return record.getMessage();
//    	}
//    }
}


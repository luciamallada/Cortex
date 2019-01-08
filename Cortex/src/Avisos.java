import java.io.IOException;
//import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Avisos {
	public final static Logger LOGGER = Logger.getLogger("Avisos");
	public static Handler fileHandler = null;

	public Avisos() {
		System.setProperty("java.util.logging.SimpleFormatter.format","%4$s: %5$s [%1$tc]%n");
		try {
			fileHandler = new FileHandler("C:\\Cortex\\Incidencias\\" + mainApp.programa + ".log", false);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		SimpleFormatter simpleFormatter = new SimpleFormatter();
		fileHandler.setFormatter(simpleFormatter);
	    LOGGER.addHandler(fileHandler);
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


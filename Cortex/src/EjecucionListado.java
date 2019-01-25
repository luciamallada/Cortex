import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class EjecucionListado {

	public static void main(String[] args) throws IOException {
		
		//-------------------------------------FICHERO DE ENTRADA---------------------------------------		
	    FileReader ficheroListado = new FileReader("C:\\Cortex\\Listado.txt");
	    BufferedReader lectorListado = new BufferedReader(ficheroListado);
        //----------------------------------------------------------------------------------------------
		//-------------------------------INICIALIZADO DE VARIABLES DEL PROGRAMA-------------------------
	    mainApp.withListado = true;
		String programa = "";
		
		//---------------------------AVISO FECHA PCL---------------------------------------------------
		File ficheroFecha = new File("C:\\Cortex\\PCL.txt");
		long mod = ficheroFecha.lastModified();
		Date fecha = new Date(mod);
				
		UIManager.put("OptionPane.minimumSize",new Dimension(400,150)); 
	    UIManager.put("OptionPane.messageFont", new Font("System", Font.PLAIN, 20));
	    UIManager.put("OptionPane.buttonFont", new Font("System", Font.PLAIN, 20)); 
	    UIManager.put("TextField.font", new Font("System", Font.PLAIN, 20)); 
	    JOptionPane.showMessageDialog(null, "Última versión PCL: " + fecha);
	    int proc = JOptionPane.showConfirmDialog(null, "¿Con archivo PROC?", "Alerta!", JOptionPane.YES_NO_OPTION);
		mainApp.withProc = proc == 0 ? true : false;
		int cntl = JOptionPane.showConfirmDialog(null, "¿Con archivo CNTL?", "Alerta!", JOptionPane.YES_NO_OPTION);
		mainApp.withCntl = cntl == 0 ? true : false;
	    //----------------------------------------------------------------------------------------------
	    
	    
	    
		while ((programa = lectorListado.readLine()) != null) {
	    	mainApp.programa = programa;
	    	
	    	File ficheroPROC = new File("C:\\Cortex\\PROC\\" + programa.substring(0,6) + ".txt");
	    	if(mainApp.withProc && !ficheroPROC.exists()) {
				JOptionPane.showMessageDialog(null, "AVISO: No existe el PROC del programa " + programa);
			}
	    	
	    	File ficheroCNTL = new File("C:\\Cortex\\CNTL\\" + programa.substring(0,6) + ".txt");
	    	if(mainApp.withCntl && !ficheroCNTL.exists()) {
				JOptionPane.showMessageDialog(null, "AVISO: No existe el CNTL del programa " + programa);
			}
	    	
	    	
	    	mainApp.main(args);	    
			//-------------------------------INICIALIZADO DE VARIABLES DEL PROGRAMA-------------------------
		    mainApp.withListado = true;
		    mainApp.datos.clear();
		    mainApp.letraPaso = "";
		    mainApp.pasoE = 0;
		    mainApp.pasoS = 1;
		    mainApp.fichero.clear();
		    mainApp.pasos.clear();
		    mainApp.lineNumber = 0;
		    mainApp.auxTot = 0;
		    mainApp.auxDecimal = 0;
		    mainApp.auxUnidad = 0;
		    mainApp.tipoPaso = "";
		    WriterPasos.pasoS = -1;
	    }
	    lectorListado.close();
	}

}

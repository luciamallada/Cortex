import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.JOptionPane;

public class Explomig {
	
	static ArrayList<String> jclTest = new ArrayList<String>();
	static ArrayList<String> cntl = new ArrayList<String>();
	public static Map<String, String> datos = new HashMap<String, String>();
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String programa;

		
	    programa = JOptionPane.showInputDialog("Introduzca el nombre del programa:");
		programa = programa.toUpperCase(); 
		mainApp.programa = programa;
		mainApp.letraPaso = programa.substring(5,6);
		
		Explomig.migracionPREP();
		
	}
	
	public static void migracionPREP() throws IOException {
		// TODO Auto-generated method stub
		LectorPasos lectorPasos = new LectorPasos();
		String linea;
		boolean librerias = true;	
		int i = 0;
		//-------------------------------------Ficheros-------------------------------------------------		
	    FileReader ficheroTEST = new FileReader("C:\\Cortex\\Migrados\\" + mainApp.programa.substring(0,6) + ".txt");
	    BufferedReader lectorTEST = new BufferedReader(ficheroTEST);
	    
	    FileWriter ficheroPREP = new FileWriter("C:\\Cortex\\EXPLOMIG\\" + mainApp.programa.substring(0,6) + ".txt");
	    BufferedWriter writerPREP = new BufferedWriter(ficheroPREP);
	    //----------------------------------------------------------------------------------------------	

	    while ((linea = lectorTEST.readLine()) != null) {
	    	jclTest.add(linea);
	    }
		lectorTEST.close();
		
		linea = jclTest.get(0).replace(mainApp.programa + "Z", mainApp.programa + " ");
	    writerPREP.write(linea);
		writerPREP.newLine();
		
		writerPREP.write(jclTest.get(1));
		writerPREP.newLine();
		
	    if (mainApp.withCntl) {
		    FileReader ficheroCNTL = new FileReader("C:\\Cortex\\CNTL\\" + mainApp.programa.substring(0,6) + ".txt");
		    BufferedReader lectorCNTL = new BufferedReader(ficheroCNTL);
	    	cntl.add(lectorCNTL.readLine());
	    	lectorCNTL.close();
	    	datos = lectorPasos.leerPaso(cntl);

	    	linea = jclTest.get(2).replace("MSGCLASS=X", "MSGCLASS=" + datos.get("MSGCLASS"));
			if(datos.containsKey("NOTIFY")) {
				linea = linea.replace("NOTIFY=&SYSUID", "NOTIFY=" + datos.get("NOTIFY"));
			}else {
				linea = linea.replace("NOTIFY=&SYSUID", "NOTIFY=EXRR");
			}
			writerPREP.write(linea);
			writerPREP.newLine();
	    }else {
			writerPREP.write(jclTest.get(2));
			writerPREP.newLine();
	    	writerPREP.write("******* Cabecera No migrada a PREP *****");
			writerPREP.newLine();
	    }
	
		for(i = 3; i < jclTest.size(); i++) {
			if (librerias) {
				if(jclTest.get(i).startsWith("//JOBLIB  DD  ") || jclTest.get(i).startsWith("//        DD  ")) {
					linea = "";
				}else if (jclTest.get(i).startsWith("//*%")){
					librerias = false;
					linea = jclTest.get(i);
				}else {
					linea = jclTest.get(i);
				}
			}else {
				linea = jclTest.get(i);
				linea = linea.replace("PARMS=Z.", "PARMS=");
				linea = linea.replace("DSN=Z.",   "DSN=");
				linea = linea.replace("SRSTAT 'Z.", "SRSTAT '");
			}	
			if (!linea.equals("")) {
				writerPREP.write(linea);
				writerPREP.newLine();
			}
		}
		System.out.println("**** Migración a PREP realizada CORRECTAMENTE *****");
	    Avisos.LOGGER.log(Level.INFO, "**** Migración a PREP realizada CORRECTAMENTE *****");	

		writerPREP.close();
	}
}

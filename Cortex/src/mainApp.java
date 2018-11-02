import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;


public class mainApp {
	//--------------------- DATO A INTRODUCIR ------------------------------
	public static String programa = "AUT01J";
	//----------------------------------------------------------------------
	
	//--------------------- Variables Programa -----------------------------
	public static Map<String, String> datos = new HashMap<String, String>();
	static String letraPaso = programa.substring(5,6);
	static int pasoE = 0;
	static int pasoS = 1;
	static ArrayList<String> fichero = new ArrayList<String>();
	static ArrayList<String> pasos = new ArrayList<String>();
	static int lineNumber = 0;
	static int auxTot = 0;
	static int auxDecimal = 0;
	static int auxUnidad = 0;
	static LectorPasos lectorPasos = new LectorPasos();
	static WriterPasos writerPasos = new WriterPasos();
	static Avisos  avisos = new Avisos();
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String linea, tipoPaso;
		boolean seguir = true, escribir = false;

//-------------------------------------Ficheros-------------------------------------------------		
	    FileReader ficheroPCL = new FileReader("C:\\Cortex\\PCL.txt");
	    BufferedReader lectorPCL = new BufferedReader(ficheroPCL);
	    
	    FileWriter ficheroCortex = new FileWriter("C:\\Cortex\\Migrados\\" + programa.substring(0,6) + ".txt");
	    BufferedWriter writerCortex = new BufferedWriter(ficheroCortex);
//----------------------------------------------------------------------------------------------	    
	     
//------------------------------------PROGRAMA--------------------------------------------------
	    Avisos.LOGGER.log(Level.INFO, "Comienza el proceso - PROGRAMA: " + programa.substring(0,6));	 

	    //Aisla el JCL a tratar.
	    while ((linea = lectorPCL.readLine()) != null && seguir) {
	    	
	    	if(linea.startsWith(":/ ADD NAME=" + programa.substring(0,6)) || escribir) {
	    		escribir = true;
	    		fichero.add(linea);
	    		if(linea.startsWith(":/ ADD NAME=") && !linea.startsWith(":/ ADD NAME=" + programa.substring(0,6))) {
		    		escribir = false;
		    		seguir = false;
		    	}
	    	}
	    }
	    lectorPCL.close();
	    seguir = true;
	    
//------------- Escribimos la cabecera
	    escribeJJOB(writerCortex);
  
// ------------ Aislamos el paso
	    while (seguir) {
		    tipoPaso = aislamientoDePaso();
		    //Verificaci�n aislamiento
		    System.out.println("------- El paso es:  -------------------");
		    for (int i = 0; i < pasos.size(); i++) {
		    	System.out.println(pasos.get(i));
		    }
		    System.out.println("----------------------------------------");
// ------------ Para cada paso, leemos el tipo de paso y escribimos su correspondiente plantilla
		    switch (tipoPaso) {
		    case "Inicio":
				for(int i = 0; i < pasos.size(); i++) {
					if (pasos.get(i).startsWith("*")){
						writerCortex.write("//" + pasos.get(i));
						writerCortex.newLine();
					}
				}
				break;
			case "DB2":
				datos = lectorPasos.leerPaso(pasos);
				writerPasos.writeDB2(datos, letraPaso, pasoE, writerCortex);
				break;	
			case "NAME=MAILTXT":
				datos = lectorPasos.leerPaso(pasos);
				writerPasos.writeMAILTXT(datos, letraPaso, pasoE, writerCortex);
				break;
			case "SORT":
				datos = lectorPasos.leerPasoSort(pasos);
				writerPasos.writeSORT(datos, letraPaso, pasoE, writerCortex);
				break;
			case "NAME=FTPSEND":
				datos = lectorPasos.leerPaso(pasos);
				writerPasos.writeJFTPSEND(datos, letraPaso, pasoE, writerCortex);
				break;
			case "NAME=FTPREB":
				datos = lectorPasos.leerPaso(pasos);
				writerPasos.writeJFTPREB(datos, letraPaso, pasoE, writerCortex);
				break;
			case "NAME=FTPDEL":
				datos = lectorPasos.leerPaso(pasos);
				writerPasos.writeFTPDEL(datos, letraPaso, pasoE, writerCortex);
				break;
			case "NAME=MAILESP":
				datos = lectorPasos.leerPaso(pasos);
				writerPasos.writeJMAILMSG(datos, letraPaso, pasoE, writerCortex);
				break;
			case "NAME=FTPSAPP":
				datos = lectorPasos.leerPaso(pasos);
				writerPasos.writeJFTPSAPP(datos, letraPaso, pasoE, writerCortex);
				break;
			case "NAME=MAIL":
				datos = lectorPasos.leerPaso(pasos);
				writerPasos.writeJMAILANX(datos, letraPaso, pasoE, writerCortex);
				break;
			case "NAME=VERBUIT":
				datos = lectorPasos.leerPaso(pasos);
				writerPasos.writeJFIVACIO(datos, letraPaso, pasoE, writerCortex);
				break;
			case "JOPCREC":
				datos = lectorPasos.leerPasoJOPCREC(pasos);
				writerPasos.writeJOPCREC(datos, letraPaso, pasoE, writerCortex);
				break;
			case "PGM=SOF07200":
				tipoPaso = pasoAdicional();
				if (tipoPaso.equals("JFUSION")) {
					datos = lectorPasos.leerPasoJFusionGenquad(pasos);
					writerPasos.writeJFUSION(datos, letraPaso, pasoE, writerCortex);
				}else {
					datos = lectorPasos.leerPasoJFusionGenquad(pasos);
					writerPasos.writeJGENCUAD(datos, letraPaso, pasoE, writerCortex);
				}
				break;
			case "NAME=PAPYRUS":
				datos = lectorPasos.leerPaso(pasos);
				writerPasos.writeJPAPYRUS(datos, letraPaso, pasoE, writerCortex);
				break;
			case "JPAUSA":
				datos = lectorPasos.leerPasoJPAUSA(pasos);
				writerPasos.writeJPAUSA(datos, letraPaso, pasoE, writerCortex);
				break;
			case "JSOFCHEC":
				datos = lectorPasos.leerPaso(pasos);
				writerPasos.writeJSOFCHEC(datos, letraPaso, pasoE, writerCortex);
				break;
			case "NAME=SOFINF":
				datos = lectorPasos.leerPaso(pasos);
				writerPasos.writeJSOFINF(datos, letraPaso, pasoE, writerCortex);
				break;
			case "NAME=FTPS123":
				datos = lectorPasos.leerPaso(pasos);
				writerPasos.writeJFTPS123(datos, letraPaso, pasoE, writerCortex);
				break;
			case "NAME=FTPVER":
				datos = lectorPasos.leerPaso(pasos);
				writerPasos.writeJFTPVER(datos, letraPaso, pasoE, writerCortex);
				break;
			case "NAME=MAIL123":
				datos = lectorPasos.leerPaso(pasos);
				writerPasos.writeJMAIL123(datos, letraPaso, pasoE, writerCortex);
				break;
			case "JBORRARF":
				datos = lectorPasos.leerPaso(pasos);
				writerPasos.writeJBORRARFPasos(datos, letraPaso, pasoE, writerCortex);
				break;
			case "ignore":
				break;
			default:
				if(tipoPaso.equals("NAME=SOF30QM") || tipoPaso.equals("NAME=SOF30Q")) {
					tipoPaso = "Plantilla QMF - Avisar Aplicaci�n";
				}
				WriterPasos.pasoS += 2;
				String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
				writerCortex.write("**************************************************");
				writerCortex.newLine();
				writerCortex.write("*******A�ADIR PLANTILLA: //"+ letraPaso + numeroPaso + tipoPaso + "*********");
				writerCortex.newLine();
				writerCortex.write("**************************************************");
				writerCortex.newLine();
				Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // A�adir Plantilla: " + tipoPaso);
				
				break;
			}
		    System.out.println("------- Datos sacados del Paso:  -------");
		    datos.forEach((k,v) -> System.out.println(k + "-" + v));
		    System.out.println("----------------------------------------");
//		    pasoE += pasoAPaso;
		    datos.clear();
			if (lineNumber + 1 == fichero.size()) {
				seguir = false;
			}
	    }
	    writerCortex.close();
	}

	private static String aislamientoDePaso() {
// Si se acaba hacer un booleano de fin fichero
		int inicio = 0, fin = 0, index = 0;
		String tipoPaso = "";
		
		for(int i = lineNumber; i < fichero.size(); i++) {
	    	if(fichero.get(i).matches("[" + letraPaso + "][" + auxDecimal + "-9][" + auxUnidad + "-9] (.*)")
	    			|| fichero.get(i).matches("[" + letraPaso + "][" + auxDecimal + 1 + "-9][0-9] (.*)")) {
	    		if (inicio == 0 && !tipoPaso.equals("Inicio")) {
	    			inicio= i;
	    			pasoE = Integer.parseInt(fichero.get(i).substring(1,3));
		    		auxDecimal = pasoE / 10;
		    		auxUnidad  = pasoE - auxDecimal * 10 + 1;
	    		}else {
	    			fin = i;
	    			i = fichero.size() + 1;
	    		}
	    	}
	    	if(i == 0) {
	    		inicio = 0;
	    		tipoPaso = "Inicio";
	    	}
	    	if(i + 1 == fichero.size()) {
	    		fin = i;
	    		i = fichero.size() + 1;
	    	}
	    }
		pasos.clear();
		
		index = fichero.get(inicio).indexOf("PATTERN");
		if (index != -1) {
			for(int i = index; i < fichero.get(inicio).trim().length(); i++) {
				if(fichero.get(inicio).charAt(i) == ',') {
					tipoPaso = fichero.get(inicio).substring(index + 8, i);
					i = 80;
				}
				if(i + 1 == fichero.get(inicio).trim().length()) {
					tipoPaso = fichero.get(inicio).substring(index + 8, i + 1);
					i = 80;
				}
			}
			if(fichero.get(inicio).contains("PGM=SOF07200")) {
				tipoPaso = "PGM=SOF07200";
			}
			if(fichero.get(inicio).contains("PGM=SOFCHEC3")) {
				tipoPaso = "JSOFCHEC";
			}
			if (fichero.get(inicio).contains("NAME=IEBGCOPY")) {
				tipoPaso = "JIEBGENE - Caso particular";
			}
		}else {
			if (fichero.get(inicio).contains(" SORT")) {
				tipoPaso = "SORT";
			}
			if (fichero.get(inicio).contains("PGM=SOF07013")) {
				String numeroPaso = (WriterPasos.pasoS - 2 < 10) ? "0" + String.valueOf(WriterPasos.pasoS - 2) : String.valueOf(WriterPasos.pasoS - 2) ;
				if (WriterPasos.histPasos.containsKey(numeroPaso) 
						&& (WriterPasos.histPasos.get(numeroPaso)[0].equals("JFUSION") 
								|| WriterPasos.histPasos.get(numeroPaso)[0].equals("JGENCUAD"))) {
					tipoPaso = "ignore";
				}else {
					tipoPaso = "JBORRARF";
				}
				
			}
			if (fichero.get(inicio).contains("PGM=IDCAMS")) {
				tipoPaso = "IDCAMS";
			}
			if (fichero.get(inicio).contains("PGM=EQQEVPGM")) {
				tipoPaso = "JOPCREC";
			}
			if (fichero.get(inicio).contains("PGM=SOF07070")) {
				tipoPaso = "JPAUSA";
			}
			if (fichero.get(inicio).contains("PGM=IEBGENER")) {
				tipoPaso = "JIEBGEN2 - Caso particular";
			}
		}
		
		for(int i = inicio; i < fin; i++) {
			String linea = fichero.get(i);
			if (linea.length() >= 71) {
				linea = linea.substring(0, 71);
			}
			if(!(tipoPaso.equals("SORT") || tipoPaso.equals("PGM=SOF07200"))) {
				for (int j = i + 1; j < fichero.size() && fichero.get(j).startsWith(" "); j++) {
					if(fichero.get(j).endsWith("X")) {
						linea = linea + fichero.get(j).substring(0, fichero.get(j).length()-1).trim();
					}else {
						linea = linea + fichero.get(j).trim();
					}
					i = j;
				}
			}
			if (!linea.trim().equals("")) {
				pasos.add(linea);
			}
			
		}
		
		lineNumber = fin;
		return tipoPaso;
	}
	
	private static String pasoAdicional() {
		// TODO Auto-generated method stub
	int inicio = 0, fin = 0;
	String tipoPaso = "";
	
	for(int i = lineNumber; i < fichero.size(); i++) {
    	if(fichero.get(i).matches("[" + letraPaso + "][" + auxDecimal + "-9][" + auxUnidad + "-9] (.*)")
    			|| fichero.get(i).matches("[" + letraPaso + "][" + auxDecimal + 1 + "-9][0-9] (.*)")) {
    		if (inicio == 0 && !tipoPaso.equals("Inicio")) {
    			inicio= i;
    			pasoE = Integer.parseInt(fichero.get(i).substring(1,3));
	    		auxDecimal = pasoE / 10;
	    		auxUnidad  = pasoE - auxDecimal * 10 + 1;
    		}else {
    			fin = i;
    			i = fichero.size() + 1;
    		}
    	}
    	if(i + 1 == fichero.size()) {
    		fin = i;
    		i = fichero.size() + 1;
    	}
    }
	
	if(fichero.get(inicio).contains("SOFCHEC3")) {
		lineNumber = fin;
		tipoPaso = "JFUSION";
	}else {
		tipoPaso = "JGENQUAD";
	}
	return tipoPaso;
}
	
	private static void escribeJJOB(BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JJOB--------------------------
	    FileReader ficheroJJOB = new FileReader("C:\\Cortex\\Plantillas\\JJOB.txt");
	    BufferedReader lectorJJOB = new BufferedReader(ficheroJJOB);
	    //----------------Variables------------------------------------------
	    String linea;
	    int contadorLinea = 0;
	    //----------------M�todo---------------------------------------------
	    while((linea = lectorJJOB.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
			//Solo modificamos la l�nea 1 de la plantilla
	    	case 1:
				linea = linea.replace("AAAAAA", programa.substring(0,6));
				break;
			default:
				break;
			}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJJOB.close();
	    Avisos.LOGGER.log(Level.INFO, "A�adir las variables de cabecera");
	}
}

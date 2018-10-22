import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class mainApp {
	//--------------------- DATO A INTRODUCIR ------------------------------
	public static String programa = "BAN06BPCL";
	//----------------------------------------------------------------------
	
	//--------------------- Variables Programa -----------------------------
	public static Map<String, String> datos = new HashMap<String, String>();
	static String letraPaso = programa.substring(5,6);
	static int paso = -2;
	static ArrayList<String> fichero = new ArrayList<String>();
	static ArrayList<String> pasos = new ArrayList<String>();
	static int lineNumber = 0;
	static LectorPasos lectorPasos = new LectorPasos();
	static WriterPasos writerPasos = new WriterPasos();
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String extension = ".txt";
		String linea, tipoPaso;
		boolean seguir = true;
//-------------------------------------Ficheros-------------------------------------------------		
	    FileReader ficheroPCL = new FileReader("C:\\Cortex\\" + programa + extension);
	    BufferedReader lectorPCL = new BufferedReader(ficheroPCL);
	    
	    FileWriter ficheroCortex = new FileWriter("C:\\Cortex\\Migrados\\" + programa.substring(0,6) + ".txt");
	    BufferedWriter writerCortex = new BufferedWriter(ficheroCortex);
//----------------------------------------------------------------------------------------------	    
	     
//------------------------------------PROGRAMA--------------------------------------------------
	    	    
//------------- Escribimos la cabecera
	    escribeJJOB(writerCortex);

//------------- Pasamos todo el fichero a un arraylist	
	    while((linea = lectorPCL.readLine())!=null) {
	    	fichero.add(linea);	 
	    }
	    lectorPCL.close();
// ------------ Aislamos el paso
	    while (seguir) {
		    tipoPaso = aislamientoDePaso();
		    //Verificación aislamiento
		    //Verificación aislamiento
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
				writerPasos.writeDB2(datos, letraPaso, paso, writerCortex);
				break;	
			case "NAME=MAILTXT":
				datos = lectorPasos.leerPaso(pasos);
				writerPasos.writeMAILTXT(datos, letraPaso, paso, writerCortex);
				break;
			case "SORT":
				datos = lectorPasos.leerPaso(pasos);
				writerPasos.writeSORT(datos, letraPaso, paso, writerCortex);
				break;
			default:
				writerCortex.write("**************************************************");
				writerCortex.newLine();
				writerCortex.write("*******AÑADIR PLANTILLA: " + tipoPaso + "*********");
				writerCortex.newLine();
				writerCortex.write("**************************************************");
				writerCortex.newLine();
				break;
			}
		    System.out.println("------- Datos sacados del Paso:  -------");
		    datos.forEach((k,v) -> System.out.println(k + "-" + v));
		    System.out.println("----------------------------------------");
		    paso += 2;
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
	    	String numeroPaso = (paso < 10) ? "0" + String.valueOf(paso) : String.valueOf(paso) ;
			String numeroPasoSiguiente = (paso + 2 < 10) ? "0" + String.valueOf(paso+2) : String.valueOf(paso+2);
	    	//Buscamos que la linea empiece por I+paso
	    	if(fichero.get(i).startsWith(letraPaso + String.valueOf(numeroPaso))) {
	    		inicio = i;
	    	}
	    	if(fichero.get(i).startsWith(letraPaso + String.valueOf(numeroPasoSiguiente))) {
	    		fin = i;
	    		i = fichero.size() + 1;
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
		
		for(int i = inicio; i < fin; i++) {
			String linea = fichero.get(i);
			if (linea.length() >= 71) {
				linea = linea.substring(0, 71);
			}
			for (int j = i + 1; j < fichero.size() && fichero.get(j).startsWith(" "); j++) {
				if(fichero.get(j).endsWith("X")) {
					linea = linea + fichero.get(j).substring(0, fichero.get(j).length()-1).trim();
				}else {
					linea = linea + fichero.get(j).trim();
				}
				i = j;
			}
			pasos.add(linea);
		}
		
		index = fichero.get(inicio).indexOf("PATTERN");
		if (index != -1) {
			for(int i = index; i < fichero.get(inicio).trim().length(); i++) {
				if(fichero.get(inicio).charAt(i) == ',' || i + 1 == fichero.get(inicio).trim().length()) {
					tipoPaso = fichero.get(inicio).substring(index + 8, i);
					i = 80;
				}				
			}
		}else {
			if (fichero.get(inicio).contains(" SORT ")) {
				tipoPaso = "SORT";
			}else {
				if (fichero.get(inicio).contains("PGM=SOF07013")) {
					tipoPaso = "JBORRARF";
				}
			}
		}
		
		lineNumber = fin;
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
	    //----------------Método---------------------------------------------
	    while((linea = lectorJJOB.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
			//Solo modificamos la línea 1 de la plantilla
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
	}
}

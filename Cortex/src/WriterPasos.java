import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;


public class WriterPasos {
	static Avisos  avisos = new Avisos();
	MetodosAux metodosAux = new MetodosAux();
	public static int pasoS = -1;
	public static Map<String, String[]> histPasos = new HashMap<String, String[]>();
	
	

	public void writeDB2(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla DB2--------------------------
	    FileReader ficheroDB2 = new FileReader("C:\\Cortex\\Plantillas\\JDB2.txt");
	    BufferedReader lectorDB2 = new BufferedReader(ficheroDB2);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"DB2", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    
	    
	    //----------------Método---------------------------------------------

	    //--------------- Miramos si hay archivos para borrar antes de ejecutar:
	    for (int i = 1; datos.containsKey("Borrar" + String.valueOf(i)); i++) {
	    	if(!datos.get("Borrar" + String.valueOf(i)).equals("No")) {
	    		writeJBORRAF(datos, numeroPaso, i, letraPaso, writerCortex, pasoE);
	    	}
	    }
	    
	    //---------------- Escribimos la plantilla JDB2
	    while((linea = lectorDB2.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
	    		linea = linea.replace("NOMPROGR", datos.get("PGM"));
	    		if(!datos.containsKey("PARDB2")) {
	    			linea = linea.replace(datos.get("PGM") + ",", datos.get("PGM"));
	    		}
				break;
	    	case 3:
	    		if(!datos.containsKey("PARDB2")) {
	    			continue;
	    		}
	    		if (metodosAux.checkLiteralesPARDB2(datos.get("PARDB2").replace("*&", "-&"))) {
	    			writerCortex.write("****** LITERALES EN LOS PARAMETROS DEL PROGRAMA: " + datos.get("PARDB2") + "*****");
	    	    	writerCortex.newLine();
	    	    	Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Literales en el programa: "
	    	    			+ datos.get("PARDB2"));
	    		}
	    		
	    		linea = linea.replace("&VAR1-&VAR2-..." , metodosAux.tratarLiteralesPARDB2(datos.get("PARDB2").replace("*&", "-&")));
			default:
				break;
	    	}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorDB2.close();
//--------------- Miramos si hay ficheros Cortex:
	    if (datos.containsKey("FCortex")) {
	    	writeFCortex(datos, writerCortex);
	    }
//--------------- Miramos si hay ficheros de entrada:
	    for (int i = 1; datos.containsKey("Entrada" + String.valueOf(i)); i++) {
	    	writeJFICHENT(datos, numeroPaso, i, letraPaso, writerCortex, pasoE);
	    }
//--------------- Miramos si hay ficheros de Salida:
	    for (int i = 1; datos.containsKey("Salida" + String.valueOf(i)); i++) {
	    	writeJFICHSAL(datos, numeroPaso, i, letraPaso, writerCortex, pasoE);
	    }
//--------------- Miramos si hay reportes para informar:
	    writeReports(datos, writerCortex, pasoE, letraPaso);
//--------------- Miramos si hay IF o ENDIF:
	    writeIF(datos, writerCortex);
//--------------- Miramos si hay Comentarios:
	    writeComments(datos, writerCortex);
//--------------- Escribimos la alerta de Condicionales:
	    writeCondicionales(datos, writerCortex);    
	    
	}

	private void writeCondicionales(Map<String, String> datos, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		for (int i = 1; datos.containsKey("COND" + i); i++) {
			Avisos.LOGGER.log(Level.INFO, mainApp.letraPaso + String.valueOf(mainApp.pasoE) + " // Condicional " + i + ": " + datos.get("COND" + i) );
			System.out.println("Escribimos: " + "*** CONDICIONAL " + i + ": " + datos.get("COND" + i));
	    	writerCortex.write("*** CONDICIONAL " + i + ": " + datos.get("COND" + i));
	    	writerCortex.newLine();
		}
	}

	private void writeFCortex(Map<String, String> datos, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Escribimos: " + "//*--FICHERO CORTEX----------------------------------------------------");
    	writerCortex.write("//*--FICHERO CORTEX----------------------------------------------------");
    	writerCortex.newLine();
		StringBuffer fCortex = new StringBuffer("//" + datos.get("FCortex"));
		for(int j = fCortex.length(); j < 11; j++) {
			fCortex.append(" ");
		}
		System.out.println("Escribimos: " + fCortex + "DD *");
    	writerCortex.write(fCortex + "DD *");
    	writerCortex.newLine();
		for (int i = 1; datos.containsKey("FC" + i); i++) {
			System.out.println("Escribimos: " + datos.get("FC" + i));
	    	writerCortex.write(datos.get("FC" + i));
	    	writerCortex.newLine();
		}
	}

	public void writeIF(Map<String, String> datos, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		if (datos.containsKey("ENDIF")) {
			System.out.println("Escribimos: " + datos.get("ENDIF"));
	    	writerCortex.write(datos.get("ENDIF"));
	    	writerCortex.newLine();
		}
		if (datos.containsKey("IF")) {
			String valorIF = datos.get("IF");
			int index1 = valorIF.indexOf("IF " + mainApp.letraPaso);
			int index2 = valorIF.indexOf(".", index1);
			String pasoCortex = valorIF.substring(index1 + 4, index2);
			String[] infoPaso = WriterPasos.histPasos.get(pasoCortex.substring(0,2));
			if(!infoPaso[0].equals("IF referido a paso no migrado")) {
				valorIF = valorIF.replace(" " + mainApp.letraPaso + pasoCortex + ".", " " + mainApp.letraPaso + infoPaso[1] + "." + infoPaso[0] + ".");
			}else {
				valorIF = valorIF.replace("//", "/*");
    			Avisos.LOGGER.log(Level.INFO, mainApp.letraPaso + String.valueOf(mainApp.pasoE) + " // IF referido a paso no migrado: ");
				System.out.println("Escribimos: " + "***** IF REFERIDO A PASO NO MIGRADO *****");
		    	writerCortex.write("***** IF REFERIDO A PASO NO MIGRADO *****");
		    	writerCortex.newLine();
			}

			System.out.println("Escribimos: " + valorIF);
	    	writerCortex.write(valorIF);
	    	writerCortex.newLine();
		}
		if (datos.containsKey("ELSE")) {
			System.out.println("Escribimos: " + datos.get("ELSE"));
	    	writerCortex.write(datos.get("ELSE"));
	    	writerCortex.newLine();
		}
	}

	private void writeReports(Map<String, String> datos, BufferedWriter writerCortex, int pasoE, String letraPaso) throws IOException {
		// TODO Auto-generated method stub
		Map<String, String> infoRep = new HashMap<String, String>();
		for (int i = 1; datos.containsKey("Reporte" + String.valueOf(i)); i++) {
			infoRep = metodosAux.infoReportes(datos.get("Reporte" + String.valueOf(i)), pasoE, letraPaso);
			writerCortex.write("//*--REPORT-----------------------------------------------------------");
	    	writerCortex.newLine();
			writerCortex.write(infoRep.get("ReportKey"));
	    	writerCortex.newLine();
			infoRep.clear();
		}
	}

	public void writeComments(Map<String, String> datos, BufferedWriter writerCortex) throws IOException {
		for (int i = 1; datos.containsKey("Comentario" + String.valueOf(i)); i++) {
			System.out.println("Escribimos: " + "//" + datos.get("Comentario" + String.valueOf(i)));
	    	writerCortex.write("//" + datos.get("Comentario" + String.valueOf(i)));
	    	writerCortex.newLine();
	    }
	}

	private void writeComments(Map<String, String> datos, BufferedWriter writerCortex, String tipo, int numFich) throws IOException {
		// TODO Auto-generated method stub
		for (int i = 1; datos.containsKey(tipo + String.valueOf(numFich) + String.valueOf(i)); i++) {
			System.out.println("Escribimos: " + "//" + datos.get(tipo + String.valueOf(numFich) + String.valueOf(i)));
	    	writerCortex.write("//" + datos.get(tipo + String.valueOf(numFich) + String.valueOf(i)));
	    	writerCortex.newLine();
	    }
	}
	
	public void writeJFICHSAL(Map<String, String> datos, String numeroPaso, int i, String letraPaso,
			BufferedWriter writerCortex, int pasoE) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JFICHENT--------------------------
	    FileReader ficheroJFICHSAL = new FileReader("C:\\Cortex\\Plantillas\\JFICHSAL.txt");
	    BufferedReader lectorJFICHSAL = new BufferedReader(ficheroJFICHSAL);	
	    //----------------Variables------------------------------------------
	    Map<String, String> infoFich = new HashMap<String, String>();
	    String linea, nombre;
	    int contadorLinea = 0;
	    
	    //----------------Método---------------------------------------------
	    
	    writeComments(datos, writerCortex, "ComFichS", i);
	    
	    nombre = datos.get("Salida" + String.valueOf(i));
	    infoFich = metodosAux.infoFichero(pasoE, letraPaso, nombre);
	    for(int j = nombre.length(); j < 8; j++) {
			nombre += " ";
		}
	    if (!infoFich.containsKey("DUMMY")) {
		    while((linea = lectorJFICHSAL.readLine()) != null) {
		    	contadorLinea ++;
		    	switch (contadorLinea) {
		    	case 3:
		    		linea = linea.replace("DDNAME--", nombre);
		    		//REVISAR Z.
		    		linea = linea.replace("APL.XXXXXXXX.NOMMEM.&FAAMMDDV", infoFich.get("DSN"));
		    		break;
		    	case 5:
		    		if(infoFich.containsKey("MGMTCLAS")) {
		    			linea = linea.replace("EXLIXXXX", infoFich.get("MGMTCLAS"));
		    		}else {
		    			linea = linea.replace("// ", "//*");
		    		}
		    		break;
		    	case 6:
		    		if (infoFich.get("DISP").equals("NEW") && infoFich.get("LRECL").equals("LRECL")) {
		    			System.out.println("******* LRECL NO INFORMADO EN PROC - IR AL PROGRAMA*********");
		    			writerCortex.write("******* LRECL NO INFORMADO EN PROC - IR AL PROGRAMA*********");
						writerCortex.newLine();
		    		}else {
			    		linea = linea.replace("(LONGREG,(KKK,KK))", infoFich.get("Definicion"));
		    		}
		    		break;
		    	case 9:
		    		linea = linea.replace("DDNAME--", nombre);
		    		linea = linea.replace("APL.XXXXXXXX.NOMMEM.XP", infoFich.get("DSN"));
		    		break;
		    	case 11:
		    		if (infoFich.get("DISP").equals("TEMP") && infoFich.get("LRECL").equals("LRECL")) {
		    			System.out.println("******* LRECL NO INFORMADO EN PROC - IR AL PROGRAMA*********");
		    			writerCortex.write("******* LRECL NO INFORMADO EN PROC - IR AL PROGRAMA*********");
						writerCortex.newLine();
		    		}else {
			    		linea = linea.replace("(LONGREG,(KKK,KK))", infoFich.get("Definicion"));
		    		}
		    		break;
		    	case 14:
		    		linea = linea.replace("DDNAME--", nombre);
		    		linea = linea.replace("APL.XXXXXXXX.NOMMEM.&FAAMMDDV", infoFich.get("DSN"));
		    		break;
		    	case 16:
		    		if(infoFich.containsKey("MGMTCLAS")) {
		    			linea = linea.replace("EXLIXXXX", infoFich.get("MGMTCLAS"));
		    		}else {
		    			linea = linea.replace("// ", "//*");
		    		}
		    		break;
		    	case 17:
		    		if (infoFich.get("DISP").equals("MOD") && infoFich.get("LRECL").equals("LRECL")) {
		    			System.out.println("******* LRECL NO INFORMADO EN PROC - IR AL PROGRAMA*********");
		    			writerCortex.write("******* LRECL NO INFORMADO EN PROC - IR AL PROGRAMA*********");
						writerCortex.newLine();
		    		}else {
			    		linea = linea.replace("(LONGREG,(KKK,KK))", infoFich.get("Definicion"));
		    		}
		    		break;
		    	default:
					break;
		    	}
		    	
		    	if(infoFich.get("DISP").equals("NEW") && contadorLinea > 6) {
		    		//No escribimos el resto de ficheros (mod, temp)
		    		linea = "";
		    	}
		    	if(infoFich.get("DISP").equals("MOD") && contadorLinea < 12) {
		    		//No escribimos el resto de ficheros (new, temp)
		    		linea = "";
		    	}
		    	if(infoFich.get("DISP").equals("TEMP") && (contadorLinea < 7 || contadorLinea > 11)) {
		    		//No escribimos el resto de ficheros (new, mod)
		    		linea = "";
		    	}   
	    		    	
		    	if (!linea.equals("")) {
			    	System.out.println("Escribimos: " + linea);
			    	writerCortex.write(linea);
			    	writerCortex.newLine();
		    	}
		    }
	    }else {
	    	writerCortex.write("//*--DUMMY-----------------------------------------------------------");
	    	writerCortex.newLine();
			writerCortex.write(infoFich.get("DUMMY"));
	    	writerCortex.newLine();	
	    }
	    infoFich.clear();
	    lectorJFICHSAL.close();	 
	}

	public void writeJFICHENT(Map<String, String> datos, String numeroPaso, int i, String letraPaso, BufferedWriter writerCortex, int pasoE) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JFICHENT--------------------------
	    FileReader ficheroJFICHENT = new FileReader("C:\\Cortex\\Plantillas\\JFICHENT.txt");
	    BufferedReader lectorJFICHENT = new BufferedReader(ficheroJFICHENT);	
	    //----------------Variables------------------------------------------
	    String linea, nombre;
	    int contadorLinea = 0;
	    
	    //----------------Método---------------------------------------------
	    
	    writeComments(datos, writerCortex, "ComFichE", i);

	    Map<String, String> infoFich = new HashMap<String, String>();
	    nombre = datos.get("Entrada" + String.valueOf(i));
		for(int j = nombre.length(); j < 8; j++) {
			nombre += " ";
		}
	    infoFich = metodosAux.infoFichero(pasoE, letraPaso, nombre);
	    if (!infoFich.containsKey("DUMMY")) {
		    while((linea = lectorJFICHENT.readLine()) != null) {
		    	contadorLinea ++;
		    	if(i > 1 && contadorLinea == 1) {
		    		//No queremos que vuelva a escribir la primera línea de la plantilla
		    		continue;
		    	}
		    	switch (contadorLinea) {
		    	case 2:
		    		linea = linea.replace("DDNAME--", nombre);
		    		linea = linea.replace("APL.XXXXXXXX.NOMMEM.&FAAMMDDV", "Z." + infoFich.get("DSN"));
		    		break;
		    	default:
					break;
		    	}
		    	System.out.println("Escribimos: " + linea);
		    	writerCortex.write(linea);
		    	writerCortex.newLine();
		    }
	    }else {
	    	writerCortex.write("//*--DUMMY-----------------------------------------------------------");
	    	writerCortex.newLine();
			writerCortex.write(infoFich.get("DUMMY"));
	    	writerCortex.newLine();	
	    }
	    lectorJFICHENT.close();	 
	}

	public void writeJBORRAF(Map<String, String> datos, String numeroPaso, int i, String letraPaso, BufferedWriter writerCortex, int pasoE) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JBORRAF--------------------------
	    FileReader ficheroJBORRAF = new FileReader("C:\\Cortex\\Plantillas\\JBORRAF.txt");
	    BufferedReader lectorJBORRAF = new BufferedReader(ficheroJBORRAF);	
	    //----------------Variables------------------------------------------
	    String linea, nombre;
	    int contadorLinea = 0;
	    
	    //----------------Método---------------------------------------------
	    Map<String, String> infoFich = new HashMap<String, String>();
	    nombre = datos.get("Borrar" + String.valueOf(i));
	    infoFich = metodosAux.infoFichero(pasoE, letraPaso, nombre);
	    
	    while((linea = lectorJBORRAF.readLine()) != null) {
	    	contadorLinea ++;
	    	if(i > 1 && contadorLinea == 1) {
	    		//No queremos que vuelva a escribir la primera línea de la plantilla
	    		continue;
	    	}
	    	switch (contadorLinea) {
	    	case 2:
	    		if(i < 10) {
	    			linea = linea.replace("//---D-", "//" + letraPaso + numeroPaso + "D" + String.valueOf(i));
	    		}else {
	    			linea = linea.replace("//---D- ", "//" + letraPaso + numeroPaso + "D" + String.valueOf(i));
	    		}
	    		linea = linea.replace("APL.XXXXXXXX.NOMMEM.&FAAMMDDV", infoFich.get("DSN"));
	    		break;
	    	default:
				break;
	    	}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJBORRAF.close();	 
	}

	public void writeMAILTXT(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JJMAILTXT--------------------------
	    FileReader ficheroMAILTXT = new FileReader("C:\\Cortex\\Plantillas\\JMAILTXT.txt");
	    BufferedReader lectorMAILTXT = new BufferedReader(ficheroMAILTXT);	
	    //----------------Variables------------------------------------------
	    String linea, fi = "";
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"MAIL00", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    ArrayList<String> salida = new ArrayList<String>();
	    
	    
	    //----------------Método---------------------------------------------
	    while((linea = lectorMAILTXT.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
				break;
	    	case 4:
	    		linea = (datos.get("ASUNTO") == null) ? linea.trim() : linea.trim() + datos.get("ASUNTO");
	    		break;
	    	case 5:
	    		linea = (datos.get("ADREMI") == null) ? linea.trim() : linea.trim() + datos.get("ADREMI");
	    		break;
	    	case 6:
	    		if (datos.get("ADRDES") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("ADRDES", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 7:
	    		if (datos.get("ADRDE1") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("ADRDE1", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 8:
	    		if (datos.get("ADRDE2") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("ADRDE2", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 9:
	    		if (datos.get("ADRDE3") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("ADRDE3", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
					if (!fi.isEmpty()) {
		    			Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // No caben todos los correos - Falta añadir: " + fi);
		    			System.out.println("Escribimos: " + "***** No caben todos los correos. Revisar  ****");
		    	    	writerCortex.write("***** No caben todos los correos. Revisar  ****");
		    	    	writerCortex.newLine();
		    	    	fi = "";
					}
				}
    			break;
	    	case 10:
	    		linea = (datos.get("TIPMAIL") == null) ? linea.trim() : linea.trim() + datos.get("TIPMAIL");
	    		break;
	    	case 12:
	    		linea = (datos.get("UIDPETI") == null) ? linea.trim() : linea.trim() + datos.get("UIDPETI");
	    		break;
	    	case 13:
	    		linea = (datos.get("IDEANEX") == null) ? linea.trim() : linea.trim() + datos.get("IDEANEX");
	    		break;
	    	case 14:
	    		linea = (datos.get("DATAENVI") == null) ? linea.trim() : linea.trim() + datos.get("DATAENVI");
	    		break;
	    	case 15:
	    		linea = (datos.get("HORENVI") == null) ? linea.trim() : linea.trim() + datos.get("HORENVI");
	    		break;
	    	case 16:
				if (datos.get("DADA721") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("DADA721", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 17:
				if (datos.get("DADA722") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("DADA722", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 18:
				if (datos.get("DADA723") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("DADA723", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 19:
				if (datos.get("DADA724") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("DADA724", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 20:
	    		//Revisar nombre variable
				if (datos.get("DADA725") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("DADA725", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    		break;
			default:
				break;
			}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorMAILTXT.close();
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);   
	}

	public void writeSORT(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla SORT--------------------------
	    FileReader ficheroJSORT = new FileReader("C:\\Cortex\\Plantillas\\JSORT.txt");
	    BufferedReader lectorJSORT = new BufferedReader(ficheroJSORT);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"TSSORT", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    int i = 1;
	    Map<String, String> infoSortOut = new HashMap<String, String>();
	    //----------------Método---------------------------------------------
	    
	    infoSortOut = metodosAux.infoFichero(pasoE, letraPaso, "SORTOUT");
	    //---------------- Escribimos la plantilla JSORT
	    while((linea = lectorJSORT.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---D1", "//" + letraPaso + numeroPaso + "D" + String.valueOf(i));
	    		i++;
	    		linea = linea.replace("APL.XXXXXXXX.NOMMEM.&FAAMMDDV", infoSortOut.get("DSN"));
				break;
	    	case 4:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
	    		break;
	    	case 5:
	    		ArrayList<String> sortIn = new ArrayList<String>();
	    		sortIn =  metodosAux.infoSORTIN(pasoE, letraPaso);
	    		for (int j = 0; j < sortIn.size(); j++) {
	    			System.out.println("Escribimos: " + sortIn.get(j));
		    		writerCortex.write(sortIn.get(j));
		    		writerCortex.newLine();
	    		}
	    		linea = "";
	    		break;
	    	case 6:
	    		linea = linea.replace("APL.XXXXXXXX.NOMMEM.&FAAMMDDV", infoSortOut.get("DSN"));
	    		break;
	    	case 8:
	    		if(infoSortOut.containsKey("MGMTCLAS")) {
	    			linea = linea.replace("//*", "// ");
	    			linea = linea.replace("EXLIXXXX", infoSortOut.get("MGMTCLAS"));
	    		}
	    		break;
	    	case 9:
	    		linea = linea.replace("XXX", infoSortOut.get("LRECL"));
	    		break;
	    	case 10:
	    		linea = linea.replace("(LONGREG,(KKK,KK))", infoSortOut.get("Definicion"));
	    		break;
	    	case 12:
	    		for (int j = 1; datos.containsKey("SORT" + j); j++) {
	    			if (datos.get("SORT" + j).startsWith("SORT")) {
	    				linea = linea.replace("SORT FIELDS=(X,XX,XX,X)", datos.get("SORT" + j));
	    			}else {
	    				linea = "   " + datos.get("SORT" + j); 
	    			}
	    			System.out.println("Escribimos: " + linea);
	    	    	writerCortex.write(linea);
	    	    	writerCortex.newLine();
	    	    	linea = "";
	    		}
	    		
	    		break;
			default:
				break;
			}
	    	if (!linea.equals("")) {
	    		System.out.println("Escribimos: " + linea);
	    		writerCortex.write(linea);
	    		writerCortex.newLine();
	    	}
	    }
	    lectorJSORT.close();
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJFTPSEND(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JFTPSEND--------------------------
	    FileReader ficheroJFTPSEND = new FileReader("C:\\Cortex\\Plantillas\\JFTPSEND.txt");
	    BufferedReader lectorJFTPSEND = new BufferedReader(ficheroJFTPSEND);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"TSF01", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0, spaces = 0;
	    
	    //----------------Método---------------------------------------------
	    while((linea = lectorJFTPSEND.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
				break;
	    	case 3:
	    		//Calculamos cuantos espacios hay que añadir detrás para que no se muevan los comentarios de posición
	    		StringBuffer des = new StringBuffer("DES=" + datos.get("DES") + ",");
	    		spaces = 40 - des.length();
	    		for (int j = 0; j < spaces; j++) {
	    			des.append(" ");
	    		}
	    		linea = linea.replace("DES=destino,                            ", des);
				break;
	    	case 4:
	    		String dsn = metodosAux.infoFTP(pasoE, letraPaso, datos.get("FHOST"));
	    		if (dsn.equals("")){
	    			Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // DSN Fichero no encontrada ");
	    			System.out.println("***** REVISAR FICHERO - DSN NO ENCONTRADA *****");
	    	    	writerCortex.write("***** REVISAR FICHERO - DSN NO ENCONTRADA *****");
	    	    	writerCortex.newLine();
	    		}
	    	    StringBuffer host = new StringBuffer("HOST=Z." + dsn + ",");
	    	    spaces = 40 - host.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			host.append(" ");
	    		}
	    		linea = linea.replace("HOST=,                                  ", host);
	    		break;
	    	case 5:
	    		if(datos.get("FDEST").contains("_")) {
	    			String aux = "'" + datos.get("FDEST") + "'";
	    			datos.replace("FDEST", aux);
	    		}
	    		if(datos.get("FDEST").contains("_&")) {
	    			String aux = datos.get("FDEST");
	    			aux = aux.replaceAll("_&", "-&");
	    			datos.replace("FDEST", aux);
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ");
	    			System.out.println("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.write("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.newLine();
	    		}
	    		if(datos.get("FDEST").contains("*")) {
	    			System.out.println("******** FICHERO CON ASTERISCOS - AVISAR APLICACIÓN ******");
			    	writerCortex.write("******** FICHERO CON ASTERISCOS - AVISAR APLICACIÓN ******");
			    	writerCortex.newLine();
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Fichero con * - Avisar Aplicacion ");
	    		}
	    		StringBuffer fit = new StringBuffer("FIT=" + datos.get("FDEST"));
	    		if(datos.containsKey("MSG") || datos.containsKey("DIR")) {
	    			fit.append(",");
	    		}
	    		spaces = 40 - fit.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			fit.append(" ");
	    		}
	    		String aux = linea = linea.replace("FIT=nomfichred                          ", fit);
	    		if(aux.length() > 72) {
	    			linea = linea.replace("FIT=nomfichred                          <== nombre fich red", fit);
	    		}else {
	    			linea = linea.replace("FIT=nomfichred                          ", fit);
	    		}
	    		break;
	    	case 6:
	    		if(datos.containsKey("DIR")) {
	    			linea = linea.replace("//*", "// "); 
	    			StringBuffer dir = new StringBuffer("DIR='" + datos.get("DIR") + "'");
		    		if(datos.containsKey("MSG")) {
		    			dir.append(",");
		    		}
		    		spaces = 40 - dir.length();  		
		    		for (int j = 0; j < spaces; j++) {
		    			dir.append(" ");
		    		}
		    		linea = linea.replace("DIR=XXX                                 ", dir);
	    		}
	    		break;
	    	case 7:
	    		if(datos.containsKey("MSG")) {
	    			linea = linea.replace("//*", "// ");
	    			if(!datos.containsKey("MSG2")) { 
		    			StringBuffer msg = new StringBuffer("MSG='" + datos.get("MSG").replace("-", ",") + "'");
			    		spaces = 40 - msg.length();  		
			    		for (int j = 0; j < spaces; j++) {
			    			msg.append(" ");
			    		}
			    		linea = linea.replace("MSG='UE----,UE----'                     ", msg);
	    			}else {
	    				StringBuffer msg = new StringBuffer("MSG='" + datos.get("MSG").replace("-", ",")
	    						+ datos.get("MSG2").trim().replace("-", ",") + "'");
	    				if (msg.length() > 68) {
	    					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Variable MSG excede de la longitud permitida - " + msg);
	    	    			System.out.println("*****REVISAR LONGITUD MSG*****");
	    	    	    	writerCortex.write("*****REVISAR LONGITUD MSG*****");
	    	    	    	writerCortex.newLine();
	    				}
	    				linea = linea.replace("MSG='UE----,UE----'                     <== aviso usuario (opc.)", msg);
	    			}
	    		}
	    		break;
			default:
				break;
			}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJFTPSEND.close();		
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}
	
	public void writeJFTPREB(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JFTPREB--------------------------
	    FileReader ficheroJFTPREB = new FileReader("C:\\Cortex\\Plantillas\\JFTPREB.txt");
	    BufferedReader lectorJFTPREB = new BufferedReader(ficheroJFTPREB);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"F01", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0, spaces = 0;
	    Map<String, String> infoFtpReb = new HashMap<String, String>();
	    //----------------Método---------------------------------------------
	    
	    infoFtpReb = metodosAux.infoFtpReb(pasoE, letraPaso);
	    //----------------Método---------------------------------------------
	    while((linea = lectorJFTPREB.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
	    		linea = linea.replace("APL.XXXXXXXX.NOMMEM.&FAAMMDDV", infoFtpReb.get("DSN"));
				break;
	    	case 3:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
	    		break;
	    	case 4:
	    		//Calculamos cuantos espacios hay que añadir detrás para que no se muevan los comentarios de posición
	    		StringBuffer orig = new StringBuffer("ORIG=" + datos.get("ORIG") + ",");
	    		spaces = 39 - orig.length();
	    		for (int j = 0; j < spaces; j++) {
	    			orig.append(" ");
	    		}
	    		linea = linea.replace("ORIG=SERVIDOR_ORIGEN,                  ", orig);
				break;
	    	case 5:
	    		if(datos.get("FORIG").contains("_")) {
	    			String aux = "'" + datos.get("FORIG") + "'";
	    			datos.replace("FORIG", aux);
	    		}
	    		if(datos.get("FORIG").contains("_&")) {
	    			String aux = datos.get("FORIG");
	    			aux = aux.replaceAll("_&", "-&");
	    			datos.replace("FORIG", aux);
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ");
	    			System.out.println("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.write("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.newLine();
	    		}
	    		if(datos.get("FORIG").contains("*")) {
	    			System.out.println("******** FICHERO CON ASTERISCOS - AVISAR APLICACIÓN ******");
			    	writerCortex.write("******** FICHERO CON ASTERISCOS - AVISAR APLICACIÓN ******");
			    	writerCortex.newLine();
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Fichero con * - Avisar Aplicacion ");
	    		}
	    	    StringBuffer forig = new StringBuffer("FIT=" + datos.get("FORIG").replace("*", "****"));
	    	    if(datos.containsKey("DIR")) {
	    	    	forig.append(",");
	    	    }
	    	    spaces = 39 - forig.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			forig.append(" ");
	    		}
	    		linea = linea.replace("FIT=NOMFICHRED.TXT                     ", forig);
	    		break;
	    	case 6:
	    		if(datos.containsKey("DIR")) {
	    			linea = linea.replace("//*", "// "); 
	    			StringBuffer dir = new StringBuffer("DIR='" + datos.get("DIR") + "'");
	    			spaces = 38 - dir.length();  		
		    		for (int j = 0; j < spaces; j++) {
		    			dir.append(" ");
		    		}
		    		linea = linea.replace("DIR=XXX                               ", dir);
	    		}
	    		break;
	    	case 7:
	    		linea = linea.replace("APL.XXXXXXXX.NOMMEM.&FAAMMDDV", infoFtpReb.get("DSN"));
	    		break;
	    	case 9:
	    		if(infoFtpReb.containsKey("MGMTCLAS")) {
	    			linea = linea.replace("//*", "// ");
	    			linea = linea.replace("EXLIXXXX", infoFtpReb.get("MGMTCLAS"));
	    		}
	    		break;
	    	case 10:
	    		linea = linea.replace("(LONGREG,(KKK,KK))", infoFtpReb.get("Definicion"));
	    	case 11:
	    		linea = linea.replace("LONGREG", infoFtpReb.get("LRECL"));
			default:
				break;
			}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJFTPREB.close();
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}
	
	public void writeFTPDEL(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JFTDEL--------------------------
	    FileReader ficheroJFTPDEL = new FileReader("C:\\Cortex\\Plantillas\\JFTPDEL.txt");
	    BufferedReader lectorJFTPDEL = new BufferedReader(ficheroJFTPDEL);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"F01", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0, spaces = 0;
	    //----------------Método---------------------------------------------
	    
	    while((linea = lectorJFTPDEL.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
    			linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
    			break;
	    	case 3:	
	    		StringBuffer orig = new StringBuffer("ORIG=" + datos.get("ORIG") + ",");
	    		spaces = 40 - orig.length();
	    		for (int j = 0; j < spaces; j++) {
	    			orig.append(" ");
	    		}
	    		linea = linea.replace("ORIG=SERVIDOR_ORIGEN,                   ", orig);
				break;
	    	case 4:
	    		if(datos.get("FITXER").contains("_")) {
	    			String aux = "'" + datos.get("FITXER") + "'";
	    			datos.replace("FITXER", aux);
	    		}
	    		if(datos.get("FITXER").contains("_&")) {
	    			String aux = datos.get("FITXER");
	    			aux = aux.replaceAll("_&", "-&");
	    			datos.replace("FITXER", aux);
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ");
	    			System.out.println("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.write("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.newLine();
	    		}
	    		if(datos.get("FITXER").contains("*")) {
	    			System.out.println("******** FICHERO CON ASTERISCOS - AVISAR APLICACIÓN ******");
			    	writerCortex.write("******** FICHERO CON ASTERISCOS - AVISAR APLICACIÓN ******");
			    	writerCortex.newLine();
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Fichero con * - Avisar Aplicacion ");
	    		}
	    	    StringBuffer forig = new StringBuffer("FIT=" + datos.get("FITXER"));
	    	    if(datos.containsKey("DIR")) {
	    	    	forig.append(",");
	    	    }
	    	    spaces = 40 - forig.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			forig.append(" ");
	    		}
	    		linea = linea.replace("FIT=nomfichred                          ", forig);
	    		break;
	    	case 5:
	    		if(datos.containsKey("DIR")) {
	    			linea = linea.replace("//*", "// "); 
	    			StringBuffer dir = new StringBuffer("DIR='" + datos.get("DIR") + "'");
	    			spaces = 40 - dir.length();  		
		    		for (int j = 0; j < spaces; j++) {
		    			dir.append(" ");
		    		}
		    		linea = linea.replace("DIR=XXX                                 ", dir);
	    		}
	    		break;
			default:
				break;
			}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJFTPDEL.close();
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJMAILMSG(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JMAILMSG--------------------------
	    FileReader ficheroJMAILMSG = new FileReader("C:\\Cortex\\Plantillas\\JMAILMSG.txt");
	    BufferedReader lectorJMAILMSG = new BufferedReader(ficheroJMAILMSG);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"TSMAIL04", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0, spaces = 0;
	    //----------------Método---------------------------------------------    
	    
	    while((linea = lectorJMAILMSG.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
				break;
	    	case 3:
	    		//Calculamos cuantos espacios hay que añadir detrás para que no se muevan los comentarios de posición
	    		StringBuffer dsnName = new StringBuffer("DSNAME=Z." + metodosAux.infoDSN(pasoE, letraPaso, "ENTRA1") + ",");
	    		spaces = 42 - dsnName.length();
	    		for (int j = 0; j < spaces; j++) {
	    			dsnName.append(" ");
	    		}
	    		linea = linea.replace("DSNAME=,                                  ", dsnName);
				break;
	    	case 4:
	    		if(datos.get("SORTIDA").contains("_")) {
	    			String aux = "'" + datos.get("SORTIDA") + "'";
	    			datos.replace("SORTIDA", aux);
	    		}
	    		if(datos.get("SORTIDA").contains("_&")) {
	    			String aux = datos.get("SORTIDA");
	    			aux = aux.replaceAll("_&", "-&");
	    			datos.replace("SORTIDA", aux);
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ");
	    			System.out.println("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.write("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.newLine();
	    		}
	    		//Calculamos cuantos espacios hay que añadir detrás para que no se muevan los comentarios de posición
	    		StringBuffer fitTxt = new StringBuffer("FITTXT=" + datos.get("SORTIDA"));
	    		spaces = 42 - fitTxt.length();
	    		for (int j = 0; j < spaces; j++) {
	    			fitTxt.append(" ");
	    		}
	    		linea = linea.replace("FITTXT=                                   ", fitTxt);
				break;
			default:
				break;
			}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJMAILMSG.close();	
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJFTPSAPP(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JFTPSAPP--------------------------
	    FileReader ficheroJFTPSAPP = new FileReader("C:\\Cortex\\Plantillas\\JFTPSAPP.txt");
	    BufferedReader lectorJFTPSAPP = new BufferedReader(ficheroJFTPSAPP);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"TSF02", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0, spaces = 0;
	    
	    while((linea = lectorJFTPSAPP.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
				break;
	    	case 3:
	    		//Calculamos cuantos espacios hay que añadir detrás para que no se muevan los comentarios de posición
	    		StringBuffer des = new StringBuffer("DES=" + datos.get("DES") + ",");
	    		spaces = 40 - des.length();
	    		for (int j = 0; j < spaces; j++) {
	    			des.append(" ");
	    		}
	    		linea = linea.replace("DES=destino,                            ", des);
				break;
	    	case 4:
	    	    StringBuffer sqlin = new StringBuffer("SQLIN='" + datos.get("SQLIN") + "',");
	    	    spaces = 40 - sqlin.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			sqlin.append(" ");
	    		}
	    		linea = linea.replace("SQLIN=,                                 ", sqlin);
	    		break;
	    	case 5:
	    		if(datos.get("FDEST").contains("_")) {
	    			String aux = "'" + datos.get("FDEST") + "'";
	    			datos.replace("FDEST", aux);
	    		}
	    		if(datos.get("FDEST").contains("_&")) {
	    			String aux = datos.get("FDEST");
	    			aux = aux.replaceAll("_&", "-&");
	    			datos.replace("FDEST", aux);
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ");
	    			System.out.println("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.write("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.newLine();
	    		}
	    		if(datos.get("FDEST").contains("*")) {
	    			System.out.println("******** FICHERO CON ASTERISCOS - AVISAR APLICACIÓN ******");
			    	writerCortex.write("******** FICHERO CON ASTERISCOS - AVISAR APLICACIÓN ******");
			    	writerCortex.newLine();
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Fichero con * - Avisar Aplicacion ");
	    		}
	    		StringBuffer fit = new StringBuffer("FIT=" + datos.get("FDEST"));
	    		if(datos.containsKey("MSG") || datos.containsKey("DIR")) {
	    			fit.append(",");
	    		}
	    		spaces = 40 - fit.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			fit.append(" ");
	    		}
	    		linea = linea.replace("FIT=nomfichred                          ", fit);
	    		break;
	    	case 6:
	    		if(datos.containsKey("DIR")) {
	    			linea = linea.replace("//*", "// "); 
	    			StringBuffer dir = new StringBuffer("DIR='" + datos.get("DIR") + "'");
		    		if(datos.containsKey("MSG")) {
		    			dir.append(",");
		    		}
		    		spaces = 40 - dir.length();  		
		    		for (int j = 0; j < spaces; j++) {
		    			dir.append(" ");
		    		}
		    		linea = linea.replace("DIR=XXX                                 ", dir);
	    		}
	    		break;
	    	case 7:
	    		if(datos.containsKey("MSG")) {
	    			linea = linea.replace("//*", "// ");
	    			if(!datos.containsKey("MSG2")) { 
		    			StringBuffer msg = new StringBuffer("MSG='" + datos.get("MSG").replace("-", ",") + "'");
			    		spaces = 40 - msg.length();  		
			    		for (int j = 0; j < spaces; j++) {
			    			msg.append(" ");
			    		}
			    		linea = linea.replace("MSG='UE----,UE----'                     ", msg);
	    			}else {
	    				StringBuffer msg = new StringBuffer("MSG='" + datos.get("MSG").replace("-", ",")
	    						+ datos.get("MSG2").trim().replace("-", ",") + "'");
	    				if (msg.length() > 68) {
	    					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Variable MSG excede de la longitud permitida - " + msg);
	    	    			System.out.println("*****REVISAR LONGITUD MSG*****");
	    	    	    	writerCortex.write("*****REVISAR LONGITUD MSG*****");
	    	    	    	writerCortex.newLine();
	    				}
	    				linea = linea.replace("MSG='UE----,UE----'                     <== aviso usuario (opc.)", msg);
	    			}
	    		}
	    		break;	
	    	default:
				break;	
	    	}
		    System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJFTPSAPP.close();
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJMAILANX(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JMAILANX--------------------------
	    FileReader ficheroJMAILANX = new FileReader("C:\\Cortex\\Plantillas\\JMAILANX.txt");
	    BufferedReader lectorJMAILANX = new BufferedReader(ficheroJMAILANX);	
	    //----------------Variables------------------------------------------
	    String linea;
		String fi = "";
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"MAIL06", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    
	    ArrayList<String> salida = new ArrayList<String>();
	    
	    //----------------Método---------------------------------------------
	    while((linea = lectorJMAILANX.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
				break;
	    	case 3:
	    		StringBuffer dsname = new StringBuffer("DSNAME=Z." + metodosAux.infoDSN(pasoE, letraPaso, "ENTRA1") + ", ");
	    		linea = linea.replace("DSNAME=,               ", dsname);
	    		break;
	    	case 4:
	    		if(datos.get("SORTIDA").contains("_")) {
	    			String aux = "'" + datos.get("SORTIDA") + "'";
	    			datos.replace("SORTIDA", aux);
	    		}
	    		if(datos.get("SORTIDA").contains("_&")) {
	    			String aux = datos.get("SORTIDA");
	    			aux = aux.replaceAll("_&", "-&");
	    			datos.replace("SORTIDA", aux);
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ");
	    			System.out.println("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.write("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.newLine();
	    		}
	    		String aux = linea.replace("------.TXT      ", datos.get("SORTIDA") +" ");
	    		if (aux.length() > 72) {
	    			linea = linea.replace("FITTXT=------.TXT      <= debe ser idÃ©ntico al informado en IDEANEX", "FITTXT=" + datos.get("SORTIDA"));
				}else {
		    		linea = linea.replace("------.TXT      ", datos.get("SORTIDA")+" ");
				}
	    		break;
	    	case 6:
	    		linea = (datos.get("ASUNTO") == null) ? linea.trim() : linea.trim() + datos.get("ASUNTO");
	    		break;
	    	case 7:
	    		linea = (datos.get("ADREMI") == null) ? linea.trim() : linea.trim() + datos.get("ADREMI");
	    		break;
	    	case 8:
	    		if (datos.get("ADRDES") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("ADRDES", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 9:
	    		if (datos.get("ADRDE1") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("ADRDE1", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 10:
	    		if (datos.get("ADRDE2") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("ADRDE2", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 11:
	    		if (datos.get("ADRDE3") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("ADRDE3", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
					if (!fi.isEmpty()) {
		    			Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // No caben todos los correos - Falta añadir: " + fi);
		    			System.out.println("Escribimos: " + "***** No caben todos los correos. Revisar  ****");
		    	    	writerCortex.write("***** No caben todos los correos. Revisar  ****");
		    	    	writerCortex.newLine();
		    	    	fi = "";
					}
				}
    			break;
	    	case 12:
	    		linea = (datos.get("TIPMAIL") == null) ? linea.trim() : linea.replace("???", datos.get("TIPMAIL"));
	    		break;
	    	case 14:
	    		linea = (datos.get("UIDPETI") == null) ? linea.trim() : linea.trim() + datos.get("UIDPETI");
	    		break;
	    	case 15:
	    		linea = (datos.get("SORTIDA") == null) ? linea.trim() : linea.replace("------.TXT", datos.get("SORTIDA"));
	    		break;
	    	case 16:
	    		linea = (datos.get("DATENVI") == null) ? linea.trim() : linea.trim() + datos.get("DATENVI");
	    		break;
	    	case 17:
	    		linea = (datos.get("HORENVI") == null) ? linea.trim() : linea.trim() + datos.get("HORENVI");
	    		break;
	    	case 18:
				if (datos.get("DADA721") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("DADA721", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
	    		break;
	    	case 19:
				if (datos.get("DADA722") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("DADA722", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
	    		break;
	    	case 20:
				if (datos.get("DADA723") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("DADA723", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
				break;
	    	case 21:
				if (datos.get("DADA724") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("DADA724", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
				break;
	    	case 22:
				if (datos.get("DADA725") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("DADA725", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
				break;
	    	default:
				break;
			}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJMAILANX.close();	
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}
	
	public void writeJFIVACIO(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JFIVACIO--------------------------
	    FileReader ficheroJFIVACIO = new FileReader("C:\\Cortex\\Plantillas\\JFIVACIO.txt");
	    BufferedReader lectorJFIVACIO = new BufferedReader(ficheroJFIVACIO);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"A00TS", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    //----------------Método---------------------------------------------
	    			    
	    //----------------Método---------------------------------------------
	    while((linea = lectorJFIVACIO.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:			    		
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);			    		
	    		linea = linea.replace("APL.XXXXXXXX.NOMMEM.&FAAMMDDV", metodosAux.infoDSN(pasoE, letraPaso, "ENTRADA"));
				break;			    	
			}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJFIVACIO.close();		
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJOPCREC(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JOPCREC--------------------------
	    FileReader ficheroJOPCREC = new FileReader("C:\\Cortex\\Plantillas\\JOPCREC.txt");
	    BufferedReader lectorJOPCREC = new BufferedReader(ficheroJOPCREC);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"OPCREC", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;	    
	    
	    //----------------Método---------------------------------------------
	    while((linea = lectorJOPCREC.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
				break;
	    	case 4:
	    		linea = linea.replace("'APL.XXXXXXXX.NOMMEM'", "'Z." + datos.get("SRSTAT") + "'");
	    		break;
	    	default:
				break;
			}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJOPCREC.close();	
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}
	
	public void writeJFUSION(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JFUSION--------------------------
	    FileReader ficheroJFUSION = new FileReader("C:\\Cortex\\Plantillas\\JFUSION.txt");
	    BufferedReader lectorJFUSION = new BufferedReader(ficheroJFUSION);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"JFUSION", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    //----------------Método---------------------------------------------    
	    
	    metodosAux.infoJFUSION(datos, pasoE, letraPaso);
	    
	    
	    while((linea = lectorJFUSION.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 3:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
	    		linea = linea.replace("APL.XXXXXXXX.NOMMEM.&FAAMMDDV", datos.get("DSN"));
				break;
	    	case 5:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
	    		break;
	    	case 7:
	    		linea = linea.replace("&APLIC", datos.get("APL"));
	    		break;
	    	case 8:
	    		linea = linea.replace("&NOMQDRE", datos.get("QUADRE"));
	    		break;
	    	case 10:
	    		for(int i = 1; datos.containsKey("DSN" + i); i++) {
	    			String lineaEditada = linea;
	    			lineaEditada = lineaEditada.replace("TSFUS01.DD----1", "TSFUS01." + datos.get("FICH" + i));
	    			if (i < 10) {
	    				lineaEditada = lineaEditada.replace("&DUMY01", "&DUMY0" + i);
	    			}else {
	    				lineaEditada = lineaEditada.replace("&DUMY01", "&DUMY" + i);
	    			} 
	    			lineaEditada = lineaEditada.replace("APL.XXXXXXXX.NOMMEM.&GENE1", "Z." + datos.get("DSN" + i));
	    			
	    			System.out.println("Escribimos: " + lineaEditada);
	    	    	writerCortex.write(lineaEditada);
	    	    	writerCortex.newLine();
	    		}
	    		linea = "";
	    		break;
	    	case 12:
	    		linea = "";
	    		for(int i = 1; datos.containsKey("FICHA" + i); i++){
	    			linea = datos.get("FICHA" + i);
	    			
	    			System.out.println("Escribimos: " + linea);
	    	    	writerCortex.write(linea);
	    	    	writerCortex.newLine();	
	    		}
	    		linea = "";
	    		break;
	    	case 13:
	    		linea = "";
	    		break;
	    	case 14:
	    		StringBuffer nameFich = new StringBuffer(datos.get("SALIDA"));
	    		for (int i = nameFich.length(); i < 9; i++) {
	    			nameFich.append(" ");
	    		}
	    		linea = linea.replace("//DDSAL--  ", "//" + nameFich);
	    		linea = linea.replace("APL.XXXXXXXX.NOMMEM.&FAAMMDDV", datos.get("DSN"));
	    		break;
	    	case 16:
	    		if (datos.containsKey("MGMTCLAS")) {
	    			linea = linea.replace("//*", "// ");
	    			linea = linea.replace("EXLIXXXX", datos.get("MGMTCLAS"));
	    		}
	    		break;
	    	case 17:
	    		linea = linea.replace("(LONGREG,(KKK,KK))", datos.get("Definicion"));
	    		break;
	    	case 19:
	    		linea = linea.replace("//---IF-", "//" + letraPaso + numeroPaso + "IF1");
	    		break;
	    	case 21:
	    		pasoS += 2;
	    		numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    		for(int i = 1; datos.containsKey("DSN" + i); i++) {
	    			String lineaEditada = linea;
	    			lineaEditada = lineaEditada.replace("//---D-", "//" + letraPaso + numeroPaso + "D" + i);
	    			lineaEditada = lineaEditada.replace("APL.XXXXXXXX.NOMMEM.&GENE1", "Z." + datos.get("DSN" + i));
	    			
	    			System.out.println("Escribimos: " + lineaEditada);
	    	    	writerCortex.write(lineaEditada);
	    	    	writerCortex.newLine();
	    		}
	    		linea = "";
	    		break;
	    	case 22:
	    		numeroPaso = (pasoS - 2 < 10) ? "0" + String.valueOf(pasoS - 2) : String.valueOf(pasoS - 2) ;
	    		linea = linea.replace("//E---IF-", "//E" + letraPaso + numeroPaso + "IF1");
	    		break;
			default:
				break;
			}
	    	if(!linea.equals("")) {
		    	System.out.println("Escribimos: " + linea);
		    	writerCortex.write(linea);
		    	writerCortex.newLine();
	    	}
	    }
	    lectorJFUSION.close();
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}
	
	public void writeJGENCUAD(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JGENCUAD--------------------------
	    FileReader ficheroJGENCUAD = new FileReader("C:\\Cortex\\Plantillas\\JGENCUAD.txt");
	    BufferedReader lectorJGENCUAD = new BufferedReader(ficheroJGENCUAD);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"JGENCUAD", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    //----------------Método---------------------------------------------    
	    
	    metodosAux.infoJFUSION(datos, pasoE, letraPaso);
	    
	    
	    while((linea = lectorJGENCUAD.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 3:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
	    		linea = linea.replace("APL.XXXXXXXX.NOMMEM.&FAAMMDDV", datos.get("DSN"));
				break;
	    	case 5:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
	    		break;
	    	case 7:
	    		linea = linea.replace("&APLIC", datos.get("APL"));
	    		break;
	    	case 8:
	    		linea = linea.replace("&NOMQDRE", datos.get("QUADRE"));
	    		break;
	    	case 9:
	    		for(int i = 1; datos.containsKey("DSN" + i); i++) {
	    			String lineaEditada = linea;
	    			lineaEditada = lineaEditada.replace("TSGENQ1.DD----1", "TSGENQ1." + datos.get("FICH" + i)); 
	    			lineaEditada = lineaEditada.replace("APL.XXXXXXXX.NOMMEM.&GENE1", "Z." + datos.get("DSN" + i));
	    			
	    			System.out.println("Escribimos: " + lineaEditada);
	    	    	writerCortex.write(lineaEditada);
	    	    	writerCortex.newLine();
	    		}
	    		linea = "";
	    		break;
	    	case 11:
	    		linea = "";
	    		for(int i = 1; datos.containsKey("FICHA" + i); i++){
	    			linea = datos.get("FICHA" + i);
	    			
	    			System.out.println("Escribimos: " + linea);
	    	    	writerCortex.write(linea);
	    	    	writerCortex.newLine();	
	    		}
	    		linea = "";
	    		break;
	    	case 12:
	    		linea = "";
	    		break;
	    	case 13:
	    		StringBuffer nameFich = new StringBuffer(datos.get("SALIDA"));
	    		for (int i = nameFich.length(); i < 9; i++) {
	    			nameFich.append(" ");
	    		}
	    		linea = linea.replace("//DDSAL--  ", "//" + nameFich);
	    		linea = linea.replace("APL.XXXXXXXX.NOMMEM.&FAAMMDDV", datos.get("DSN"));
	    		break;
	    	case 15:
	    		if (datos.containsKey("MGMTCLAS")) {
	    			linea = linea.replace("//*", "// ");
	    			linea = linea.replace("EXLIXXXX", datos.get("MGMTCLAS"));
	    		}
	    		break;
	    	case 16:
	    		linea = linea.replace("(LONGREG,(KKK,KK))", datos.get("Definicion"));
	    		break;
	    	case 18:
	    		linea = linea.replace("//---IF-", "//" + letraPaso + numeroPaso + "IF1");
	    		break;
	    	case 20:
	    		pasoS += 2;
	    		numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    		for(int i = 1; datos.containsKey("DSN" + i); i++) {
	    			String lineaEditada = linea;
	    			lineaEditada = lineaEditada.replace("//---D-", "//" + letraPaso + numeroPaso + "D" + i);
	    			lineaEditada = lineaEditada.replace("APL.XXXXXXXX.NOMMEM.&GENE1", "Z." + datos.get("DSN" + i));
	    			
	    			System.out.println("Escribimos: " + lineaEditada);
	    	    	writerCortex.write(lineaEditada);
	    	    	writerCortex.newLine();
	    		}
	    		linea = "";
	    		break;
	    	case 21:
	    		numeroPaso = (pasoS - 2 < 10) ? "0" + String.valueOf(pasoS - 2) : String.valueOf(pasoS - 2) ;
	    		linea = linea.replace("//E---IF-", "//E" + letraPaso + numeroPaso + "IF1");
	    		break;
			default:
				break;
			}
	    	if(!linea.equals("")) {
		    	System.out.println("Escribimos: " + linea);
		    	writerCortex.write(linea);
		    	writerCortex.newLine();
	    	}
	    }
	    lectorJGENCUAD.close();		
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJPAPYRUS(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JPAPYRUS--------------------------
	    FileReader ficheroJPAPYRUS = new FileReader("C:\\Cortex\\Plantillas\\JPAPYRUS.txt");
	    BufferedReader lectorJPAPYRUS = new BufferedReader(ficheroJPAPYRUS);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"PAP06", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;	    
	    
	    //----------------Método---------------------------------------------
	    while((linea = lectorJPAPYRUS.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
				break;
	    	case 3:
	    		linea = linea.replace("DFA=XXXXXXXX", "DFA=" + datos.get("DFA"));
	    		break;
	    	case 4:
	    		linea = linea.replace("DESTI=XXXXXX", "DESTI=" + datos.get("DESTI"));
	    		break;
	    	case 5:
	    		linea = linea.replace("FORMU=XXXX", "FORMU=" + datos.get("FORMU"));
	    		break;
	    	case 6:
	    		StringBuffer entre = new StringBuffer(datos.get("ENTRE"));
	    		if (datos.containsKey("POSTPRO") || datos.containsKey("DISTRIB") || datos.containsKey("B")) {
	    			entre.append(",");
	    		}
	    		for (int k = entre.length(); k < 12; k++) {
	    			entre.append(" ");
	    		}
	    		linea = linea.replace("ENTREGAR=XXXXXXXXXX  ", "ENTREGAR=" + entre);
	    	case 7:
	    		if (datos.containsKey("POSTPRO")) {
	    			linea = linea.replace("//*", "// ");
	    			StringBuffer postpro = new StringBuffer(datos.get("POSTPRO"));
	    			if (datos.containsKey("DISTRIB") || datos.containsKey("B")) {
		    			postpro.append(",");
		    		}
	    			for (int k = postpro.length(); k < 13; k++) {
		    			postpro.append(" ");
		    		}
	    			linea = linea.replace("POSTPRO=X,           ", "POSTPRO=" + postpro);	
	    		}
	    		break;
	    	case 8:
	    		if (datos.containsKey("DISTRIB")) {
	    			linea = linea.replace("//*", "// ");
	    			StringBuffer distrib = new StringBuffer(datos.get("DISTRIB"));
	    			if (datos.containsKey("B")) {
		    			distrib.append(",");
		    		}
	    			for (int k = distrib.length(); k < 13; k++) {
		    			distrib.append(" ");
		    		}
	    			linea = linea.replace("DISTRIB=X,           ", "DISTRIB=" + distrib);	
	    		}
	    		break;
	    	case 9:
	    		if (datos.containsKey("B")) {
	    			linea = linea.replace("B=X", "B=" + datos.get("B"));	
	    		}
	    		break;
	    	case 10:
	    		linea = linea.replace("APL.XXXXXXXX.NOMMEM.&FAAMMDDV", "Z." + metodosAux.infoDSN(pasoE, letraPaso, "ENTRADA"));
	    		break;
	    	default:
				break;
			}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJPAPYRUS.close();
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJPAUSA(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JPAUSA--------------------------
	    FileReader ficheroJPAUSA = new FileReader("C:\\Cortex\\Plantillas\\JPAUSA.txt");
	    BufferedReader lectorJPAUSA = new BufferedReader(ficheroJPAUSA);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"JPAUSA", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    //----------------Método---------------------------------------------    
	    while((linea = lectorJPAUSA.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---P", "//" + letraPaso + numeroPaso);
	    		linea = linea.replace("TTT", datos.get("PARM"));
				break;
	    	default:
				break;
			}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJPAUSA.close();		
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJSOFCHEC(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JSOFCHEC--------------------------
	    FileReader ficheroJSOFCHEC = new FileReader("C:\\Cortex\\Plantillas\\JSOFCHEC.txt");
	    BufferedReader lectorJSOFCHEC = new BufferedReader(ficheroJSOFCHEC);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"JSOFCHEC", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    // Borrado de ficheros de salida
	    for (int i = 1; datos.containsKey("Borrar" + String.valueOf(i)); i++) {
	    	if(!datos.get("Borrar" + String.valueOf(i)).equals("No")) {
	    		writeJBORRAF(datos, numeroPaso, i, letraPaso, writerCortex, pasoE);
	    	}
	    }
	    
	    int contadorLinea = 0;
	    while((linea = lectorJSOFCHEC.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 3:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
	    		if (datos.containsKey("COND1") && datos.get("COND1").equals("EVEN")){
	    			linea = linea.replace("SOFCHEC3,", "SOFCHEC3," + "COND=((EVEN)),");
	    		}
	    		
				break;
	    	case 4:
	    		String[] valores = datos.get("PARDB2").split(" ");
	    		String[] plantillas = {"&NOMQDRE", "&FECHAQ", "&OPCIONQ"};
	    		String lineaEditada = "";
	    		int i = 0;
	    		for(i=0; i < plantillas.length; i++) {
		    		if(valores[i].startsWith("&")) {
		    			linea = linea.replace(plantillas[i], valores[i]);
		    		}else{
		    			lineaEditada = "**   SET " + plantillas[i].substring(1) +"='" + valores[i] + "'";
		    			Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // añadir literial cabecera PROG=SOFCHEC3");
		    			System.out.println("Escribimos: " + lineaEditada);
		    	    	writerCortex.write(lineaEditada);
		    	    	writerCortex.newLine();
		    		}
	    		}
	    		for(int j = i; j < valores.length; j++) {
	    			if(valores[i].startsWith("&")) {
	    				linea = linea.trim() + "-" + valores[i];
	    			}else {
	    				lineaEditada = "**   SET " + valores[i] +"='" + valores[i] + "'";
	    				Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // añadir literial cabecera PROG=SOFCHEC3");
		    			System.out.println("Escribimos: " + lineaEditada);
		    	    	writerCortex.write(lineaEditada);
		    	    	writerCortex.newLine();
		    	    	linea = linea + "-&" + valores[i];
	    			}
	    		}
	    		break;
	    	case 5:
	    		if(datos.containsKey("Salida1") || datos.containsKey("Entrada1")) {
		    		linea = "";
	    		}
	    		break;
	    	default:
				break;
			}
	    	if (!linea.equals("")) {
		    	System.out.println("Escribimos: " + linea);
		    	writerCortex.write(linea);
		    	writerCortex.newLine();
	    	}
	    }
	    lectorJSOFCHEC.close();		
	    
	  //--------------- Miramos si hay ficheros de entrada:
	    for (int i = 1; datos.containsKey("Entrada" + String.valueOf(i)); i++) {
	    	writeJFICHENT(datos, numeroPaso, i, letraPaso, writerCortex, pasoE);
	    }
	  //--------------- Miramos si hay ficheros de Salida:
	    for (int i = 1; datos.containsKey("Salida" + String.valueOf(i)); i++) {
	    	writeJFICHSAL(datos, numeroPaso, i, letraPaso, writerCortex, pasoE);
	    }
	    
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJSOFINF(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JSOFINF--------------------------
	    FileReader ficheroJSOFINF = new FileReader("C:\\Cortex\\Plantillas\\JSOFINF.txt");
	    BufferedReader lectorJSOFINF = new BufferedReader(ficheroJSOFINF);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"INF00", numeroPaso};
	    histPasos.put(numeroPasoE, valor); 
	    int contadorLinea = 0;
	    while((linea = lectorJSOFINF.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
				break;
	    	case 3:
	    		linea = linea.replace("XXXXXXXX,", "'" + datos.get("TAULA") + "',");
	    		break;
	    	case 4:
	    		linea = linea.replace("XXXXXXXX,", "'" + datos.get("COL") + "',");
	    		break;
	    	case 5:
	    		int aux = Integer.parseInt(datos.get("LONG"));
	    		linea = linea.replace("XXXX,", Integer.toString(aux) + ",");
	    		break;
	    	case 6:
	    		linea = linea.replace("&YYYYYY,", datos.get("DATBAIXA") + ",");
	    		break;
	    	case 7:
	    		linea = linea.replace("XXXXXXX", "'" + datos.get("TIPREM") + "'");
	    		break;
	    	default:
				break;
			}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJSOFINF.close();
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJFTPS123(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JSOFINF--------------------------
	    FileReader ficheroJFTPS123 = new FileReader("C:\\Cortex\\Plantillas\\JFTPS123.txt");
	    BufferedReader lectorJFTPS123 = new BufferedReader(ficheroJFTPS123);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"TSF02", numeroPaso};
	    histPasos.put(numeroPasoE, valor); 
	    int contadorLinea = 0, spaces = 0;
	    while((linea = lectorJFTPS123.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
				break;
	    	case 3:
	    		StringBuffer des = new StringBuffer("DES=" + datos.get("DES") + ",");
	    	    spaces = 40 - des.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			des.append(" ");
	    		}
	    		linea = linea.replace("DES=destino,                            ", des);
	    		break;
	    	case 4:
	    		StringBuffer sqlin = new StringBuffer("SQLIN='" + datos.get("SQLIN") + "',");
	    	    spaces = 40 - sqlin.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			sqlin.append(" ");
	    		}
	    		linea = linea.replace("SQLIN=,                                 ", sqlin);
	    		break;
	    	case 5:
	    		if(datos.get("FDEST").contains("_")) {
	    			String aux = "'" + datos.get("FDEST") + "'";
	    			datos.replace("FDEST", aux);
	    		}
	    		if(datos.get("FDEST").contains("_&")) {
	    			String aux = datos.get("FDEST");
	    			aux = aux.replaceAll("_&", "-&");
	    			datos.replace("FDEST", aux);
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ");
	    			System.out.println("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.write("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.newLine();
	    		}
	    		if(datos.get("FDEST").contains("*")) {
	    			System.out.println("******** FICHERO CON ASTERISCOS - AVISAR APLICACIÓN ******");
			    	writerCortex.write("******** FICHERO CON ASTERISCOS - AVISAR APLICACIÓN ******");
			    	writerCortex.newLine();
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Fichero con * - Avisar Aplicacion ");
	    		}
	    		StringBuffer fit = new StringBuffer("FIT=" + datos.get("FDEST"));
	    		if(datos.containsKey("MSG") || datos.containsKey("DIR")) {
	    			fit.append(",");
	    		}
	    		spaces = 40 - fit.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			fit.append(" ");
	    		}
	    		linea = linea.replace("FIT=nomfichred                          ", fit);
	    		break;
	    	case 6:
	    		if(datos.containsKey("DIR")) {
	    			linea = linea.replace("//*", "// "); 
	    			StringBuffer dir = new StringBuffer("DIR='" + datos.get("DIR") + "'");
		    		if(datos.containsKey("MSG")) {
		    			dir.append(",");
		    		}
		    		spaces = 40 - dir.length();  		
		    		for (int j = 0; j < spaces; j++) {
		    			dir.append(" ");
		    		}
		    		linea = linea.replace("DIR=XXX                                 ", dir);
	    		}
	    		break;
	    	case 7:
	    		if(datos.containsKey("MSG")) {
	    			linea = linea.replace("//*", "// ");
	    			if(!datos.containsKey("MSG2")) { 
		    			StringBuffer msg = new StringBuffer("MSG='" + datos.get("MSG").replace("-", ",") + "'");
			    		spaces = 40 - msg.length();  		
			    		for (int j = 0; j < spaces; j++) {
			    			msg.append(" ");
			    		}
			    		linea = linea.replace("MSG='UE----,UE----'                     ", msg);
	    			}else {
	    				StringBuffer msg = new StringBuffer("MSG='" + datos.get("MSG").replace("-", ",")
	    						+ datos.get("MSG2").trim().replace("-", ",") + "'");
	    				if (msg.length() > 68) {
	    					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Variable MSG excede de la longitud permitida - " + msg);
	    	    			System.out.println("*****REVISAR LONGITUD MSG*****");
	    	    	    	writerCortex.write("*****REVISAR LONGITUD MSG*****");
	    	    	    	writerCortex.newLine();
	    				}
	    				linea = linea.replace("MSG='UE----,UE----'                     <== aviso usuario (opc.)", msg);
	    			}
	    		}
	    		break;
	    	default:
				break;
			}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJFTPS123.close();	
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJFTPVER(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JFTPVER--------------------------
	    FileReader ficheroJFTPVER = new FileReader("C:\\Cortex\\Plantillas\\JFTPVER.txt");
	    BufferedReader lectorJFTPVER = new BufferedReader(ficheroJFTPVER);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"F01", numeroPaso};
	    histPasos.put(numeroPasoE, valor); 
	    int contadorLinea = 0, spaces = 0;
	    while((linea = lectorJFTPVER.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
				break;
	    	case 3:
	    		StringBuffer orig = new StringBuffer("ORIG=" + datos.get("ORIG") + ",");
	    	    spaces = 40 - orig.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			orig.append(" ");
	    		}
	    		linea = linea.replace("ORIG=SERVIDOR_ORIGEN,                   ", orig);
	    		break;
	    	case 4:
	    		if(datos.get("FITXER").contains("_")) {
	    			String aux = "'" + datos.get("FITXER") + "'";
	    			datos.replace("FITXER", aux);
	    		}
	    		if(datos.get("FITXER").contains("_&")) {
	    			String aux = datos.get("FITXER");
	    			aux = aux.replaceAll("_&", "-&");
	    			datos.replace("FITXER", aux);
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ");
	    			System.out.println("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.write("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.newLine();
	    		}
	    		if(datos.get("FITXER").contains("*")) {
	    			System.out.println("******** FICHERO CON ASTERISCOS - AVISAR APLICACIÓN ******");
			    	writerCortex.write("******** FICHERO CON ASTERISCOS - AVISAR APLICACIÓN ******");
			    	writerCortex.newLine();
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Fichero con * - Avisar Aplicacion ");
	    		}
	    		StringBuffer fit = new StringBuffer("FIT=" + datos.get("FITXER"));
	    		if(datos.containsKey("DIR")) {
	    			fit.append(",");
	    		}
	    		spaces = 40 - fit.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			fit.append(" ");
	    		}
	    		linea = linea.replace("FIT=nomfichred                          ", fit);
	    		break;
	    	case 5:
	    		if(datos.containsKey("DIR")) {
	    			linea = linea.replace("//*", "// "); 
	    			StringBuffer dir = new StringBuffer("DIR='" + datos.get("DIR") + "'");
		    		spaces = 40 - dir.length();  		
		    		for (int j = 0; j < spaces; j++) {
		    			dir.append(" ");
		    		}
		    		linea = linea.replace("DIR=XXX                                 ", dir);
	    		}
	    		break;
	    	default:
				break;
			}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJFTPVER.close();	
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJMAIL123(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JMAIL123--------------------------
	    FileReader ficheroJMAIL123 = new FileReader("C:\\Cortex\\Plantillas\\JMAIL123.txt");
	    BufferedReader lectorJMAIL123 = new BufferedReader(ficheroJMAIL123);	
	    //----------------Variables------------------------------------------
	    String linea;
		String fi = "";
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"MAIL06", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    
	    ArrayList<String> salida = new ArrayList<String>();
	    
	    //----------------Método---------------------------------------------
	    while((linea = lectorJMAIL123.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
				break;
	    	case 3:
	    		StringBuffer SQLIN = new StringBuffer("SQLIN='" + datos.get("SQLIN") + "',");
	    		for (int k = SQLIN.length(); k < 42; k++) {
	    			SQLIN.append(" ");
	    		}
	    		linea = linea.replace("SQLIN='XXXXXXXX_XX',                      ", SQLIN);
	    		break;
	    	case 4:
	    		if(datos.get("SORTIDA").contains("_")) {
	    			String aux = "'" + datos.get("SORTIDA") + "'";
	    			datos.replace("SORTIDA", aux);
	    		}
	    		if(datos.get("SORTIDA").contains("_&")) {
	    			String aux = datos.get("SORTIDA");
	    			aux = aux.replaceAll("_&", "-&");
	    			datos.replace("SORTIDA", aux);
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ");
	    			System.out.println("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.write("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.newLine();
	    		}
	    		StringBuffer fitTxt = new StringBuffer("FITTXT=" + datos.get("SORTIDA"));
	    		for (int k =  fitTxt.length(); k < 42; k++) {
	    			fitTxt.append(" ");
	    		}
	    		linea = linea.replace("FITTXT=                                   ", fitTxt);
	    		break;
	    	case 6:
	    		linea = (datos.get("ASUNTO") == null) ? linea.trim() : linea.trim() + datos.get("ASUNTO");
	    		break;
	    	case 7:
	    		linea = (datos.get("ADREMI") == null) ? linea.trim() : linea.trim() + datos.get("ADREMI");
	    		break;
	    	case 8:
	    		if (datos.get("ADRDES") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("ADRDES", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 9:
	    		if (datos.get("ADRDE1") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("ADRDE1", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 10:
	    		if (datos.get("ADRDE2") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("ADRDE2", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
    			break;
	    	case 11:
	    		if (datos.get("ADRDE3") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("ADRDE3", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
					if (!fi.isEmpty()) {
		    			Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // No caben todos los correos - Falta añadir: " + fi);
		    			System.out.println("Escribimos: " + "***** No caben todos los correos. Revisar  ****");
		    	    	writerCortex.write("***** No caben todos los correos. Revisar  ****");
		    	    	writerCortex.newLine();
		    	    	fi = "";
					}
				}
    			break;
	    	case 12:
	    		linea = (datos.get("TIPMAIL") == null) ? linea.trim() : linea.replace("???", datos.get("TIPMAIL"));
	    		break;
	    	case 14:
	    		linea = (datos.get("UIDPETI") == null) ? linea.trim() : linea.trim() + datos.get("UIDPETI");
	    		break;
	    	case 15:
	    		linea = (datos.get("SORTIDA") == null) ? linea.trim() : linea.replace("------.TXT", datos.get("SORTIDA"));
	    		break;
	    	case 16:
	    		linea = (datos.get("DATENVI") == null) ? linea.trim() : linea.trim() + datos.get("DATENVI");
	    		break;
	    	case 17:
	    		linea = (datos.get("HORENVI") == null) ? linea.trim() : linea.trim() + datos.get("HORENVI");
	    		break;
	    	case 18:
				if (datos.get("DADA721") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("DADA721", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
	    		break;
	    	case 19:
				if (datos.get("DADA722") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("DADA722", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
	    		break;
	    	case 20:
				if (datos.get("DADA723") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("DADA723", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
				break;
	    	case 21:
				if (datos.get("DADA724") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("DADA724", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
				break;
	    	case 22:
				if (datos.get("DADA725") == null && fi == "") {
					linea = linea.trim();
				}
				else {
					salida = MetodosAux.ComprobarTamañoLinea("DADA725", linea, fi, datos); 
					linea = salida.get(0);
					fi = salida.get(1);
				}
				break;
	    	default:
				break;
			}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJMAIL123.close();	
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}
	
	public void writeJIEBGENE(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JIEBGCOPY--------------------------
	    FileReader ficheroJIEBGENE = new FileReader("C:\\Cortex\\Plantillas\\JIEBGENE.txt");
	    BufferedReader lectorJIEBGENE = new BufferedReader(ficheroJIEBGENE);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    Map<String, String> infoFichOut = new HashMap<String, String>();
	    Map<String, String> infoFichIn = new HashMap<String, String>();
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"INF00", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    infoFichIn = metodosAux.infomultiDSN(pasoE, letraPaso, "SYSUT1");
	    infoFichOut = metodosAux.infoFichero(pasoE, letraPaso, "SYSUT2");
	    int contadorLinea = 0;
	    
	    while((linea = lectorJIEBGENE.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
	    		linea = linea.replace("APL.YYYYYYYY.NOMMEM2.&FAAMMDDV", metodosAux.infoDSN(pasoE, letraPaso, "SYSUT2"));
	    		break;
	    	case 3:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
	    		break;
	    	case 4:
	    		for(int i = 0; infoFichIn.containsKey("DSN" + i); i++) {
	    			String lineaEditada = linea;
	    			if(i==0) {
	    				lineaEditada = lineaEditada.replace("APL.XXXXXXXX.NOMMEM1.&FAAMMDDV", infoFichIn.get("DSN" + i));
	    			}else {
	    				lineaEditada = lineaEditada.replace("//SYSUT1", "//      ");
	    				lineaEditada = lineaEditada.replace("APL.XXXXXXXX.NOMMEM1.&FAAMMDDV", infoFichIn.get("DSN" + i));
	    			}
	    			System.out.println("Escribimos: " + lineaEditada);
	    	    	writerCortex.write(lineaEditada);
	    	    	writerCortex.newLine();
	    		}
	    		linea = "";
	    		break;
	    	case 5:
	    		linea = linea.replace("APL.YYYYYYYY.NOMMEM2.&FAAMMDDV", metodosAux.infoDSN(pasoE, letraPaso, "SYSUT2"));
	    		break;
	    	case 7:
	    		linea = linea.replace("APL.XXXXXXXX.NOMMEM1.&FAAMMDDV,", metodosAux.infoDSN(pasoE, letraPaso, "SYSUT1"));
	    		break;
	    	case 8:
	    		if(infoFichOut.containsKey("MGMTCLAS")) {
	    			linea = linea.replace("EXLIXXXX", infoFichOut.get("MGMTCLAS"));
	    		}else {
	    			linea = linea.replace("// ", "//*");
	    		}
	    		break; 
	    	default:
				break;
			}
	    	if(!linea.equals("")) {
		    	System.out.println("Escribimos: " + linea);
		    	writerCortex.write(linea);
		    	writerCortex.newLine();
	    	}
	    }
	    lectorJIEBGENE.close();	
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJBORRARFPasos(Map<String, String> datos, String letraPaso, int pasoE,
			BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JBORRAF--------------------------
	    FileReader ficheroJBORRAF = new FileReader("C:\\Cortex\\Plantillas\\JBORRAF.txt");
	    BufferedReader lectorJBORRAF = new BufferedReader(ficheroJBORRAF);	
	    //----------------Variables------------------------------------------
	    String linea;
	    ArrayList<String> lineaProc;
	    int contadorLinea = 0;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    //----------------Método---------------------------------------------
	    
	    while((linea = lectorJBORRAF.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---D-", "//" + letraPaso + numeroPaso);
	    		break;
	    	default:
				break;
	    	}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJBORRAF.close();	 
	    Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + "// DSN no encontrada PROC - Acudir a: " );
	    System.out.println("Escribimos: Revisar SYSIN Cortex");
    	writerCortex.write("***** REVISAR SYSIN Cortex");
    	writerCortex.newLine();
    	if (mainApp.withProc) {
    		lineaProc = metodosAux.buscaInfoProc(pasoE, letraPaso, "SYSIN");
		    for(int i = 0; i < lineaProc.size(); i++) {
		    	Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + lineaProc.get(i));
		    	System.out.println("Escribimos: " + lineaProc.get(i).replace("//", "**"));
		    	writerCortex.write(lineaProc.get(i).replace("//", "**"));
		    	writerCortex.newLine();
		    }
    	}
	    
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJFIVERDS(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JFIVERDS--------------------------
	    FileReader ficheroJFIVERDS = new FileReader("C:\\Cortex\\Plantillas\\JFIVERDS.txt");
	    BufferedReader lectorJFIVERDS = new BufferedReader(ficheroJFIVERDS);	
	    //----------------Variables------------------------------------------
	    String linea;
	    ArrayList<String> lineaProc;
	    int contadorLinea = 0;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"A00TS", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    //----------------Método---------------------------------------------
	    lineaProc = metodosAux.buscaInfoProc(pasoE, letraPaso, "SYSIN");
	    
	    while((linea = lectorJFIVERDS.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
	    		break;
	    	default:
				break;
	    	}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJFIVERDS.close();	 
	    Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + "// DSN no encontrada PROC - Acudir a: " );
	    System.out.println("Escribimos: Revisar SYSIN Cortex");
    	writerCortex.write("***** REVISAR SYSIN Cortex");
    	writerCortex.newLine();
	    for(int i = 0; i < lineaProc.size(); i++) {
	    	Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + lineaProc.get(i));
	    	System.out.println("Escribimos: " + lineaProc.get(i).replace("//", "**"));
	    	writerCortex.write(lineaProc.get(i).replace("//", "**"));
	    	writerCortex.newLine();
	    }
	    
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}
	
	public void writeJFTBSEND(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JFTBSEND--------------------------
	    FileReader ficheroJFTBSEND = new FileReader("C:\\Cortex\\Plantillas\\JFTBSEND.txt");
	    BufferedReader lectorJFTBSEND = new BufferedReader(ficheroJFTBSEND);	
	    //----------------Variables------------------------------------------
	    String linea;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"FTBSEND", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0, spaces = 0;
	    
	    while((linea = lectorJFTBSEND.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
	    		break;
	    	case 3:
	    		StringBuffer des = new StringBuffer("DES=" + datos.get("DES") + ",");
	    		spaces = 40 - des.length();
	    		for (int j = 0; j < spaces; j++) {
	    			des.append(" ");
	    		}
	    		linea = linea.replace("DES=destino,                            ", des);
				break;
	    	case 4:
	    		String dsn = metodosAux.infoFTP(pasoE, letraPaso, datos.get("FHOST"));
	    		if (dsn.equals("")){
	    			Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // DSN Fichero no encontrada ");
	    			System.out.println("***** REVISAR FICHERO - DSN NO ENCONTRADA *****");
	    	    	writerCortex.write("***** REVISAR FICHERO - DSN NO ENCONTRADA *****");
	    	    	writerCortex.newLine();
	    		}
	    	    StringBuffer host = new StringBuffer("HOST=Z." + dsn + ",");
	    	    spaces = 40 - host.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			host.append(" ");
	    		}
	    		linea = linea.replace("HOST=,                                  ", host);
	    		break;
	    	case 5:
	    		if(datos.get("FDEST").contains("_")) {
	    			String aux = "'" + datos.get("FDEST") + "'";
	    			datos.replace("FDEST", aux);
	    		}
	    		if(datos.get("FDEST").contains("_&")) {
	    			String aux = datos.get("FDEST");
	    			aux = aux.replaceAll("_&", "-&");
	    			datos.replace("FDEST", aux);
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Revisar fichero -  contiene _& ");
	    			System.out.println("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.write("*****REVISAR FICHERO CON _&*****");
	    	    	writerCortex.newLine();
	    		}
	    		if(datos.get("FDEST").contains("*")) {
	    			System.out.println("******** FICHERO CON ASTERISCOS - AVISAR APLICACIÓN ******");
			    	writerCortex.write("******** FICHERO CON ASTERISCOS - AVISAR APLICACIÓN ******");
			    	writerCortex.newLine();
					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Fichero con * - Avisar Aplicacion ");
	    		}
	    		StringBuffer fit = new StringBuffer("FIT=" + datos.get("FDEST"));
	    		if(datos.containsKey("MSG") || datos.containsKey("DIR")) {
	    			fit.append(",");
	    		}
	    		spaces = 40 - fit.length();  		
	    		for (int j = 0; j < spaces; j++) {
	    			fit.append(" ");
	    		}
	    		String aux = linea = linea.replace("FIT=nomfichred                          ", fit);
	    		if(aux.length() > 72) {
	    			linea = linea.replace("FIT=nomfichred                          <== nombre fich red", fit);
	    		}else {
	    			linea = linea.replace("FIT=nomfichred                          ", fit);
	    		}
	    		break;
	    	case 6:
	    		if(datos.containsKey("DIR")) {
	    			linea = linea.replace("//*", "// "); 
	    			StringBuffer dir = new StringBuffer("DIR='" + datos.get("DIR") + "'");
		    		if(datos.containsKey("MSG")) {
		    			dir.append(",");
		    		}
		    		spaces = 40 - dir.length();  		
		    		for (int j = 0; j < spaces; j++) {
		    			dir.append(" ");
		    		}
		    		linea = linea.replace("DIR=XXX                                 ", dir);
	    		}
	    		break;
	    	case 7:
	    		if(datos.containsKey("MSG")) {
	    			linea = linea.replace("//*", "// ");
	    			if(!datos.containsKey("MSG2")) { 
		    			StringBuffer msg = new StringBuffer("MSG='" + datos.get("MSG").replace("-", ",") + "'");
			    		spaces = 40 - msg.length();  		
			    		for (int j = 0; j < spaces; j++) {
			    			msg.append(" ");
			    		}
			    		linea = linea.replace("MSG='UE----,UE----'                     ", msg);
	    			}else {
	    				StringBuffer msg = new StringBuffer("MSG='" + datos.get("MSG").replace("-", ",")
	    						+ datos.get("MSG2").trim().replace("-", ",") + "'");
	    				if (msg.length() > 68) {
	    					Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Variable MSG excede de la longitud permitida - " + msg);
	    	    			System.out.println("*****REVISAR LONGITUD MSG*****");
	    	    	    	writerCortex.write("*****REVISAR LONGITUD MSG*****");
	    	    	    	writerCortex.newLine();
	    				}
	    				linea = linea.replace("MSG='UE----,UE----'                     <== aviso usuario (opc.)", msg);
	    			}
	    		}
	    		break;	
	    	default:
				break;
			}
	    	if(!linea.equals("")) {
		    	System.out.println("Escribimos: " + linea);
		    	writerCortex.write(linea);
		    	writerCortex.newLine();
	    	}
	    }
	    lectorJFTBSEND.close();	
	    writeReports(datos, writerCortex, pasoE, letraPaso);
	    writeIF(datos, writerCortex);
	    writeComments(datos, writerCortex);
	    writeCondicionales(datos, writerCortex);  
	}

	public void writeJPGM(Map<String, String> datos, String letraPaso, int pasoE, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JPGM--------------------------
	    FileReader ficheroJPGM = new FileReader("C:\\Cortex\\Plantillas\\JPGM.txt");
	    BufferedReader lectorJPGM = new BufferedReader(ficheroJPGM);	
	    //----------------Variables------------------------------------------
	    String linea, nombre;
	    pasoS += 2;
	    String numeroPaso = (pasoS < 10) ? "0" + String.valueOf(pasoS) : String.valueOf(pasoS) ;
	    String numeroPasoE = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    String[] valor = {"JPGM", numeroPaso};
	    histPasos.put(numeroPasoE, valor);
	    int contadorLinea = 0;
	    Map<String, String> infoFich = new HashMap<String, String>();
	    
	    //----------------Método---------------------------------------------
	    
	    //--------------- Miramos si hay archivos para borrar antes de ejecutar:
	    for (int i = 1; datos.containsKey("Salida" + String.valueOf(i)); i++) {
	    	nombre = datos.get("Salida" + String.valueOf(i));
		    infoFich = metodosAux.infoFichero(pasoE, letraPaso, nombre);
		    if(infoFich.get("DISP").equals("NEW")) {
		    	datos.replace("Borrar" + i, nombre);
		    	writeJBORRAF(datos, numeroPaso, i, letraPaso, writerCortex, pasoE);
		    }
	    }
	    
	    //---------------- Escribimos la plantilla JDB2
	    while((linea = lectorJPGM.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
	    		linea = linea.replace("NOMPROGR", datos.get("PGM"));
	    		if(!datos.containsKey("PARM")) {
	    			linea = linea.replace(datos.get("PGM") + ",PARM=&VAR1-&VAR2-...", datos.get("PGM"));
	    		}else {
	    			if (metodosAux.checkLiteralesPARDB2(datos.get("PARM").replace("*", "-"))) {
		    			writerCortex.write("****** LITERALES EN LOS PARAMETROS DEL PROGRAMA: " + datos.get("PARDB2") + "*****");
		    	    	writerCortex.newLine();
		    	    	Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Literales en el programa: "
		    	    			+ datos.get("PARM"));
		    		}
	    			linea = linea.replace("&VAR1-&VAR2-...", "('" + datos.get("PARM").replace("*", "-") + "')");
	    		}	    		
				break;
			default:
				break;
	    	}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJPGM.close();
//--------------- Miramos si hay ficheros Cortex:
	    if (datos.containsKey("FCortex")) {
	    	writeFCortex(datos, writerCortex);
	    }
//--------------- Miramos si hay ficheros de entrada:
	    for (int i = 1; datos.containsKey("Entrada" + String.valueOf(i)); i++) {
	    	writeJFICHENT(datos, numeroPaso, i, letraPaso, writerCortex, pasoE);
	    }
//--------------- Miramos si hay ficheros de Salida:
	    for (int i = 1; datos.containsKey("Salida" + String.valueOf(i)); i++) {
	    	writeJFICHSAL(datos, numeroPaso, i, letraPaso, writerCortex, pasoE);
	    }
//--------------- Miramos si hay reportes para informar:
	    writeReports(datos, writerCortex, pasoE, letraPaso);
//--------------- Miramos si hay IF o ENDIF:
	    writeIF(datos, writerCortex);
//--------------- Miramos si hay Comentarios:
	    writeComments(datos, writerCortex);
//--------------- Escribimos la alerta de Condicionales:
	    writeCondicionales(datos, writerCortex);    
	    
	}
}

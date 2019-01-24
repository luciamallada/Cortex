import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class MetodosAux {
	private static final String CONST_EMPTY = "";
	private static final String CONST_AMPERSAND = "&";
	private static final String CONST_EQUALS = "=";
	private static final String CONST_DEFINICION = "Definicion";
//	static Avisos  avisos = new Avisos();
	LectorPasos lectorPasos =  new LectorPasos();

	public boolean checkLiteralesPARDB2(String param) {
		// TODO Auto-generated method stub
		if (!param.startsWith(CONST_AMPERSAND)) {
			return true;
		}
		for(int i = 1; i < param.length(); i++) {
			if(param.charAt(i) == '-') {
				if(!(param.charAt(i+1) == '&')) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String tratarLiteralesPARDB2(String PARDB2) {
		// TODO Auto-generated method stub
		int index = PARDB2.indexOf(CONST_EQUALS);
		
		while (index != -1) {
			for (int i = index; i >= 0; i--) {
				if (PARDB2.charAt(i) == '-' || i == 0) {
					i = i > 0 ? i + 1 : i;
					String aux = PARDB2.substring(i, index + 1);
					PARDB2 = PARDB2.replace(aux, CONST_EMPTY);
					i = -1;
				}
			}
			index = PARDB2.indexOf(CONST_EQUALS, index+1);
		}
		if (index != -1) {
			
		}
		
		return PARDB2;
	}

	public ArrayList<String> buscaInfoProc(int pasoE, String letraPaso, String nombre) throws IOException{
		boolean seguir = true, buscar = false;	
		String linea;
		ArrayList<String> infoFichero = new ArrayList<String>();
		//----------------Fichero de plantilla JPROC--------------------------
	    FileReader ficheroPROC = new FileReader("C:\\Cortex\\PROC\\" + mainApp.programa.substring(0,6) + ".txt");
	    BufferedReader lectorPROC = new BufferedReader(ficheroPROC);
		//-----------------------------------------------------------------------
	    
	    String numeroPaso;    
	    numeroPaso = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    
	    while((linea = lectorPROC.readLine()) != null && seguir) {
	    	if(linea.startsWith("//" + letraPaso + numeroPaso)) {
	    		buscar = true;
	    	}
	    	if(buscar) {
	    		if(linea.startsWith("//" + nombre + " ")) {
	    			infoFichero.add(linea);
	    			linea = lectorPROC.readLine();
	    			while (linea.startsWith("//  ")) {
						infoFichero.add(linea);
						linea = lectorPROC.readLine();
					}
	    			buscar = false;
	    			seguir = false;
	    		}
	    	}	    	
	    }
	    lectorPROC.close();	
	    
	    return infoFichero;
	}
	
	public Map<String, String> infoFichero(int pasoE, String letraPaso, String nombre) throws IOException {
		// TODO Auto-generated method stub
		ArrayList<String> infoFichero = new ArrayList<String>();
		Map<String, String> infoFich = new HashMap<String, String>();
		
		if(mainApp.withProc) {
			infoFichero = buscaInfoProc(pasoE, letraPaso, nombre);
		    
		    String clave, valor;
	    	long primario = 0, secundario = 0, tamaño; 
	    	int numDSN = 0;
		    for(int j = 0; j < infoFichero.size(); j++) {
		    	int index = 1;
			    while (index != -1) {
					index = infoFichero.get(j).indexOf('=', index);
					if (index != -1) {
						clave = lectorPasos.leerClave(infoFichero.get(j), index);
						valor = lectorPasos.leerValor(infoFichero.get(j), index);
						valor = valor.replace("(",CONST_EMPTY);
						if (!clave.equals(CONST_EMPTY) && !valor.equals(CONST_EMPTY)) {
							if (clave.equals("DSN") && (infoFich.containsKey("DSN") || infoFich.containsKey("DSN" + numDSN))) {
								numDSN++;
								clave += numDSN;
							}
							infoFich.put(clave, valor);
						}
					}
					if (index != - 1) {
						index ++;
					}
				}
			    if(infoFichero.get(j).contains("SPACE") && !infoFich.get("SPACE").equals("CYL") && !infoFich.get("SPACE").equals("TRK")) {
			    	if (infoFich.containsKey("LRECL")) {
			    		int ini = 1, fin = 2;
				    	ini = infoFichero.get(j).lastIndexOf("(");
				    	for(int i = ini; i < infoFichero.get(j).length(); i++) {
				    		if(infoFichero.get(j).charAt(i) == ',') {
				    			fin = i;
				    			i = 1000;
				    		}
				    	}
				    	tamaño = Long.valueOf(infoFichero.get(j).substring(ini + 1, fin));
				    	primario = Long.parseLong(infoFich.get("SPACE")) * tamaño / Long.parseLong(infoFich.get("LRECL")) / 1000;
				    	primario = primario < 5 ? 10 : primario;
				    	
				    	ini = fin;
				    	for(int i = ini; i < infoFichero.get(j).length(); i++) {
				    		if(infoFichero.get(j).charAt(i) == ')') {
				    			fin = i;
				    			i = 1000;
				    		}
				    	}
				    	tamaño = Long.valueOf(infoFichero.get(j).substring(ini + 1, fin));
				    	secundario = Long.parseLong(infoFich.get("SPACE")) * tamaño / Long.parseLong(infoFich.get("LRECL")) / 1000;; 
				    	secundario = secundario < 3 ? 3 : secundario;
			    	}else {
						infoFich.put("LRECL","LRECL");
			    	}
			    }else {
			    	if(infoFichero.get(j).contains("SPACE") && infoFich.get("SPACE").equals("CYL")) {
			    		primario = 15;
			    		secundario = 1;
						Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " //Comprobar fichero CYL migrado correctamente");
			    		//infoFich.put("LRECL","CYL");
			    	}
			    }
		    }
		    
		    clave = CONST_DEFINICION;
		    valor = "(" + infoFich.get("LRECL") + ",(" + String.valueOf(primario) + "," + String.valueOf(secundario) + "))";
			infoFich.put(clave, valor);
			
			if(!infoFich.containsKey("DSN")) {
				clave = "DUMMY";
				valor = infoFichero.get(0);
				infoFich.put(clave, valor);
			}
			else {
				if(infoFich.get("DSN").endsWith("XP")) {
					infoFich.replace("DISP", "TEMP");
				}
			}
		}else {
			infoFich.put("DSN", nombre);
			infoFich.put(CONST_DEFINICION, "(LONGREG,(KKK,KK))");
			infoFich.put("DISP", "NEW");
			infoFich.put("LRECL", "NO");
		}
		System.out.println("------- Datos sacados del Fichero:  -------");
	    infoFich.forEach((k,v) -> System.out.println(k + "-" + v));
	    System.out.println("----------------------------------------");
		
		return infoFich;
	}

	public Map<String, String> infoReportes(String nombre, int pasoE, String letraPaso) throws IOException {
		// TODO Auto-generated method stub
		ArrayList<String> infoFichero = new ArrayList<String>();
		Map<String, String> infoRep = new HashMap<String, String>();
		String clave, valor;
		
		if (mainApp.withProc) {
			infoFichero = buscaInfoProc(pasoE, letraPaso, nombre);
			
			if (infoFichero.size() == 1) {
				clave = "ReportKey";
				valor = infoFichero.get(0);
				
			}else {
				clave = "ReportKey";
				valor = "* Error al leer línea de Reporte - Nombre reporte: " + nombre; 
				Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " // Error al leer el reporte - Nombre reporte: " + nombre);
			}
				
			infoRep.put(clave, valor);
		}else {
			clave = "ReportKey";
			valor = "Sacar reporte del PROC - nombre:" + nombre;
			infoRep.put(clave, valor);
		}
		return infoRep;
	}

	public String infoFTP(int pasoE, String letraPaso, String fhost) throws IOException {
		// TODO Auto-generated method stub
		boolean seguir = true, buscar = false;	
		@SuppressWarnings("unused")
		String linea, clave, valor = CONST_EMPTY;
		int index = 0;
		
		if(mainApp.withProc) {
			//----------------Fichero de plantilla JPROC--------------------------
		    FileReader ficheroPROC = new FileReader("C:\\Cortex\\PROC\\" + mainApp.programa.substring(0,6) + ".txt");
		    BufferedReader lectorPROC = new BufferedReader(ficheroPROC);
			//-----------------------------------------------------------------------
		    
		    String numeroPaso;    
		    numeroPaso = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
		    
		    while((linea = lectorPROC.readLine()) != null && seguir) {
		    	if(linea.startsWith("//" + letraPaso + numeroPaso)) {
		    		buscar = true;
		    	}
		    	if(buscar) {
		    		if(linea.contains(fhost + ".") && linea.contains("DSN=")){
		    			index = linea.indexOf('=', index);
		    			clave = lectorPasos.leerClave(linea, index);
						valor = lectorPasos.leerValor(linea, index);
						buscar = false;
						seguir = false;
		    		}
		    	}	    	
		    }
		    lectorPROC.close();
		}else {
			valor = fhost;
		}
		return valor;
	}

//	public Map<String, String> infoSort(int paso, String letraPaso) throws IOException {
//		// TODO Auto-generated method stub
//	    Map<String, String> infoFich   = new HashMap<String, String>();
//	    String clave, valor;
//	    
//	    if (mainApp.withProc) {
//		    infoFich   = infoFichero(paso, letraPaso, "SORTOUT");    
//	    }else {
//	    	infoFich   = infoFichero(paso, letraPaso, "SORTOUT");  
//	    }
//		return infoFich;
//	}
	
	public ArrayList<String> infoSORTIN(int pasoE, String letraPaso) throws IOException {
		// TODO Auto-generated method stub
		ArrayList<String> infoFichero = new ArrayList<String>();
		ArrayList<String> infoFicheroProc = new ArrayList<String>();
		int ini = 0, fin = 0;
		
		if(mainApp.withProc) {
			String primero = CONST_EMPTY, segundo = CONST_EMPTY, tercero = CONST_EMPTY;
			infoFicheroProc = buscaInfoProc(pasoE, letraPaso, "SORTIN");
			for(int i = 0; i < infoFicheroProc.size(); i++) {
				fin = infoFicheroProc.get(i).indexOf("DSN=");
				if (fin != -1) {
					primero = infoFicheroProc.get(i).substring(0, fin);
					ini = fin;
					fin = infoFicheroProc.get(i).indexOf(",DISP=");
					tercero = infoFicheroProc.get(i).substring(ini, ini + 4) + "Z." + infoFicheroProc.get(i).substring(ini + 4, fin);
					ini = fin + 1;
					segundo = infoFicheroProc.get(i).substring(ini) + ",";
					infoFichero.add(primero + segundo + tercero);	
				}
			}
		}else {
			infoFichero.add("**** No encontrado fichero SORTIN");
			Avisos.LOGGER.log(Level.INFO, letraPaso + String.valueOf(pasoE) + " //Comprobar fichero CYL migrado correctamente");
		}
		return infoFichero;
	}

	public Map<String, String> infoFtpReb(int pasoE, String letraPaso) throws IOException {
		// TODO Auto-generated method stub
		Map<String, String> infoFich   = new HashMap<String, String>();
		
		infoFich = infoFichero(pasoE, letraPaso, "SORTI1");

		return infoFich;
	}
	
	public String infoDSN(int pasoE, String letraPaso, String name) throws IOException {
		// TODO Auto-generated method stub
		boolean seguir = true, buscar = false;	
		@SuppressWarnings("unused")
		String linea, clave, valor = CONST_EMPTY;
		int index = 0;
		if(mainApp.withProc) {
			//----------------Fichero de plantilla JPROC--------------------------
		    FileReader ficheroPROC = new FileReader("C:\\Cortex\\PROC\\" + mainApp.programa.substring(0,6) + ".txt");
		    BufferedReader lectorPROC = new BufferedReader(ficheroPROC);
			//-----------------------------------------------------------------------
		    
		    String numeroPaso;    
		    numeroPaso = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
		    
		    while((linea = lectorPROC.readLine()) != null && seguir) {
		    	if(linea.startsWith("//" + letraPaso + numeroPaso)) {
		    		buscar = true;
		    	}
		    	if(buscar) {
		    		if(linea.startsWith("//" + name + "  ")){
		    			index = linea.indexOf('=', index);
		    			clave = lectorPasos.leerClave(linea, index);
						valor = lectorPasos.leerValor(linea, index);
						buscar = false;
						seguir = false;
		    		}
		    	}	    	
		    }
		    lectorPROC.close();
		}else {
			valor = name;
		}
		return valor;
	}
	
	public void infoJFUSION(Map<String, String> datos, int pasoE, String letraPaso) throws IOException {
		// TODO Auto-generated method stub
		Map<String, String> infoFich   = new HashMap<String, String>();
		String[] ficheros;
		int contadorFicheros = 0;
		String clave = CONST_EMPTY, valor = CONST_EMPTY;
		
		pasoE -= 2;
		for(int i = 1; datos.containsKey("FICHA" + String.valueOf(i)); i++) {
			ficheros = datos.get("FICHA" + String.valueOf(i)).split(",");
			ficheros[0] = ficheros[0].replace("ENTRADA=", CONST_EMPTY);
			
			if(ficheros[0].contains("SORTIDA=")) {
				infoFich = infoFichero(pasoE, letraPaso, ficheros[0].replace("SORTIDA=", CONST_EMPTY));
				if (infoFich.containsKey("MGMTCLAS")){
					datos.put("MGMTCLAS", infoFich.get("MGMTCLAS"));
				}
				datos.put(CONST_DEFINICION, infoFich.get(CONST_DEFINICION));
				datos.put("DSN", infoFich.get("DSN"));
				datos.put("SALIDA", ficheros[0].replace("SORTIDA=", CONST_EMPTY));
				
			}else {
				for (int j = 0; j < ficheros.length; j++) {
					contadorFicheros++;
					clave = "DSN" + contadorFicheros;
					valor = infoDSN(pasoE, letraPaso, ficheros[j]);
					datos.put(clave, valor);
					datos.put("FICH" + contadorFicheros, ficheros[j]);
				}
			}	
		}
		
	}
	
	public static ArrayList<String> ComprobarTamañoLinea(String cabecera, String linea, String fi, Map<String, String> datos) {
		// TODO Auto-generated method stub
		ArrayList<String> salida = new ArrayList<String>();
		if((linea.trim() + fi + datos.get(cabecera)).length() < 72) {
			if(datos.get(cabecera) == null) {
				salida.add(0 ,linea.trim() + fi.trim());
			}else {
//				if (fi.isEmpty()) {
//					salida.add(0 ,linea.trim() + fi.trim() + datos.get(cabecera));
//				}else {
//					salida.add(0 ,linea.trim() + fi.trim() + " " + datos.get(cabecera));
//				}
//				salida.add(0 ,linea.trim() + fi.trim() + datos.get(cabecera));
				salida.add(0 ,linea.trim() + fi + datos.get(cabecera));
			}
			salida.add(1, CONST_EMPTY);			 
		}
		else{
			if (fi.isEmpty()) {
				fi = linea.trim()+ fi.trim() + datos.get(cabecera);
			}else {
				String masdatos = datos.get(cabecera) != null ? datos.get(cabecera) : CONST_EMPTY;
				fi = linea.trim()+ fi.trim() + " " + masdatos;
			}
			for(int i = 72; i > 0; i--) {
				int index = fi.lastIndexOf(" ", i);
				index = index == -1 ? fi.lastIndexOf(";", i) : index;
				
				if(index != -1) {
					salida.add(0, fi.substring(0, index));
					salida.add(1, fi.substring(index + 1).trim() + " ");
					i = -1;
				}
			}			
		}		
		return salida;
	}

	public Map<String, String> infomultiDSN(int pasoE, String letraPaso, String name) throws IOException {
		// TODO Auto-generated method stub
		boolean seguir = true, buscar = false;	
		String linea, clave, valor = CONST_EMPTY;
		int index = 0, contador=0;
		Map<String, String> datos = new HashMap<String, String>();
		
		
		if(mainApp.withProc) {
			//----------------Fichero de plantilla JPROC--------------------------
		    FileReader ficheroPROC = new FileReader("C:\\Cortex\\PROC\\" + mainApp.programa.substring(0,6) + ".txt");
		    BufferedReader lectorPROC = new BufferedReader(ficheroPROC);
			//-----------------------------------------------------------------------
		    
		    String numeroPaso;    
		    numeroPaso = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
		    
		    while((linea = lectorPROC.readLine()) != null && seguir) {
		    	if(linea.startsWith("//" + letraPaso + numeroPaso)) {
		    		buscar = true;
		    	}
		    	if(buscar) {
		    		if(linea.startsWith("//" + name + "  ")){
		    			index = linea.indexOf('=', index);
		    			clave = lectorPasos.leerClave(linea, index)+ contador;
						valor = lectorPasos.leerValor(linea, index);
						datos.put(clave, valor);
						contador++;
		    		}else if(linea.startsWith("//  ")) {
		    			index = linea.indexOf('=', index);
		    			clave = lectorPasos.leerClave(linea, index)+ contador;
						valor = lectorPasos.leerValor(linea, index);
						datos.put(clave, valor);
						contador++;
		    		}else if(linea.startsWith("//SYSUT2")){
						buscar = false;
						seguir = false;
		    		}
		    	}	    	
		    }
		    lectorPROC.close();
		}else {
			datos.put("DSN0", "Leer el PROC para sacar archivos");
		}
		System.out.println("------- Datos sacados del Fichero:  -------");
	    datos.forEach((k,v) -> System.out.println(k + "-" + v));
	    System.out.println("----------------------------------------");
		return datos;
	}

	public Map<String, String> cabecera(int pasoE, String letraPaso) throws IOException {
		// TODO Auto-generated method stub
		boolean seguir = true, buscar = false;	
		String linea, clave, valor = CONST_EMPTY;
		int index = 0, contador=0, numVariable=0, numOPC=0, finIndex = 0;
		Map<String, String> datos = new HashMap<String, String>();
		//----------------Fichero de plantilla CNTL--------------------------
	    FileReader ficheroCNTL = new FileReader("C:\\Cortex\\CNTL\\" + mainApp.programa.substring(0,6) + ".txt");
	    BufferedReader lectorCNTL = new BufferedReader(ficheroCNTL);
		//-----------------------------------------------------------------------
	    
	    while((linea = lectorCNTL.readLine()) != null && seguir) {
	    	if(linea.startsWith("//REPORT ")) {
	    		buscar = true;
	    	}
	    	if(buscar) {
	    		if(linea.startsWith("//*%")){
	    			index = linea.lastIndexOf("%");
    				clave = "OPC" + numOPC;
    				valor = linea.substring(index+1);
					datos.put(clave, valor);
					numOPC++;
	    		}
    			else if(linea.startsWith("//"+ mainApp.programa.substring(0,6))) {
    				continue;
    			}else if(linea.startsWith("// ")) {
    				index = linea.lastIndexOf(" ");
    				clave = "Variable" + numVariable;
    				valor = linea.substring(index+1);
    				if(isNombreEqualsValor(valor)) {
    					datos.put("aviso", "REVISAR: Nombre de la variable igual a su valor");
    				}
    				if(valor.charAt(valor.length()-1)==',') {
    					finIndex = valor.length() + index;
    					valor = linea.substring(index+1,finIndex);	
    				}
    				if(valor.endsWith("=")) {
    					valor += "''";
    				}
					datos.put(clave, valor);
					numVariable++;
    			}else if(linea.startsWith("//*")) {
    				index = linea.lastIndexOf("*");
    				clave = "Comentario" + contador;
    				valor = linea.substring(index+1);
					datos.put(clave, valor);
					contador++;
    			}
	    	}	    	
	    }
	    lectorCNTL.close();
		System.out.println("------- Datos sacados de la Cabecera:  -------");
	    datos.forEach((k,v) -> System.out.println(k + "-" + v));
	    System.out.println("----------------------------------------");
		return datos;
	}

	private boolean isNombreEqualsValor(String valor) {
		
		int index = 0;
		String variable = "" , literal ="";
		index = valor.indexOf(CONST_EQUALS);
		variable = valor.substring(0, index);
		literal = valor.substring(index + 1);
		if (literal.contains(variable)) {
			return true;
		}else {
			return false;
		}
	}

}

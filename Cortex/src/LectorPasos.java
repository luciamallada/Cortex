import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LectorPasos {

	public Map<String, String> leerPaso(ArrayList<String> pasos) {
		// TODO Auto-generated method stub
		Map<String, String> datos = new HashMap<String, String>();
		String clave, valor;
		int index = 0;
		int archivosEntrada = 0, archivosSalida = 0;
		
		for(int i = 0; i < pasos.size(); i++) {
			index = 0;
			if (!pasos.get(i).startsWith("CUADRE")) {
// ------------- Buscamos las variables, con la referencia del igual	
				if (!pasos.get(i).contains("FILE ") && !pasos.get(i).startsWith("*")) {
					while (index != -1) {
						index = pasos.get(i).indexOf('=', index);
						if (index != -1 && pasos.get(i).charAt(index + 1) != '(') {
							clave = leerClave(pasos.get(i), index);
							valor = leerValor(pasos.get(i), index, clave);
							if (!clave.equals("") && !valor.equals("")) {
								datos.put(clave, valor);
							}
						}
						if (index != - 1) {
							index ++;
						}
					}
				}
				if(pasos.get(i).contains("FILE ")){
// -------------- Buscamos los posibles archivos
					index = 0;
					index = pasos.get(i).indexOf("MODE=") + 5;
					if (pasos.get(i).charAt(index) == 'I' || pasos.get(i).charAt(index) == 'U') {
						archivosEntrada++;
						valor = leerArchivoEntrada(pasos.get(i));
						clave = "Entrada" + String.valueOf(archivosEntrada);
						datos.put(clave, valor);
					}else {
						archivosSalida++;
						clave = "Salida" + String.valueOf(archivosSalida);
						valor = leerArchivoSalida(pasos.get(i), datos, archivosSalida);
						datos.put(clave, valor);
					}	
				}
//---------------- Buscamos el valor para los SORTS
				if (pasos.get(i).contains("SORT FIELDS")) {
					clave = "SORT";
					index = pasos.get(i).indexOf("FIELDS=");
					for (int j = index; j < pasos.get(i).length(); j++) {
						if(pasos.get(i).charAt(j) == ')') {
							valor = pasos.get(i).substring(index, j+1);
							datos.put(clave, valor);
						}
					}
				}
//---------------- Buscamos ficheros Cortex
				if (pasos.get(i).contains("DATA  *") && (mainApp.tipoPaso.equals("DB2") || mainApp.tipoPaso.equals("JPGM"))) {
					datos.put("FCortex", pasos.get(i).substring(0, pasos.get(i).indexOf(" ")));
					String aux = "";
					int lineaSalida = 0, contFCortex = 0;
					for (int j = i + 1; j < pasos.size(); j++) {
						if (pasos.get(j).contains("DATAEND")) {
							aux = pasos.get(j).substring(0, pasos.get(j).indexOf("DATAEND"));
							lineaSalida = j + 1;
							j =  pasos.size() + 1;
						}else {
							aux = pasos.get(j).trim();	
						}
						contFCortex++;
						datos.put("FC" + contFCortex, aux);						
					}
					i = lineaSalida - 1;
				}
			}
		}
		datos = busquedaAdicional(datos, pasos);

		return datos;
	}

	private Map<String, String> busquedaAdicional(Map<String, String> datos, ArrayList<String> pasos){
		String clave, valor;
		int index = 0;
		int archivosEntrada = 0, archivosSalida = 0, comentarios = 0, reportes = 0;
		for(int i = 0; i < pasos.size(); i++) {
			index = 0;
			if (!pasos.get(i).startsWith("CUADRE")) {
				// --------------- Buscamos comentarios
				if(pasos.get(i).startsWith("*")) {
					int j = i + 1, totalComents = 0;
					while (pasos.size() > j && pasos.get(j).startsWith("*")) {
						j++;
					}
					totalComents = j - i;
					if (pasos.size() > j && pasos.get(j).contains("FILE")) {
						index = 0;
						index = pasos.get(j).indexOf("MODE=") + 5;
						if (pasos.get(j).charAt(index) == 'I' || pasos.get(j).charAt(index) == 'U') {
							for (int k = 0; k < totalComents; k++) {
								clave = "ComFichE" + String.valueOf(archivosEntrada + 1) + String.valueOf(k + 1);
								valor = pasos.get(i + k);
								datos.put(clave, valor);
							}
							archivosEntrada++;
						}else {
							for (int k = 0; k < totalComents; k++) {
								clave = "ComFichS" + String.valueOf(archivosSalida + 1) + String.valueOf(k + 1);
								valor = pasos.get(i + k);
								datos.put(clave, valor);
							}
							archivosSalida++;
						}
					i = j-1;
					}else {
						comentarios++;
						clave = "Comentario" + String.valueOf(comentarios);
						valor = pasos.get(i);
						datos.put(clave, valor);
					}
				}
		//--------------- Buscar reportes	
				if (pasos.get(i).contains(" REPORT ")) {
					if(pasos.get(i).trim().endsWith("SYSOUT=S") || pasos.get(i).trim().endsWith("SYSOUT=*") || pasos.get(i).trim().endsWith("SYSOUT=*END")
							|| pasos.get(i).trim().endsWith("SYSOUT=P")) {
						continue;
					}else {
						reportes++;
						clave = "Reporte" + String.valueOf(reportes);
						valor = pasos.get(i).substring(0,8);
						datos.put(clave, valor);
					}	
				}
		//--------------- Buscar IF - ENDIF
//				if(pasos.get(i).matches("(.*)IF [" + mainApp.letraPaso + "][0-9]{2}(.*)")) {
				if(pasos.get(i).matches("(.*)IF\\s+[" + mainApp.letraPaso + "][0-9]{2}(.*)")) {
					String aux = pasos.get(i).replaceFirst("IF\\s+","IF ");
					clave = "IF";
					//index = pasos.get(i).indexOf("IF " + mainApp.letraPaso);
					index = aux.indexOf("IF " + mainApp.letraPaso);
					//valor = "//         " + pasos.get(i).substring(index);
					valor = "//         " + aux.substring(index);
					datos.put(clave, valor);
				}
				if(pasos.get(i).contains("ENDIF")){
					clave = "ENDIF";
					valor = "//         ENDIF";
					datos.put(clave, valor);
				}
				if(pasos.get(i).contains("ELSE")) {
					clave = "ELSE";
					valor = "//         ELSE";
					datos.put(clave, valor);
				}
				
		//---------------- Buscar condicionales
				if(pasos.get(i).contains("COND1=") || pasos.get(i).contains("COND2=")) {
					if(pasos.get(i).indexOf("COND1") != -1) {
						int ind = pasos.get(i).indexOf("COND1");
						ind = pasos.get(i).indexOf("=", ind);
						valor = leerCond(pasos.get(i), ind);
						datos.put("COND1", valor);
					}
					if(pasos.get(i).indexOf("COND2") != -1) {
						int ind = pasos.get(i).indexOf("COND2");
						ind = pasos.get(i).indexOf("=", ind);
						valor = leerCond(pasos.get(i), ind);
						datos.put("COND2", valor);
					}
				}
			}
		}
		
		return datos;	
	}
	
	public String leerCond(String linea, int ind) {
		String valor = "";
		for(int i = ind; i < linea.length(); i++) {
			if(linea.charAt(i) == ')') {
				valor = linea.substring(ind, i + 1);
				i = linea.length() + 1;
			}
		}
		return valor;
	}

	private String leerArchivoSalida(String linea, Map<String, String> datos, int archivosSalida){
		// TODO Auto-generated method stub
		String valor = "";
		String claveB = "";
		String valorB = "";
		
		for(int i = 0; i < linea.length(); i++) {
			if(linea.charAt(i) == ' ') {
				valor = linea.substring(0, i);
				i = linea.length() + 1;
			}
		}
		claveB = "Borrar" + String.valueOf(archivosSalida);
		if (linea.contains("(YES,DELETE")) {
			valorB = valor;
		}else {
			Map<String, String> infoFich = new HashMap<String, String>();
			MetodosAux metodosAux = new MetodosAux();
		    try {
				infoFich = metodosAux.infoFichero(mainApp.pasoE, mainApp.letraPaso, valor);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    if (infoFich.get("DISP").equals("NEW")) {
		    	valorB = valor;
		    }else {
		    	valorB = "No";
		    }
		}
		datos.put(claveB, valorB);	
		
		return valor;
	}

	private String leerArchivoEntrada(String linea) {
		// TODO Auto-generated method stub		
		String valor = "";
		for(int i = 0; i < linea.length(); i++) {
			if(linea.charAt(i) == ' ') {
				valor = linea.substring(0, i);
				i = linea.length() + 1;
			}
		}
		return valor;
	}

	public String leerValor(String linea, int index) {
		// TODO Auto-generated method stub
		String valor = "";
		int fin = 0;
		if(linea.charAt(index + 1) == '\''){
			for(int i = index + 2; i < linea.length(); i++) {
				if (linea.charAt(i) == '\'') {
					fin = i;					
					i = linea.length() + 1;
				}
			}
			valor = linea.substring(index + 2, fin).replace(',', '-');
		}else{
			for(int i = index; i < linea.length(); i++) {
				if (linea.charAt(i) == ',' || linea.charAt(i) == ' ' || linea.charAt(i) == ')') {
					fin = i;
					i = linea.length() + 1;
				}
				if (fin == 0) {
					fin = linea.length();
				}
			}
			valor = linea.substring(index + 1, fin);
		}
		
		return valor;
	}
	
	private String leerValor(String linea, int index, String clave) {
		// TODO Auto-generated method stub
		String valor = "";
		int fin = 0;
		if(linea.charAt(index + 1) == '\''){
			for(int i = index + 2; i < linea.length(); i++) {
				if (linea.charAt(i) == '\'') {
					fin = i;					
					i = linea.length() + 1;
				}
			}
			if (clave.contains("DADA") || clave.contains("ADR")) {
				linea = linea.substring(index + 2, fin);	
			}else {
				linea = linea.substring(index + 2, fin).replace(',', '-');
			}
			valor = linea;
		}else{
			for(int i = index; i < linea.length(); i++) {
				if (linea.charAt(i) == ',' || linea.charAt(i) == ' ' || linea.charAt(i) == ')') {
					fin = i;
					i = linea.length() + 1;
				}
				if (fin == 0) {
					fin = linea.length();
				}
			}
			valor = linea.substring(index + 1, fin);
		}
		
		return valor;
	}

	public String leerClave(String linea, int index) {
		// TODO Auto-generated method stub
		String clave = "";
		int inicio = 0;

		for (int i = index; i > 0; i--) {
			if (linea.charAt(i) == ' ' || linea.charAt(i) == '(' || linea.charAt(i) == ',') {
				inicio = i;
				i = 0;
			}
		}
		clave = linea.substring(inicio + 1, index);
		
		return clave;
	}
	
	public Map<String, String> leerPasoSort(ArrayList<String> pasos) {
		Map<String, String> datos = new HashMap<String, String>();
		String valor, clave;
		int i = 0;
		
		for(int j = 0; j < pasos.size(); j++) {
			if(pasos.get(j).startsWith("SYSIN")) {
				for(int k = j + 1; !pasos.get(k).contains("DATAEND"); k++) {
					
				//	vacios y end!!!!
					if (pasos.get(k).endsWith("X")) {
						i++;
						clave = "SORT" + String.valueOf(i);
						valor = pasos.get(k).substring(0, pasos.get(k).length()-1);
						datos.put(clave, valor);
					}else {
						i++;
						clave = "SORT" + String.valueOf(i);
						valor = pasos.get(k);
						datos.put(clave, valor);
					}	
				}
				j = pasos.size() + 1;
			}
		}
		for(int j = 0; j < pasos.size(); j++) {
			if(pasos.get(j).contains("SORTOUT")) {
				datos.put("withSortout", "true");
			}
		}
		datos = busquedaAdicional(datos, pasos);
		
		return datos;
	}

	public Map<String, String> leerPasoJOPCREC(ArrayList<String> pasos) {
		Map<String, String> datos = new HashMap<String, String>();
		String valor, clave;
		int index = 0;
		for(int j = 0; j < pasos.size(); j++) {
			if(pasos.get(j).startsWith("SRSTAT")) {
				clave = "SRSTAT";
				index = pasos.get(j).indexOf("'", index);
				valor = pasos.get(j).substring(index+1, pasos.get(j).indexOf("' SUB"));
				datos.put(clave, valor);
				j = pasos.size() + 1;
			}
		}
		datos = busquedaAdicional(datos, pasos);
		
		return datos;
		
	} 
	
	public Map<String, String> leerPasoJFusionGenquad(ArrayList<String> pasos) {
		Map<String, String> datos = new HashMap<String, String>();
		String valor, clave, linea;
		int index = 0;
		
		int i = 0;
		if (pasos.get(1).startsWith(" ")) {
			linea = pasos.get(0).trim() + pasos.get(1).trim();
		}else {
			linea = pasos.get(0);
		}
		
		index = linea.indexOf("APL=");
		clave = "APL";
		for (int k = index; k < linea.length(); k++) {
			if (linea.charAt(k) == ',') {
				valor = linea.substring(index + 4, k);
				k = linea.length() + 1;
				datos.put(clave, valor);
			}
		}
		index = linea.indexOf("QUADRE=");
		clave = "QUADRE";
		for (int k = index; k < linea.length(); k++) {
			if (linea.charAt(k) == ',' || linea.charAt(k) == '\'' ) {
				valor = linea.substring(index + 7, k);
				k = linea.length() + 1;
				datos.put(clave, valor);
			}
		}
		
		for(int j = 0; j < pasos.size(); j++) {
			if(pasos.get(j).startsWith("FICHA    DATA")) {
				for(int k = j + 1; !pasos.get(k).contains("DATAEND"); k++) {
						i++;
						clave = "FICHA" + String.valueOf(i);
						valor = pasos.get(k);
						datos.put(clave, valor);	
				}
				j = pasos.size() + 1;
			}
		}
		datos = busquedaAdicional(datos, pasos);
		
		return datos;
	}

	public Map<String, String> leerPasoJPAUSA(ArrayList<String> pasos) {
		Map<String, String> datos = new HashMap<String, String>();
		String valor, clave;
		int index = 0;
		for(int j = 0; j < pasos.size(); j++) {
			if(pasos.get(j).contains("PARM=")) {
				clave = "PARM";
				index = pasos.get(j).indexOf("('", index);
				valor = pasos.get(j).substring(index+2, pasos.get(j).lastIndexOf("')"));
				datos.put(clave, valor);
				j = pasos.size() + 1;
			}
		}
		datos = busquedaAdicional(datos, pasos);
		
		return datos;
		
	}   
}

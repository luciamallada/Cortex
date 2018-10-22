import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MetodosAux {

	public boolean checkLiteralesPARDB2(String param) {
		// TODO Auto-generated method stub
		if (!param.startsWith("&")) {
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

	public Map<String, String> infoFichero(int pasoE, String letraPaso, String nombre) throws IOException {
		// TODO Auto-generated method stub
		boolean seguir = true, buscar = false;	
		LectorPasos lectorPasos =  new LectorPasos();
		String linea;
		ArrayList<String> infoFichero = new ArrayList<String>();
		Map<String, String> infoFich = new HashMap<String, String>();
		//----------------Fichero de plantilla JPROC--------------------------
	    FileReader ficheroPROC = new FileReader("C:\\Cortex\\PROC.txt");
	    BufferedReader lectorPROC = new BufferedReader(ficheroPROC);
		//-----------------------------------------------------------------------
	    
	    String numeroPaso;    
	    numeroPaso = (pasoE < 10) ? "0" + String.valueOf(pasoE) : String.valueOf(pasoE) ;
	    
	    while((linea = lectorPROC.readLine()) != null && seguir) {
	    	if(linea.startsWith("//" + letraPaso + numeroPaso)) {
	    		buscar = true;
	    	}
	    	if(buscar) {
	    		if(linea.startsWith("//" + nombre)) {
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
	    
	    
	    String clave, valor;
    	int primario = 0, secundario = 0, tamaño;
	    for(int j = 0; j < infoFichero.size(); j++) {
	    	int index = 1;
		    while (index != -1) {
				index = infoFichero.get(j).indexOf('=', index);
				if (index != -1) {
					clave = lectorPasos.leerClave(infoFichero.get(j), index);
					valor = lectorPasos.leerValor(infoFichero.get(j), index);
					valor = valor.replace("(","");
					if (!clave.equals("") && !valor.equals("")) {
						infoFich.put(clave, valor);
					}
				}
				if (index != - 1) {
					index ++;
				}
			}
		    if(infoFichero.get(j).contains("SPACE") && !infoFich.get("SPACE").equals("CYL") && !infoFich.get("SPACE").equals("TRK")) {
		    	int ini = 1, fin = 2;
		    	ini = infoFichero.get(j).lastIndexOf("(");
		    	for(int i = ini; i < infoFichero.get(j).length(); i++) {
		    		if(infoFichero.get(j).charAt(i) == ',') {
		    			fin = i;
		    			i = 1000;
		    		}
		    	}
		    	tamaño = Integer.valueOf(infoFichero.get(j).substring(ini + 1, fin));
		    	primario = Integer.parseInt(infoFich.get("SPACE")) * tamaño / Integer.parseInt(infoFich.get("LRECL")) / 1000;
		    	
		    	ini = fin;
		    	for(int i = ini; i < infoFichero.get(j).length(); i++) {
		    		if(infoFichero.get(j).charAt(i) == ')') {
		    			fin = i;
		    			i = 1000;
		    		}
		    	}
		    	tamaño = Integer.valueOf(infoFichero.get(j).substring(ini + 1, fin));
		    	secundario = Integer.parseInt(infoFich.get("SPACE")) * tamaño / Integer.parseInt(infoFich.get("LRECL")) / 1000;; 
		    	
		    }else {
		    	if(infoFichero.get(j).contains("SPACE") && infoFich.get("SPACE").equals("CYL")) {
		    		primario = 15;
		    		secundario = 1;
		    	}
		    }
	    }
	    
	    clave = "Definicion";
	    valor = "(" + infoFich.get("LRECL") + ",(" + String.valueOf(primario) + "," + String.valueOf(secundario) + "))";
		infoFich.put(clave, valor);
		
		if(!infoFich.containsKey("DSN")) {
			//FICHEROS DUMMY
		}
		else {
			if(infoFich.get("DSN").endsWith("XP")) {
		
			infoFich.replace("DISP", "TEMP");
			}
		}
		System.out.println("------- Datos sacados del Fichero:  -------");
	    infoFich.forEach((k,v) -> System.out.println(k + "-" + v));
	    System.out.println("----------------------------------------");
		
		return infoFich;
	}

}

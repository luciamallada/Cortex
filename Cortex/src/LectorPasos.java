import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LectorPasos {

	public Map<String, String> leerPaso(ArrayList<String> pasos) {
		// TODO Auto-generated method stub
		Map<String, String> datos = new HashMap<String, String>();
		String clave, valor;
		int index = 0;
		int archivosEntrada = 0, archivosSalida = 0, comentarios = 0;
		
		for(int i = 0; i < pasos.size(); i++) {
			index = 0;
			if (!pasos.get(i).startsWith("CUADRE")) {
// ------------- Buscamos las variables, con la referencia del igual	
				if (!pasos.get(i).contains("FILE")) {
					while (index != -1) {
						index = pasos.get(i).indexOf('=', index);
						if (index != -1 && pasos.get(i).charAt(index + 1) != '(') {
							clave = leerClave(pasos.get(i), index);
							valor = leerValor(pasos.get(i), index);
							if (!clave.equals("") && !valor.equals("")) {
								datos.put(clave, valor);
							}
						}
						if (index != - 1) {
							index ++;
						}
					}
				}else {
// -------------- Buscamos los posibles archivos
					index = 0;
					index = pasos.get(i).indexOf("MODE=") + 5;
					if (pasos.get(i).charAt(index) == 'I') {
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
// --------------- Buscamos comentarios
				if(pasos.get(i).startsWith("*")) {
					comentarios++;
					clave = "Comentario" + String.valueOf(comentarios);
					valor = pasos.get(i);
					datos.put(clave, valor);
				}
// --------------- Buscar reportes				
			}	
		}

		return datos;
	}

	private String leerArchivoSalida(String linea, Map<String, String> datos, int archivosSalida) {
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
			valorB = "Si";
		}else {
			valorB = "No";
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

	private String leerValor(String linea, int index) {
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
			//evitar, solo se hará replace al insertar la variable correspondiente
			valor = linea.substring(index + 2, fin).replace(',', '-');
		}else{
			for(int i = index; i < linea.length(); i++) {
				if (linea.charAt(i) == ',' || linea.charAt(i) == ' ') {
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

	private String leerClave(String linea, int index) {
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

}

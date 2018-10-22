import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class WriterPasos {
	
	MetodosAux metodosAux = new MetodosAux();

	public void writeDB2(Map<String, String> datos, String letraPaso, int paso, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla DB2--------------------------
	    FileReader ficheroDB2 = new FileReader("C:\\Cortex\\Plantillas\\JDB2.txt");
	    BufferedReader lectorDB2 = new BufferedReader(ficheroDB2);	
	    //----------------Variables------------------------------------------
	    String linea;
	    paso++;
	    String numeroPaso = (paso < 10) ? "0" + String.valueOf(paso) : String.valueOf(paso) ;
	    int contadorLinea = 0;
	    
	    
	    //----------------Método---------------------------------------------
	    
	    //--------------- Miramos si hay archivos para borrar antes de ejecutar:
	    for (int i = 1; datos.containsKey("Borrar" + String.valueOf(i)); i++) {
	    	writeJBORRAF(numeroPaso, i, letraPaso, writerCortex);
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
	    		linea = linea.replace("&VAR1-&VAR2-..." , datos.get("PARDB2"));
	    		if (metodosAux.checkLiteralesPARDB2(datos.get("PARDB2"))) {
	    			writerCortex.write("****** LITERALES EN LOS PARAMETROS DEL PROGRAMA *****");
	    	    	writerCortex.newLine();
	    	    	System.out.println("Escribimos: " + linea);
	    		}
			default:
				break;
	    	}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorDB2.close();
	    
//--------------- Miramos si hay ficheros de entrada:
	    for (int i = 1; datos.containsKey("Entrada" + String.valueOf(i)); i++) {
	    	writeJFICHENT(datos, numeroPaso, i, letraPaso, writerCortex);
	    }
//--------------- Miramos si hay ficheros de Salida:
	    for (int i = 1; datos.containsKey("Salida" + String.valueOf(i)); i++) {
	    	writeJFICHSAL(datos, numeroPaso, i, letraPaso, writerCortex);
	    }
//--------------- Miramos si hay Comentarios:
	    writeComments(datos, writerCortex);
	}

	public void writeComments(Map<String, String> datos, BufferedWriter writerCortex) throws IOException {
		for (int i = 1; datos.containsKey("Comentario" + String.valueOf(i)); i++) {
	    	writerCortex.write("//" + datos.get("Comentario" + String.valueOf(i)));
	    	writerCortex.newLine();
	    }
	}

	public void writeJFICHSAL(Map<String, String> datos, String numeroPaso, int i, String letraPaso,
			BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JFICHENT--------------------------
	    FileReader ficheroJFICHSAL = new FileReader("C:\\Cortex\\Plantillas\\JFICHSAL.txt");
	    BufferedReader lectorJFICHSAL = new BufferedReader(ficheroJFICHSAL);	
	    //----------------Variables------------------------------------------
	    String linea, nombre;
	    int contadorLinea = 0;
	    
	    //----------------Método---------------------------------------------
	    nombre = datos.get("Salida" + String.valueOf(i));
	    for(int j = nombre.length(); j < 8; j++) {
			nombre += " ";
		}
	    while((linea = lectorJFICHSAL.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 3:
	    		linea = linea.replace("DDNAME--", nombre);
	    		break;
	    	case 9:
	    		linea = linea.replace("DDNAME--", nombre);
	    		break;
	    	case 14:
	    		linea = linea.replace("DDNAME--", nombre);
	    		break;
	    	default:
				break;
	    	}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJFICHSAL.close();	 
	}

	public void writeJFICHENT(Map<String, String> datos, String numeroPaso, int i, String letraPaso, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JFICHENT--------------------------
	    FileReader ficheroJFICHENT = new FileReader("C:\\Cortex\\Plantillas\\JFICHENT.txt");
	    BufferedReader lectorJFICHENT = new BufferedReader(ficheroJFICHENT);	
	    //----------------Variables------------------------------------------
	    String linea, nombre;
	    int contadorLinea = 0;
	    
	    //----------------Método---------------------------------------------
	    while((linea = lectorJFICHENT.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		nombre = datos.get("Entrada" + String.valueOf(i));
	    		for(i = nombre.length(); i < 8; i++) {
	    			nombre += " ";
	    		}
	    		linea = linea.replace("DDNAME--", nombre);
	    		break;
	    	default:
				break;
	    	}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJFICHENT.close();	 
	}

	public void writeJBORRAF(String numeroPaso, int i, String letraPaso, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JBORRAF--------------------------
	    FileReader ficheroJBORRAF = new FileReader("C:\\Cortex\\Plantillas\\JBORRAF.txt");
	    BufferedReader lectorJBORRAF = new BufferedReader(ficheroJBORRAF);	
	    //----------------Variables------------------------------------------
	    String linea;
	    int contadorLinea = 0;
	    
	    //----------------Método---------------------------------------------
	    while((linea = lectorJBORRAF.readLine()) != null) {
	    	contadorLinea ++;
	    	if(i > 1 && contadorLinea == 1) {
	    		//No queremos que vuelva a escribir la primera línea de la plantilla
	    		continue;
	    	}
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---D-", "//" + letraPaso + numeroPaso + "D" + String.valueOf(i));
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

	public void writeMAILTXT(Map<String, String> datos, String letraPaso, int paso, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
			//----------------Fichero de plantilla JJMAILTXT--------------------------
		    FileReader ficheroMAILTXT = new FileReader("C:\\Cortex\\Plantillas\\JMAILTXT.txt");
		    BufferedReader lectorMAILTXT = new BufferedReader(ficheroMAILTXT);	
		    //----------------Variables------------------------------------------
		    String linea;
		    paso++;
		    String numeroPaso = (paso < 10) ? "0" + String.valueOf(paso) : String.valueOf(paso) ;
		    int contadorLinea = 0;
		    
		    
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
		    		linea = (datos.get("ADRDES") == null) ? linea.trim() : linea.trim() + datos.get("ADRDES");
		    		break;
		    	case 7:
		    		linea = (datos.get("ADRDE1") == null) ? linea.trim() : linea.trim() + datos.get("ADRDE1");
		    		break;
		    	case 8:
		    		linea = (datos.get("ADRDE2") == null) ? linea.trim() : linea.trim() + datos.get("ADRDE2");
		    		break;
		    	case 9:
		    		linea = (datos.get("ADRDE3") == null) ? linea.trim() : linea.trim() + datos.get("ADRDE3");
		    		break;
		    	case 10:
		    		linea = (datos.get("TIPMAIL") == null) ? linea.trim() : linea.trim() + datos.get("TIPMAIL");
		    		break;
		    	case 14:
		    		linea = (datos.get("DATAENVI") == null) ? linea.trim() : linea.trim() + datos.get("DATAENVI");
		    		break;
		    	case 15:
		    		linea = (datos.get("HORENVI") == null) ? linea.trim() : linea.trim() + datos.get("HORENVI");
		    		break;
		    	case 16:
		    		linea = (datos.get("DADA721") == null) ? linea.trim() : linea.trim() + datos.get("DADA721");
		    		break;
		    	case 17:
		    		linea = (datos.get("DADA722") == null) ? linea.trim() : linea.trim() + datos.get("DADA722");
		    		break;
		    	case 18:
		    		linea = (datos.get("DADA723") == null) ? linea.trim() : linea.trim() + datos.get("DADA723");
		    		break;
		    	case 19:
		    		linea = (datos.get("DADA724") == null) ? linea.trim() : linea.trim() + datos.get("DADA724");
		    		break;
		    	case 20:
		    		//Revisar nombre variable
		    		linea = (datos.get("DADA725") == null) ? linea.trim() : linea.trim() + datos.get("DADA725");
		    		break;
				default:
					break;
				}
		    	System.out.println("Escribimos: " + linea);
		    	writerCortex.write(linea);
		    	writerCortex.newLine();
		    }
		    lectorMAILTXT.close();		
		    writeComments(datos, writerCortex);
	}

	public void writeSORT(Map<String, String> datos, String letraPaso, int paso, BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla DB2--------------------------
	    FileReader ficheroJSORT = new FileReader("C:\\Users\\0014879\\Desktop\\Cortex\\Plantillas\\JSORT.txt");
	    BufferedReader lectorJSORT = new BufferedReader(ficheroJSORT);	
	    //----------------Variables------------------------------------------
	    String linea;
	    paso++;
	    String numeroPaso = (paso < 10) ? "0" + String.valueOf(paso) : String.valueOf(paso) ;
	    int contadorLinea = 0;
	    int i = 1;
	    
	    //----------------Método---------------------------------------------
	    
	    //---------------- Escribimos la plantilla JSORT
	    while((linea = lectorJSORT.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
	    	case 2:
	    		linea = linea.replace("//---D1", "//" + letraPaso + numeroPaso + "D" + String.valueOf(i));
	    		i++;
				break;
	    	case 4:
	    		linea = linea.replace("//---", "//" + letraPaso + numeroPaso);
	    		break;
	    	case 11:
	    		linea = linea.replace("FIELDS=(X,XX,XX,X)", datos.get("SORT"));
	    		break;
			default:
				break;
			}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }
	    lectorJSORT.close();
	    writeComments(datos, writerCortex);
	}
}

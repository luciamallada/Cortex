
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

}

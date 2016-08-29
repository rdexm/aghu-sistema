package br.gov.mec.aghu.ambulatorio.business;

public class MarcacaoConsultaSisregUtil {
	
	public static String formataString(String texto, Integer limiteMaximo) {
		String retorno = null;
		if (texto != null) {
			retorno = texto.trim();
			if (retorno.length() >= limiteMaximo) {
				retorno = retorno.substring(0, limiteMaximo - 1).trim();
			}
		}
		return retorno;
	}

}

package br.gov.mec.aghu.compras.constantes;

public enum EntregasGlobaisAcesso {

	SALDO_PROGRAMADO, VALOR_LIBERAR, VALOR_LIBERADO, VALOR_ATRASO;
	
	public static EntregasGlobaisAcesso getValue(String acesso) {
		if ("SALDO_PROGRAMADO".equals(acesso)) {
			return SALDO_PROGRAMADO;
		} else if ("VALOR_LIBERAR".equals(acesso)) {
			return VALOR_LIBERAR;
		} else if ("VALOR_LIBERADO".equals(acesso)) {
			return VALOR_LIBERADO;
		} else if ("VALOR_ATRASO".equals(acesso)) {
			return VALOR_ATRASO;
		} 
		return null;
	}
}

package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o sexo de uma pessoa
 * 
 * @author dansantos
 * 
 */
public enum DominioMesFeriado implements Dominio {
	
	JANEIRO("01"),
	FEVEREIRO("02"),
	MARCO("03"),
	ABRIL("04"),
	MAIO("05"),
	JUNHO("06"),
	JULHO("07"),
	AGOSTO("08"), 
	SETEMBRO("09"),
	OUTUBRO("10"),
	NOVEMBRO("11"),
	DEZEMBRO("12");
	
	private String value;
	
	private DominioMesFeriado(String value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return 0;
	}

	@Override
	public String getDescricao() {
		return value;
	}
	

}

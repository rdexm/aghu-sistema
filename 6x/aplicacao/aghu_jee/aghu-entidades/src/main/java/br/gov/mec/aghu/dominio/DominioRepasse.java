package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;



/**
 * INDICAR SE O RESULTADO O CALCULO DEVE REPASSAR PARA O PACIENTE OU SOMENTE PARA O OBJETO CUSTO
 * 
 * @author rogeriovieira
 * 
 */
public enum DominioRepasse implements Dominio {

	/**
	 * Paciente
	 */
	P, 
	/**
	 * Objeto de Custo
	 */
	C, 
	/**
	 * Nenhum
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Paciente";
		case C:
			return "Objeto de Custo";
		case N:
			return "Nenhum";
		default:
			return "";
		}
	}
	
	

}

package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o local da cobrança
 * 
 */
public enum DominioLocalCobranca implements Dominio {
	
	/**
	 * Cobrado em APAC - Autorização de Procedimentos de Auto-custo
	 */
	A,

	/**
	 * Cobrado na Internação
	 */
	I,
	
	/**
	 * Cobrado no Ambulatório
	 */
	B;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Aberta";
		case I:
			return "Cobrado na Internação";
		case B:
			return "Cobrado no Ambulatório";
		default:
			return "";
		}
	}

}

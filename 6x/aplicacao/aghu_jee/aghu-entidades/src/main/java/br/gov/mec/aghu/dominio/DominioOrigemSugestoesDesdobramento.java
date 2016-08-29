package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a origem do Sugestão Desdobramento
 * 
 * @author romario.caldeira
 *
 */
public enum DominioOrigemSugestoesDesdobramento implements Dominio {
	
	/**
	 * CCA.
	 */
	CCA,
	
	/**
	 * Faturamento.
	 */
	FAT,
	/**
	 * Bloco.
	 */
	UBC;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CCA:
			return "CCA - CCA";
		case FAT:
			return "FAT - FATURAMENTO";
		case UBC:
			return "UBC - BLOCO";
		default:
			return "";
		}
	}
}
package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoDataObito implements Dominio {
	/**
	 * dd/MM/yyyy
	 */
	DMA		("dd/MM/yyyy HH:mm", "##/##/#### ##:##"),
	/**
	 * MM/yyyy
	 */
	MES 	("MM/yyyy", "##/####"),
	/**
	 * YYYY
	 */
	ANO		("yyyy", "####"),
	/**
	 * Data Ignorada
	 */
	IGN		(null, null);
	
	private String pattern;
	
	private String mask;

	private DominioTipoDataObito(String pattern, String mask) {
		this.pattern = pattern;
		this.mask = mask;
	}

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case MES:
			return "Mês do Óbito";
		case DMA:
			return "Data do Óbito";
		case ANO:
			return "Ano do Óbito";
		case IGN:
			return "Data ignorada";
		default:
			return "";
		}
	}
	
	public String getPattern() {
		return pattern;
	}
	
	public String getMask() {
		return mask;
	}

}

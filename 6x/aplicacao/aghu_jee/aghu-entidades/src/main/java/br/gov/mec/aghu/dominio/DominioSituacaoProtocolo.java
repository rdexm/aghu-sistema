package br.gov.mec.aghu.dominio;


import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que a Situação do Protocolo 
 * * 
 * 
 */
public enum DominioSituacaoProtocolo implements Dominio {
	
	/**
	 * Ativos.
	 */
	A,

	/**
	 * Liberados.
	 */
	L,
	/**
	 * Em Construção.
	 */
	C,
	/**
	 * Em Homologação.
	 */
	H,
	/**
	 * Inativos.
	 */
	I;
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Ativos";
		case L:
			return "Liberados";
		case C:
			return "Em Construção";
		case H:
			return "Em Homologação";
		case I:
			return "Inativos";
		default:
			return "";
		}
	}
	
	
	public String getDescricaoSingular() {
		switch (this) {
		case A:
			return "Ativo";
		case L:
			return "Liberado";
		case C:
			return "Em Construção";
		case H:
			return "Em Homologação";
		case I:
			return "Inativo";
		default:
			return "";
		}
	}
}

package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o Estado Civil de uma pessoa 
 * 
 * @author ehgsilva
 * 
 */
public enum DominioTipoUsoMdtoAntimicrobia implements Dominio {
	
	/**
	 * Terapêutico Empírico
	 */
	T,

	/**
	 * Profilático
	 */
	P,
	
	/**
	 * Profilaxia Cirúrgica
	 */
	C,

	/**
	 * Terapêutico Específico
	 */
	E;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case T:
			return "Terapêutico Empírico";
		case P:
			return "Profilático";
		case C:
			return "Profilaxia Cirúrgica";
		case E:
			return "Terapêutico Específico";
		default:
			return "";
		}
	}

	public String obterDescricaoDetalhada() {
		switch (this) {
		case T:
			return "Uso Terapêutico Empírico";
		case P:
			return "Uso Profilático";
		case E:
			return "Uso Terapêutico Específico";
		default:
			return "";
		}
	}
	
	public String obterDescricaoDetalhadaTipo() {
		switch (this) {
		case T:
			return "Uso Terapêutico Empírico";
		case P:
			return "Uso Profilático";
		case C:
			return "Uso Profilaxia Cirúrgica";
		case E:
			return "Uso Terapêutico Específico";
		default:
			return "";
		}
	}

}
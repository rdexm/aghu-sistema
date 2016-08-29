package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoSolicitacaoMedicamento implements Dominio {
	/**
	 * Pendente.
	 */
	P,
	/**
	 * Tomada conhecimento.
	 */
	T,
	
	/**
	 * Avaliada.
	 */
	A;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Pendente";
		case T:
			return "Tomada conhecimento";
		case A:
			return "Avaliada";
		default:
			return "";
		}
	}
	
	public String getCharDescricar(){
		switch (this) {
		case P:
			return "P";
		case T:
			return "T";
		case A:
			return "A";
		default:
			return "";
		}
	}
}
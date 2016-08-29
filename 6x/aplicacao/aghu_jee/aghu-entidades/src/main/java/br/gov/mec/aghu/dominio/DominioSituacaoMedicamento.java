package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author bsoliveira
 *
 */
public enum DominioSituacaoMedicamento implements Dominio {
	
	/**
	 * Pendente
	 */
	P, 
	/**
	 * Ativo
	 */
	A,
	/**
	 * Inativo
	 */
	I,
	/**
	 * Bloqueio COMEDI
	 */
	C,
	/**
	 * Bloqueio Consumo
	 */
	B;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Pendente";
		case A:
			return "Ativo";
		case I:
			return "Inativo";
		case C:
			return "Bloqueio COMEDI";
		case B:
			return "Bloqueio Consumo";	
		default:
			return "";
		}
	}

}

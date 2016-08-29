package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioItemContaHospitalar implements Dominio {
	//Dominio gerado no momento do multirao para arrumar os pojos do modulo de prescricao.
	//Inserir as descricoes quando estiver contextualizado com os seus respectivos significados.
	
	/**
	 * TODO inserir descricao
	 */
	A,

	/**
	 * TODO inserir descricao
	 */
	C;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "A";
		case C:
			return "C";
		default:
			return "";
		}
	}

}

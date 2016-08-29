package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoCalculo implements Dominio{
	/**
	 * Custo Médio do Objeto de Custo
	 */
	CM,
	/**
	 * Custo de um Evento do Paciente
	 */
	CP;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CM:
			return "Custo Médio do Objeto de Custo";
		case CP:
			return "Custo de um Evento do Paciente";
		default:
			return "";
		}
	}

}

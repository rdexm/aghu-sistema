package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * Domínio para o tipo de estoque do relatório de consumo sintético de materiais
 * 
 * @author aghu
 * 
 */
public enum DominioEstocavelConsumoSinteticoMaterial implements Dominio {
	S, N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Estocável";
		case N:
			return "Consumo Eventual";
		default:
			return "";
		}
	}

}

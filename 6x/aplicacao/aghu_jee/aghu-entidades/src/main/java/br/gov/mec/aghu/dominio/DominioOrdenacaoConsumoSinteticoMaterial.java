package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * Domínio para ordenação do relatório de consumo sintético de materiais
 * 
 * @author aghu
 * 
 */
public enum DominioOrdenacaoConsumoSinteticoMaterial implements Dominio {
	C, N, V;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	public String getDescricao() {
		switch (this) {
		case C:
			return "Código Material";
		case N:
			return "Nome Material";
		case V:
			return "Valor";
		default:
			return "";
		}
	}

}

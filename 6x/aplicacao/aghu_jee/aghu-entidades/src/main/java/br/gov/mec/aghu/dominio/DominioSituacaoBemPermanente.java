package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação de um bem permanente.
 * 
 */
public enum DominioSituacaoBemPermanente implements Dominio {

	AQUISICAO_TEMPORARIA,
	EM_USO,
	EM_SINDICANCIA,
	EM_MANUTENCAO,
	BAIXADO,
	DEVOLVIDO,
	EM_DESFAZIMENTO;

	@Override
	public int getCodigo() {

		return this.ordinal() + 1;
	}

	@Override
	public String getDescricao() {

		switch (this) {
		case AQUISICAO_TEMPORARIA:
			return "Aquisição Temporária";
		case EM_USO:
			return "Em Uso";
		case EM_SINDICANCIA:
			return "Em Sindicância";
		case EM_MANUTENCAO:
			return "Em Manutenção";
		case BAIXADO:
			return "Baixado";
		case DEVOLVIDO:
			return "Devolvido";
		case EM_DESFAZIMENTO:
			return "Em Desfazimento";
		default:
			return "";
		}
	}

}

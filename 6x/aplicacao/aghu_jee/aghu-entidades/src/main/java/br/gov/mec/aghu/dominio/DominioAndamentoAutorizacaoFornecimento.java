package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioAndamentoAutorizacaoFornecimento implements Dominio {

	LIBERAR_AF_ASSINATURA,
	AF_LIBERADA_ASSINATURA,
	VERSAO_JA_ASSINADA,
	VERSAO_EMPENHADA,
	ALTERACAO_PENDENTE_JUSTIFICATIVA;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		
		switch (this) {
		case LIBERAR_AF_ASSINATURA:
			return "Liberar AF p/ Assinatura";
		case AF_LIBERADA_ASSINATURA:
			return "Liberada para Assinatura";
		case VERSAO_JA_ASSINADA:
			return "Versão já Assinada";
		case VERSAO_EMPENHADA:
			return "Versão empenhada";
		case ALTERACAO_PENDENTE_JUSTIFICATIVA:
			return "Alteração Pendente de Justificativa";
		default:
			return this.toString();	
		}
	}
	
}

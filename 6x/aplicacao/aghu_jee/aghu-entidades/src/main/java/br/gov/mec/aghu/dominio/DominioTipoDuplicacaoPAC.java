package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o local de coleta da classe AelItemSolicitacaoExames.
 * 
 * @author frutkowski
 */
public enum DominioTipoDuplicacaoPAC implements Dominio {
	/**
	 * Ate abertura do PAC
	 */
	ABERTURA_PAC,
	/**
	 * Ate Autorização de Fornecimento
	 */
	AUTORIZACAO_FORN;
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ABERTURA_PAC:
			return "Duplicar das Solicitações até abertura do Processo de Compra";
		case AUTORIZACAO_FORN:
			return "Duplicar das Solicitações até a autorização de fornecimento";		
		default:
			return "";
		}
	}
}
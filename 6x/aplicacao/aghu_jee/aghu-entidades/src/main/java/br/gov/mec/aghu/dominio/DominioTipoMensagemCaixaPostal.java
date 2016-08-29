package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Tipo Msg caixa postal da entidade AelItemSolicitacaoExames.
 * 
 * @author rcorvalao
 *
 */
public enum DominioTipoMensagemCaixaPostal implements Dominio {
	NA, NE
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		return this.toString();
	}
}

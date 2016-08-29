package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Forma respiracao da entidade AelItemSolicitacaoExames.
 * 
 * @author rcorvalao
 *
 */
public enum DominioFormaRespiracao implements Dominio {
	UM(1), DOIS(2), TRES(3)
	;

	private int value;
	
	private DominioFormaRespiracao(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return this.value;
	}
	
	@Override
	public String getDescricao() {
		switch (this) {
		case UM:
			return "Respirando em ar ambiente";
		case DOIS:
			return "Recebendo _ litros/minuto de oxigênio";
		case TRES:
			return "Em ventilação mecânica com fração inspirada de oxigênio de _%";
		default:
			return "";
		}
	}
}

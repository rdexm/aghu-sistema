package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o status de um ticket.
 * 
 */
public enum DominioStatusTicket implements Dominio {

	AGUARDANDO_ATENDIMENTO,
	EM_ATENDIMENTO,
	EXPIRADO,
	CONCLUIDO,
	CANCELADO;


	@Override
	public int getCodigo() {
		return this.ordinal() + 1;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AGUARDANDO_ATENDIMENTO:
			return "Aguardando Atendimento";
		case EM_ATENDIMENTO:
			return "Em Atendimento";
		case EXPIRADO:
			return "Expirado";
		case CONCLUIDO:
			return "Concluído";
		case CANCELADO:
			return "Cancelado";
		default:
			return "";
		}
	}
	
	
	public static String obterDominioStatusNotificacaoTecnica(Integer value) {
		switch (value) {
		case 1:
			return DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getDescricao();			
		case 2:
			return DominioStatusTicket.EM_ATENDIMENTO.getDescricao();			
		case 3:
			return DominioStatusTicket.EXPIRADO.getDescricao();
		case 4:
			return DominioStatusTicket.CONCLUIDO.getDescricao();
		case 5:
			return DominioStatusTicket.CANCELADO.getDescricao();
		default:
			return null;
		}
	}

}

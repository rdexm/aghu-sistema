package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoProcessamentoCusto implements Dominio{
	/**
	 * Em Processamento
	 */
	A,
	
	/**Processado	 */
	P,
	
	/**Erro no processamento*/
	E,
	
	/**Fechado*/
	F,
	
	/**Agendamento manual*/
	S,
	
	/**Em Andamento Diário*/
	AD,	
	
	PD,
	
	FD,
	
	/**Erro Diário*/
	ED,
	
	/**Agendado Manual Diário*/
	SD
	;	
	
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Agendamento manual";
		case A:
			return "Em Processamento";
		case P:
			return "Processado";
		case E:
			return "Erro no Processamento";
		case F:
			return "Fechado";
		case SD:
			return "Agendamento manual Diário";			
		case AD:
			return "Em Andamento Diário";
		case PD:
			return "Processado Diário";
		case FD:
			return "Fechado Diário";
		case ED:
			return "Erro no processamento Diário";			
		default:
			return "";
		}
	}

}

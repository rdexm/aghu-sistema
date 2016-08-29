package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * IndSituacao da MbcAgendas
 * 
 * @author cristiane barbado
 *
 */
public enum DominioLadoCirurgiaAgendas implements  Dominio {
	 D		
	,E		
	,B
	,N
	;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case D:
			return "Direito";
		case E:
			return "Esquerdo";
		case B:
			return "Bilateral";
		case N:
			return "Não se aplica";
		default:
			return "";
		}
	}
	
	public String getDescricaoDefaultNA() {
		switch (this) {
		case D:
			return "Direito";
		case E:
			return "Esquerdo";
		case B:
			return "Bilateral";
		default:
			return "Não se aplica";
		}
	}
}

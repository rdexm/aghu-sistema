package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação do titulo
 * 
 * @author lsamberg
 * 
 */
public enum DominioSituacaoTituloSemPnd implements Dominio {
	//A Pagar
	APG,
	//Bloqueado
	BLQ,
	//Pago
	PG;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case APG:
			return "A Pagar";
		case BLQ:
			return "Bloqueado";
		case PG:
			return "Pago";	
		default:
			return "";
		}
	}
	
	public static DominioSituacaoTituloSemPnd getInstance(String value){
		if (value != null && value.equals("APG")){
			return DominioSituacaoTituloSemPnd.APG;
		}
		if (value != null && value.equals("BLQ")){
			return DominioSituacaoTituloSemPnd.BLQ;
		}
		if (value != null && value.equals("PG")){
			return DominioSituacaoTituloSemPnd.PG;
		}
	
		return null;
	}
}

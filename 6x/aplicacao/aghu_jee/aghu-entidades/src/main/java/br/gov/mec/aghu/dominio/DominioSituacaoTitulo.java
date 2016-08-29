package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação do titulo
 * 
 * @author lsamberg
 * 
 */
public enum DominioSituacaoTitulo implements Dominio {
	//A Pagar
	APG,
	//Bloqueado
	BLQ,
	//Pago
	PG,
	// Pendente
	PND;
	
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
		case PND:
			return "Pendente";	
		default:
			return "";
		}
	}
	
	public static DominioSituacaoTitulo getInstance(String value){
		if (value != null && value.equals("APG")){
			return DominioSituacaoTitulo.APG;
		}
		if (value != null && value.equals("BLQ")){
			return DominioSituacaoTitulo.BLQ;
		}
		if (value != null && value.equals("PG")){
			return DominioSituacaoTitulo.PG;
		}
		if (value != null && value.equals("PND")){
			return DominioSituacaoTitulo.PND;
		}
		return null;
	}
}

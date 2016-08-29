package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author dfriedrich
 *
 */
public enum DominioMcoType implements Dominio {

	
	/**
	 * FIN - Fornecimento Indústria
	 */
    FIN,
    
    /**
	 * FOB - Fornecimento Outro Banco de Sangue
	 */
    FOB,
    
    /**
	 * FPA - Fornecimento ao Paciente
	 */
    FPA,
    
    /**
	 * OUS - Outra Saída
	 */
    OUS,
    
    /**
	 *REJ - Rejeição
	 */
    REJ;       

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case FIN:
			return "Fornecimento Indústria";
		case FOB:
			return "Fornecimento Outro Banco de Sangue";
		case FPA:
			return "Fornecimento ao Paciente";
		case OUS:
			return "Outra Saída";
		case REJ:
			return "Rejeição";
		default:
			return "";
		}
	}

}
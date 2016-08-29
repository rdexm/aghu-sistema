package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a localização do paciente
 * 
 * @author lalegre
 * 
 */
public enum DominioLocalizacaoPaciente implements Dominio {
	
	/**
	 * Todos
	 **/
	T,
	 /**
	 * Emergência
	 **/
	E,
	/**
	 * Internação
	 **/
	I
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case T:
			return "Todos";
			
		case I:
			return "Internação";

		case E:
			return "Emergência";
		
		default:
			return "";
		}
	}

}

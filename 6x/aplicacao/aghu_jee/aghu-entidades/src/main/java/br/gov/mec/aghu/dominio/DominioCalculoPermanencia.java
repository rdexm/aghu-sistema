package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação de um calculo de permanencia.
 * 
 * @author rmalvezzi 
 * 
 */
public enum DominioCalculoPermanencia implements Dominio {
	/**
	 * Unidade de internação
	 */
	UI,
	
	/**
	 * Unidade ambulatorial
	 */
	UA, 

	/**
	 * Serviço médico (especialidade)
	 */
	SM,

	/**
	 * Equipe assistencial
	 */
	EQ;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case UI:
			return "Unidade de internação";
		case SM:
			return "Serviço médico (especialidade)";
		case EQ:
			return "Equipe assistencial";
		case UA:
			return "Unidade ambulatorial";
		default:
			return "";
		}
	}

}

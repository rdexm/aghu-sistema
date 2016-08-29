package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica tipo da certidão de um paciente
 * 
 * @author ehgsilva
 * 
 */
public enum DominioTipoCertidao implements Dominio {
	
	/**
	 * Nascimento
	 */
	NASCIMENTO(91),
	
	/**
	 * Casamento
	 */
	CASAMENTO(92),
	
	/**
	 * Separação/Divórcio
	 */
	SEPARACAO_DIVORCIO(93),
	
	/**
	 * Indígena
	 */
	INDIGENA(95);

	private int value;
	
	private DominioTipoCertidao(int value) {
		this.value = value;
	}
	
	
	@Override
	public int getCodigo() {
		return value;
	}


	@Override
	public String getDescricao() {
		switch (this) {
		case NASCIMENTO:
			return "Nascimento";
		case CASAMENTO:
			return "Casamento";
		case SEPARACAO_DIVORCIO:
			return "Separação/Divórcio";
		case INDIGENA:
			return "Indígena";
		default:
			return "";
		}
	}

}

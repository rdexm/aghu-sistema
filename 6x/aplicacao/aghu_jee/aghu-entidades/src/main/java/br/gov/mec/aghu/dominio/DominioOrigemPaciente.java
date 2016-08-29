package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * Domínio que indica a origem dos pacientes admitidos
 * 
 * @author Stanley Araujo
 * 
 */
public enum DominioOrigemPaciente implements Dominio {
	
	 /**
	 * Admissão
	 */
	A,
	 /**
	 * Emergência
	 **/
	E,
	/**
	 * Transferência
	 **/
	T;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	public String getDescricaoBanco(){
		switch (this) {
		case A:
			return "ADMISSAO";

		case E:
			return "EMERGENCIA";

		case T:
			return "TRANSFERENCIA";
			
		default:
			return "";
		}
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "ADMISSÃO";

		case E:
			return "EMERGÊNCIA";

		case T:
			return "TRANSFERÊNCIA";

		
		default:
			return "";

		}
	}

}

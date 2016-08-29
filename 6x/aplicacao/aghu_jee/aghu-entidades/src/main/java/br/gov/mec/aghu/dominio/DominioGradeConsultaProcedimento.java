package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica se uma grade é de consulta ou procedimento.<br>
 * Consulta equivale a Boolean.FALSE, Procedimento a Boolean.TRUE
 * 
 * @author diego.pacheco
 * 
 */
public enum DominioGradeConsultaProcedimento implements Dominio {

	/**
	 * Consulta
	 */
	C,
	
	/**
	 * Procedimento
	 */
	P;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Consulta";
		case P:
			return "Procedimento";			
		default:
			return "";
		}
	}
	
	/**
	 * Método criado para ajudar os mapeamentos sintéticos para boolean
	 * 
	 * @return
	 */
	public boolean isProcedimento() {
		switch (this) {
		case P:
			return Boolean.TRUE;
		default:
			return Boolean.FALSE;
		}
		
	}	

}

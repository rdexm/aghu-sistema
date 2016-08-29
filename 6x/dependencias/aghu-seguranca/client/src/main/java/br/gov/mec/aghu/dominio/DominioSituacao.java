package br.gov.mec.aghu.dominio;

import br.gov.mec.dominio.Dominio;

/**
 * Domínio que indica a situação de uma entidade, se ativa ou inativa.
 * 
 * @author gmneto
 * 
 */
public enum DominioSituacao implements Dominio {
	/**
	 * Ativo
	 */
	A,

	/**
	 * Inativo
	 */
	I;

	public int getCodigo() {
		return this.ordinal();
	}

	public String getDescricao() {
		switch (this) {
		case I:
			return "Inativa";
		case A:
			return "Ativa";
		default:
			return "";
		}
	}
	
	
	/**
	 * Método criado para ajudar os mapeamentos sintéticos para boolean
	 * @return
	 */
	public boolean isAtivo(){
		switch (this) {
		case A:
			return Boolean.TRUE;
		case I:			
		default:
			return Boolean.FALSE;
		}
	}
	
	public static DominioSituacao getInstance(boolean valor){
		if (valor){
			return DominioSituacao.A;
		}
		else{
			return DominioSituacao.I;
		}
	}

}

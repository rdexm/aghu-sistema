package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

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

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Inativo";
		case A:
			return "Ativo";
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
			return Boolean.FALSE;
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

package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação do vinculo de um servidor.
 * 
 * @author gmneto
 * 
 */
public enum DominioSituacaoVinculo implements Dominio {
	/**
	 * Ativa
	 */
	A,

	/**
	 * Inativa
	 */
	I,
	/**
	 * Programado
	 */
	P;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Inativa";
		case A:
			return "Ativa";
		case P:
			return "Programado";
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
	
	public boolean isProgramado() {
		switch (this) {
		case P:
			return true;
		default:
			return false;
		}
	}
	
	public static DominioSituacaoVinculo getInstance(boolean valor){
		if (valor){
			return DominioSituacaoVinculo.A;
		}
		else{
			return DominioSituacaoVinculo.I;
		}
	}

}

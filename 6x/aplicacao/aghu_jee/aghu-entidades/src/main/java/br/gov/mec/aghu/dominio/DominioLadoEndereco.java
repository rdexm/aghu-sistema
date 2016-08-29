package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o lado relativo ao cep no logradouro.
 * 
 * @author gmneto
 * 
 */
public enum DominioLadoEndereco implements Dominio {


	/**
	 * Ambos
	 */
	A,

	/**
	 * Direita
	 */
	D,
	
	/**
	 * Esquerda
	 */
	E,
	
	/**
	 * Impar
	 */
	I,

	/**
	 * Par
	 */
	P;	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Ambos";
		case D:
			return "Direita";
		case E:
			return "Esquerda";	
		case I:
			return "Ímpar";
		case P:
			return "Par";
		default:
			return "";
		}
	}

}

package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a cor de uma pessoa.
 * 
 * @author ehgsilva
 * 
 */
public enum DominioCor implements Dominio {

	/**
	 * Branca
	 */
	B("Branca", 1),

	/**
	 * Preta
	 */
	P("Preta", 2),

	/**
	 * Parda
	 */
	M("Parda", 3),

	/**
	 * Amarela
	 */
	A("Amarela", 4),

	/**
	 * Indígena
	 */
	I("Indígena", 5),

	/**
	 * Outros
	 */
	O("Sem Declaração", 99);	// Alterado de Outros para Sem Declaração, conforme ISSUE #19113
	private int codigo;
	private String descricao;

	DominioCor(final String descricao, int codigo) {
		this.descricao = descricao;
		this.codigo = codigo;
	}

	@Override
	public int getCodigo() {
		return codigo;
	}

	@Override
	public String getDescricao() {
		return this.descricao;
	}

	public static DominioCor getInstance(String valor) {
		if ("B".equals(valor)) {
			return DominioCor.B;
		} else if ("P".equals(valor)) {
			return DominioCor.P;
		} else if ("M".equals(valor)) {
			return DominioCor.M;
		} else if ("A".equals(valor)) {
			return DominioCor.A;
		} else if ("I".equals(valor)) {
			return DominioCor.I;
		} else if ("O".equals(valor)) {
			return DominioCor.O;			
		} else {
			return null;
		}
	}
	
}

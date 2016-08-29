package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o Grupo Sanguineo de uma pessoa. Contém valores
 * especifícos para Atendimento Diverso.
 * 
 * @author dpacheco
 * 
 */
public enum DominioGrupoSanguineoAtendimentoDiverso implements
		Dominio {

	/**
	 * O
	 */
	O,

	/**
	 * A
	 */
	A,

	/**
	 * B
	 */
	B,

	/**
	 * AB
	 */
	AB,

	/**
	 * Indeterminado
	 */
	I;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case O:
			return "O";
		case A:
			return "A";
		case B:
			return "B";
		case AB:
			return "AB";
		case I:
			return "Indeterminado";
		default:
			return "";
		}
	}
}

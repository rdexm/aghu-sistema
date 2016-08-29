package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação de um cálculo de paciente.
 * 
 * @author rmalvezzi
 * 
 */
public enum DominioSituacaoCalculoPaciente implements Dominio {
	/**
	 * Internação com alta faturada
	 */
	A,

	/**
	 * Internação em andamento
	 */
	I,

	/**
	 * Internação com alta
	 */
	IA;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Internação em andamento";
		case A:
			return "Internação com alta faturada";
		case IA:
			return "Internação com alta";
		default:
			return "";
		}
	}

}
package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrigemPacienteAmbulatorio implements Dominio {
	

	/**
	 * Consulta
	 */
	CONSULTA(1),
	
	/**
	 * Anamnese
	 */
	ANAMNESE(2),
	
	/**
	 * Evolução
	 */
	EVOLUCAO(3),

	/**
	 * Triagem da emergência
	 */
	TRIAGEM_EMERG(4),
	
	/**
	 * Atendimentos
	 */
	ATENDIMENTOS(5),
	
	/**
	 * Nro de registro da emergencia
	 */	
	REG_EMERGENCIA(6),
	
	/**
	 * Nro de registro tratando atd e trg
	 */
	REG_ATEND_TRIAGEM(7);
	
	private int value;

	private DominioOrigemPacienteAmbulatorio(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CONSULTA:
			return "1";
		case ANAMNESE:
			return "2";
		case EVOLUCAO:
			return "3";
		case TRIAGEM_EMERG:
			return "4";
		case ATENDIMENTOS:
			return "5";
		case REG_EMERGENCIA:
			return "6";
		case REG_ATEND_TRIAGEM:
			return "7";
		default:
			return "";
		}
	}
		

}

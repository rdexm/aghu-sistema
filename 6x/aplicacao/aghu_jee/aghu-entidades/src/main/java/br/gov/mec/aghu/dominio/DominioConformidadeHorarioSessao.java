package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio contendo os possíveis status de uma reserva de horário.
 * 
 * @author rodrigo.dias
 *
 */
public enum DominioConformidadeHorarioSessao implements Dominio {
	
	/**
	 * Sem diferenças (Conforme).
	 */
	SEM_DIFERENCAS(1),
	
	/**
	 * Horas reservadas maiores que a prescrita (Conforme).
	 */
	HORAS_RESERVADAS_MAIORES_PRESCRITA(2),
	
	/**
	 * Dias reservados a mais que a prescrição (Conforme).
	 */
	DIAS_RESERVADOS_MAIS_PRESCRICAO(3),
	
	/**
	 * Dia reservado diferente do prescrito (Não Conforme).
	 */
	DIA_RESERVADO_DIFERENTE_PRESCRITO(4),
	
	/**
	 * Horas prescritas maiores que a reservada (Não Conforme).
	 */
	HORAS_PRESCRITAS_MAIORES_RESERVADA(5);

	private int codigo;

	private DominioConformidadeHorarioSessao(int codigo) {

		this.codigo = codigo;
	}

	@Override
	public int getCodigo() {

		return codigo;
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case SEM_DIFERENCAS: return "Sem diferenças (Conforme).";
			case HORAS_RESERVADAS_MAIORES_PRESCRITA: return "Horas reservadas maiores que a prescrita (Conforme).";
			case DIAS_RESERVADOS_MAIS_PRESCRICAO: return "Dias reservados a mais que a prescrição (Conforme).";
			case DIA_RESERVADO_DIFERENTE_PRESCRITO: return "Dia reservado diferente do prescrito (Não Conforme).";
			case HORAS_PRESCRITAS_MAIORES_RESERVADA: return "Horas prescritas maiores que a reservada (Não Conforme).";
			default: return "";
		}
	}
}
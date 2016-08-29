package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrigemDado implements Dominio {

	/**
	 * Prescrição de Fisiatria
	 */
	FIS,
	/**
	 * Banco de Sangue
	 **/
	ABS,
	/**
	 * Exames
	 **/
	AEL,
	/**
	 * Dispensação de Medicamentos
	 **/
	AFA,
	/**
	 * Internação
	 **/
	AIN,
	/**
	 * Bloco Cirúrgico
	 **/
	BCC,
	/**
	 * Digitado
	 **/
	DIG,
	/**
	 * Prescrição Médica
	 **/
	MPM,
	/**
	 * Consultas
	 **/
	CON,
	/**
	 * Prescrição de Diálise
	 **/
	MPT,
	/**
	 * Nutrição
	 **/
	ANU,
	/**
	 * Centro Obstétrico
	 **/
	MCO;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case FIS:
			return "Prescrição de Fisiatria";
		case ABS:
			return "Banco de Sangue";
		case AEL:
			return "Exames";
		case AFA:
			return "Dispensação de Medicamentos";
		case AIN:
			return "Internação";
		case BCC:
			return "Bloco Cirúrgico";
		case DIG:
			return "Digitado";
		case MPM:
			return "Prescrição Médica";
		case CON:
			return "Consultas";
		case MPT:
			return "Prescrição de Diálise";
		case ANU:
			return "Nutrição";
		case MCO:
			return "Centro Obstétrico";
		default:
			return "";
		}
	}

}

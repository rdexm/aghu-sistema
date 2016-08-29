package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioIndOrigemItemContaHospitalar implements Dominio {
	
	
	/**
	 * Prescrição Médica
	 */ 
	MPM,
	
	/**
	 * Sistema Banco de Sangue
	 */
	ABS,
	
	/**
	 * Bloco Cirúrgico
	 */
	BCC,
	
	/**
	 * Digitado
	 */
	DIG,
	
	/**
	 * Sistema de Exames e Laudos
	 */
	AEL,
	
	/**
	 * Sistema de Internação
	 */
	AIN,
	
	/**
	 * Consultas
	 */
	CON,
	
	/**
	 * Sistema de Farmácia
	 */
	AFA,
	
	/**
	 * Procedimentos Terapêuticos
	 */
	MPT,
	
	/**
	 * Sistema de Nutrição
	 */
	ANU,
	
	/**
	 * Sistema da Perinatologia (Co + Neonatologia)
	 */
	MCO,
	
	/**
	 * Fisioterapia
	 */
	FIS;

	public DominioIndOrigemItemContaHospitalar getObjeto() {
		return this;
	}
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case MPM:
			return "MPM - Prescrição Médica";
		case ABS:
			return "ABS - Sistema Banco de Sangue";
		case BCC:
			return "BCC - Bloco Cirúrgico";
		case DIG:
			return "DIG - Digitado";
		case AEL:
			return "AEL - Sistema de Exames e Laudos";
		case AIN:
			return "AIN - Sistema de Internação";
		case CON:
			return "CON - Consultas";
		case AFA:
			return "AFA - Sistema de Farmácia";
		case MPT:
			return "MPT - Procedimentos Terapêuticos";
		case ANU:
			return "ANU - Sistema de Nutrição";
		case MCO:
			return "MCO - Sistema da Perinatologia (Co + Neonatologia)";
		case FIS:
			return "FIS - Fisioterapia";
		default:
			return "";
		}
	}
}
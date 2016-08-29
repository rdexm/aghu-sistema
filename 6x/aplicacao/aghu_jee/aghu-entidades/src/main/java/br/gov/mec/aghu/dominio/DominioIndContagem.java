package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioIndContagem implements Dominio {
	/**
	 * Procedimentos Cirúrgicos
	 */
	CR,
	
	/**
	 * Consultorias Médicas
	 */
	CS,
	
	/**
	 * Cuidados de Enfermagem
	 */
	CE,
	
	/**
	 * Dietas
	 */
	DI,
	
	/**
	 * Exames
	 */
	EX,
	
	/**
	 * Hemoterapias
	 */
	HM,
	
	/**
	 * Medicamentos
	 */
	MD,
	
	/**
	 * Nutrições Parenterais
	 */
	NP,
	
	/**
	 * Órteses e Próteses
	 */
	OP,
	
	/**
	 * Procedimentos Especiais
	 */
	PE, 
	
	/**
	 * Cuidados de Quimioterapias
	 */
	QC,
	
	/**
	 * Medicamentos de Quimioterapias
	 */
	QM, 
	
	/**
	 * Cuidados de Diálise
	 */
	DC, 
	
	/**
	 * Medicamentos de Diálise
	 */
	DM, 
	
	/**
	 * Procedimentos e Materiais de Diálise
	 */
	DP,
	
	/**
	 * Objetos de custo de Apoio com repasse para o paciente
	 */
	AP,
	
	/** 
	 * Outras Receitas
	 */
	OR
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CR:
			return "Procedimentos Cirúrgicos";
		case CS:
			return "Consultorias Médicas";
		case CE:
			return "Cuidados de Enfermagem";
		case DI:
			return "Dietas";
		case EX:
			return "Exames";
		case HM:
			return "Hemoterapias";	
		case MD:
			return "Medicamentos";
		case NP:
			return "Nutrições Parenterais";	
		case OP:
			return "Órteses e Próteses";
		case PE:
			return "Procedimentos Especiais";
		case QC:
			return "Cuidados de Quimioterapias";
		case QM:
			return "Medicamentos de Quimioterapias";
		case DC:
			return "Cuidados de Diálise";
		case DM:
			return "Medicamentos de Diálise";
		case DP:
			return "Procedimentos e Materiais de Diálise";
		case AP:
			return "Objetos de custo de Apoio"; 
		case OR:
			return "Outras Receitas";
		default:
			return "";
		}
	}
}

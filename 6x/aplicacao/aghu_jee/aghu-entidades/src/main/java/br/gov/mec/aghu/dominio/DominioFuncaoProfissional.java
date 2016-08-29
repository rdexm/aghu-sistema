package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

/**
 * Domínio que indica a função do professor
 */
public enum DominioFuncaoProfissional implements DominioString {
	/**
	 * Médico contratado
	 */
	MCO("MCO"),
	/**
	 * Médico professor
	 */
	MPF("MPF"),
	/**
	 * Anestesista professor
	 */
	ANP("ANP"),
	/**
	 * Anestesista residente
	 */
	ANR("ANR"),
	/**
	 * Anestesista contratado
	 */
	ANC("ANC"),
	/**
	 * Enfermeiro
	 */
	ENF("ENF"),
	/**
	 * Instrumentador
	 */
	INS("INS"),
	/**
	 * Médico auxiliar
	 */
	MAX("MAX"),
	/**
	 * Circulante
	 */
	CIR("CIR"),
	/**
	 * Executor Sedação
	 */
	ESE("ESE"),
	/**
	 * Médico residente
	 */
	MRE("MRE");

	private String value;
	
	private DominioFuncaoProfissional(String value) {
		this.value = value;
	}	

	@Override
	public String getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case MCO:
			return "Médico contratado";
		case MPF:
			return "Médico professor";
		case ANP:
			return "Anestesista professor";
		case ANR:
			return "Anestesista residente";
		case ANC:
			return "Anestesista contratado";
		case ENF:
			return "Enfermeiro";
		case INS:
			return "Instrumentador";
		case MAX:
			return "Médico auxiliar";
		case CIR:
			return "Circulante";
		case ESE:
			return "Executor Sedação";
		case MRE:
			return "Médico residente";
		default:
			return "";
		}
	}

	public static DominioFuncaoProfissional getInstance(String value) {
		if ("MCO".equals(value)) {
			return MCO;
		} else if ("MPF".equals(value)) {
			return MPF;
		} else if ("ANP".equals(value)) {
			return ANP;
		} else if ("ANR".equals(value)) {
			return ANR;
		} else if ("ANC".equals(value)) {
			return ANC;
		} else if ("ENF".equals(value)) {
			return ENF;
		} else if ("INS".equals(value)) {
			return INS;
		} else if ("MAX".equals(value)) {
			return MAX;
		} else if ("CIR".equals(value)) {
			return CIR;
		} else if ("ESE".equals(value)) {
			return ESE;
		}else if ("MRE".equals(value)) {
			return MRE;
		} else {
			return null;
		}
	}
	
}
 
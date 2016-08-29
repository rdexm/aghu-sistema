package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

/**
 * Domínio que indica o regime do SUS para procedimentos cirúrgicos.
 * 
 * @author dpacheco
 *
 */
public enum DominioRegimeProcedimentoCirurgicoSus implements DominioString {
	
	/**
	 * Ambulatório
	 */
	AMBULATORIO("A"),
	
	/**
	 * Hospital Dia
	 */
	HOSPITAL_DIA("H"),
	
	/**
	 * Internação até 72h
	 */
	INTERNACAO_ATE_72H("9"),
	
	/**
	 * Internação
	 */
	INTERNACAO("I");
	
	
	private String valor;
	
	private DominioRegimeProcedimentoCirurgicoSus(String valor) {
		this.valor = valor;
	}

	@Override
	public String getCodigo() {
		return this.valor;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AMBULATORIO:
			return "Ambulatório";
		case HOSPITAL_DIA:
			return "Hospital Dia";
		case INTERNACAO_ATE_72H:
			return "Internação até 72h";
		case INTERNACAO:
			return "Internação";
		default:
			return "";
		}
	}
	
	/**
	 * ORADB MBCK_AGD_RN.MBCC_GET_ORD_REGIME
	 * @return
	 * 
	 * busca a ordem do regime procedimento sus
	 */
	public Integer getOrdem(){
		switch (this) {
		case AMBULATORIO:
			return 1;
		case HOSPITAL_DIA:
			return 2;
		case INTERNACAO_ATE_72H:
			return 3;
		case INTERNACAO:
			return 4;
		default:
			return null;
		}
	}
	
	/**
	 * Obtém o regime do procedimento através da origem do paciente na cirúrgia
	 * @param origemPacienteCirurgia
	 * @return DominioRegimeProcedimentoCirurgicoSus
	 */
	public static DominioRegimeProcedimentoCirurgicoSus getRegimePorOrigemPacienteCirurgia(DominioOrigemPacienteCirurgia origemPacienteCirurgia) {
		switch (origemPacienteCirurgia) {
		case A:
			return AMBULATORIO;
		case I:
			return INTERNACAO;
		default:
			throw new IllegalArgumentException("DominioOrigemPacienteCirurgia não informado");
		}
	}
}
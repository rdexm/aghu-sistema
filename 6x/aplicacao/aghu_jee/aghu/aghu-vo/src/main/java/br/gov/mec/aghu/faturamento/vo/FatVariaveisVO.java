package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioTipoIdadeUTI;

public class FatVariaveisVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -257432184401209096L;

	/**
	 * FATK_CTH2_RN_UN.V_MAIOR_VALOR
	 */
	private Boolean vMaiorValor;

	/**
	 * FATK_PMR_RN_V_PMR_JOURNAL
	 */
	private Boolean vPmrJournal;
	
	/**
	 * FATK_PMR_RN_V_PMR_ENCERRAMENTO 
	 */
	private Boolean vPmrEncerramento;
	
	/**
	 * FATK_CTH2_RN_UN.V_IDADE_BCO_UTI
	 * 
	 */
	private DominioTipoIdadeUTI idadeBlocoUti;
	
	public Boolean getvMaiorValor() {
		return vMaiorValor;
	}

	public void setvMaiorValor(Boolean vMaiorValor) {
		this.vMaiorValor = vMaiorValor;
	}

	public Boolean getvPmrJournal() {
		return vPmrJournal;
	}

	public void setvPmrJournal(Boolean vPmrJournal) {
		this.vPmrJournal = vPmrJournal;
	}

	public Boolean getvPmrEncerramento() {
		return vPmrEncerramento;
	}

	public void setvPmrEncerramento(Boolean vPmrEncerramento) {
		this.vPmrEncerramento = vPmrEncerramento;
	}

	public DominioTipoIdadeUTI getIdadeBlocoUti() {
		return idadeBlocoUti;
	}

	public void setIdadeBlocoUti(DominioTipoIdadeUTI idadeBlocoUti) {
		this.idadeBlocoUti = idadeBlocoUti;
	}
}

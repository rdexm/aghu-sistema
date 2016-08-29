package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;


public class VerificaIndicadoresConvenioInternacaoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5325090715286809364L;

	private Integer phiSeq;
	
	private Boolean indExigeJustificativa;
	
	private Boolean indImprimeLaudo;
	
	private Boolean indCobrancaFracionada;
	
	public VerificaIndicadoresConvenioInternacaoVO() {
	}

	public VerificaIndicadoresConvenioInternacaoVO(Integer phiSeq,
			Boolean indExigeJustificativa, Boolean indImprimeLaudo,
			Boolean indCobrancaFracionada) {
		this.phiSeq = phiSeq;
		this.indExigeJustificativa = indExigeJustificativa;
		this.indImprimeLaudo = indImprimeLaudo;
		this.indCobrancaFracionada = indCobrancaFracionada;
	}

	public Integer getPhiSeq() {
		return this.phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public Boolean getIndExigeJustificativa() {
		return this.indExigeJustificativa;
	}

	public void setIndExigeJustificativa(Boolean indExigeJustificativa) {
		this.indExigeJustificativa = indExigeJustificativa;
	}

	public Boolean getIndImprimeLaudo() {
		return this.indImprimeLaudo;
	}

	public void setIndImprimeLaudo(Boolean indImprimeLaudo) {
		this.indImprimeLaudo = indImprimeLaudo;
	}

	public Boolean getIndCobrancaFracionada() {
		return this.indCobrancaFracionada;
	}

	public void setIndCobrancaFracionada(Boolean indCobrancaFracionada) {
		this.indCobrancaFracionada = indCobrancaFracionada;
	}
	
}

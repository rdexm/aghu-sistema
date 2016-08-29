package br.gov.mec.aghu.bancosangue.vo;

import java.io.Serializable;
import java.util.Date;


public class AtualizaMotivoCancelamentoColetaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6852374017698716965L;

	private Integer matriculaCancelamentoColeta;

	private Short vinCodigoCancelamentoColeta;

	private Date dthrCancelamentoColeta;

	public AtualizaMotivoCancelamentoColetaVO() {
	}

	public AtualizaMotivoCancelamentoColetaVO(
			Integer matriculaCancelamentoColeta,
			Short vinCodigoCancelamentoColeta, Date dthrCancelamentoColeta) {
		this.matriculaCancelamentoColeta = matriculaCancelamentoColeta;
		this.vinCodigoCancelamentoColeta = vinCodigoCancelamentoColeta;
		this.dthrCancelamentoColeta = dthrCancelamentoColeta;
	}

	public Integer getMatriculaCancelamentoColeta() {
		return matriculaCancelamentoColeta;
	}

	public void setMatriculaCancelamentoColeta(
			Integer matriculaCancelamentoColeta) {
		this.matriculaCancelamentoColeta = matriculaCancelamentoColeta;
	}

	public Short getVinCodigoCancelamentoColeta() {
		return vinCodigoCancelamentoColeta;
	}

	public void setVinCodigoCancelamentoColeta(Short vinCodigoCancelamentoColeta) {
		this.vinCodigoCancelamentoColeta = vinCodigoCancelamentoColeta;
	}

	public Date getDthrCancelamentoColeta() {
		return dthrCancelamentoColeta;
	}

	public void setDthrCancelamentoColeta(Date dthrCancelamentoColeta) {
		this.dthrCancelamentoColeta = dthrCancelamentoColeta;
	}

}

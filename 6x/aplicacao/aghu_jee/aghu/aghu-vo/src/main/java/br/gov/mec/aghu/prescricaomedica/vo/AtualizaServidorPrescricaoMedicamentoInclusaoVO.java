package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;


public class AtualizaServidorPrescricaoMedicamentoInclusaoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1441562537238529756L;

	private Short vinCodigo;

	private Integer matricula;

	public AtualizaServidorPrescricaoMedicamentoInclusaoVO() {
	}

	public AtualizaServidorPrescricaoMedicamentoInclusaoVO(Short vinCodigo,
			Integer matricula) {
		this.vinCodigo = vinCodigo;
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

}
